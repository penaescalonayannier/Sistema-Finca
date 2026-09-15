package com.kynsoft.report.controller;

import com.kynsoft.report.applications.command.auth.LoginRequest;
import com.kynsoft.report.applications.command.auth.LoginResponse;
import com.kynsoft.report.applications.command.auth.RegisterRequest;
import com.kynsoft.report.domain.dto.Rol;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.dto.UsuarioDto;
import com.kynsoft.report.domain.services.IAuditoriaService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import com.kynsoft.report.domain.services.IUsuarioService;
import com.kynsoft.report.infrastructure.entity.Usuario;
import com.kynsoft.report.infrastructure.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final IUsuarioService usuarioService;
    private final ITrabajadorService trabajadorService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final IAuditoriaService auditoriaService;

    public AuthController(
            AuthenticationManager authenticationManager,
            IUsuarioService usuarioService,
            ITrabajadorService trabajadorService,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            IAuditoriaService auditoriaService) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.trabajadorService = trabajadorService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            Usuario usuario = (Usuario) authentication.getPrincipal();
            String token = jwtService.generateToken(usuario);

            // Update last login
            usuarioService.updateLastLogin(usuario.getId());

            // Build response
            UsuarioDto usuarioDto = usuarioService.findById(usuario.getId());

            // Audit login
            String ip = getClientIp(httpRequest);
            auditoriaService.registrar(
                    usuario.getId(),
                    usuario.getUsername(),
                    TipoAccion.LOGIN,
                    "Usuario",
                    usuario.getId(),
                    "Inicio de sesión exitoso",
                    null,
                    null,
                    ip
            );

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .usuarioId(usuarioDto.getId())
                    .username(usuarioDto.getUsername())
                    .rol(usuarioDto.getRol())
                    .trabajadorId(usuarioDto.getTrabajadorId())
                    .trabajadorNombre(usuarioDto.getTrabajadorNombre())
                    .fincaId(usuarioDto.getFincaId())
                    .fincaName(usuarioDto.getFincaName())
                    .expiresIn(jwtService.getExpirationTime())
                    .build();

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error de autenticación: " + e.getMessage()));
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Validate username is unique
            if (usuarioService.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El nombre de usuario ya existe"));
            }

            // Validate trabajador exists
            TrabajadorDto trabajador;
            try {
                trabajador = trabajadorService.findById(request.getTrabajadorId());
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El trabajador no existe"));
            }

            // Validate trabajador doesn't have a user already
            if (usuarioService.existsByTrabajadorId(request.getTrabajadorId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El trabajador ya tiene un usuario asignado"));
            }

            // Create user
            UsuarioDto usuarioDto = UsuarioDto.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .rol(request.getRol() != null ? request.getRol() : Rol.USER)
                    .activo(true)
                    .trabajadorId(request.getTrabajadorId())
                    .build();

            UsuarioDto created = usuarioService.create(usuarioDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar usuario: " + e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        // If we reach here, the token is valid (checked by filter)
        return ResponseEntity.ok(Map.of("valid", true));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
        }

        Usuario usuario = (Usuario) authentication.getPrincipal();
        UsuarioDto usuarioDto = usuarioService.findById(usuario.getId());

        return ResponseEntity.ok(usuarioDto);
    }
}
