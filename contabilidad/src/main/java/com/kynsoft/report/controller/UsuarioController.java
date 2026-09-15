package com.kynsoft.report.controller;

import com.kynsoft.report.applications.command.auth.RegisterRequest;
import com.kynsoft.report.applications.query.responseObject.TrabajadorResponse;
import com.kynsoft.report.domain.dto.Rol;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.dto.UsuarioDto;
import com.kynsoft.report.domain.services.IAuditoriaService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import com.kynsoft.report.domain.services.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final IUsuarioService usuarioService;
    private final ITrabajadorService trabajadorService;
    private final PasswordEncoder passwordEncoder;
    private final IAuditoriaService auditoriaService;

    public UsuarioController(
            IUsuarioService usuarioService,
            ITrabajadorService trabajadorService,
            PasswordEncoder passwordEncoder,
            IAuditoriaService auditoriaService) {
        this.usuarioService = usuarioService;
        this.trabajadorService = trabajadorService;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> getAll() {
        List<UsuarioDto> usuarios = usuarioService.findAll();
        // Don't return passwords
        usuarios.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> getById(@PathVariable UUID id) {
        UsuarioDto usuario = usuarioService.findById(id);
        usuario.setPassword(null);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/finca/{fincaId}")
    public ResponseEntity<List<UsuarioDto>> getByFinca(@PathVariable UUID fincaId) {
        List<UsuarioDto> usuarios = usuarioService.findByFincaId(fincaId);
        usuarios.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody RegisterRequest request) {
        try {
            // Validate username is unique
            if (usuarioService.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El nombre de usuario ya existe"));
            }

            // Validate trabajador exists
            try {
                trabajadorService.findById(request.getTrabajadorId());
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
            created.setPassword(null);

            // Audit
            auditoriaService.registrar(TipoAccion.CREATE, "Usuario", created.getId(),
                    "Creación de usuario: " + created.getUsername(), null, created);

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear usuario: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        try {
            UsuarioDto existing = usuarioService.findById(id);
            UsuarioDto beforeUpdate = UsuarioDto.builder()
                    .username(existing.getUsername())
                    .rol(existing.getRol())
                    .activo(existing.getActivo())
                    .build();

            if (updates.containsKey("username")) {
                String newUsername = (String) updates.get("username");
                if (!newUsername.equals(existing.getUsername()) &&
                        usuarioService.existsByUsername(newUsername)) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "El nombre de usuario ya existe"));
                }
                existing.setUsername(newUsername);
            }

            if (updates.containsKey("rol")) {
                existing.setRol(Rol.valueOf((String) updates.get("rol")));
            }

            if (updates.containsKey("activo")) {
                existing.setActivo((Boolean) updates.get("activo"));
            }

            UsuarioDto updated = usuarioService.update(existing);
            updated.setPassword(null);

            // Audit
            auditoriaService.registrar(TipoAccion.UPDATE, "Usuario", id,
                    "Actualización de usuario: " + updated.getUsername(), beforeUpdate, updates);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar usuario: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        try {
            String newPassword = body.get("newPassword");
            if (newPassword == null || newPassword.length() < 4) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "La contraseña debe tener al menos 4 caracteres"));
            }

            UsuarioDto existing = usuarioService.findById(id);
            existing.setPassword(passwordEncoder.encode(newPassword));
            usuarioService.update(existing);

            // Audit
            auditoriaService.registrar(TipoAccion.UPDATE, "Usuario", id,
                    "Cambio de contraseña para usuario: " + existing.getUsername(), null, null);

            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cambiar contraseña: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            UsuarioDto existing = usuarioService.findById(id);
            usuarioService.delete(id);

            // Audit
            auditoriaService.registrar(TipoAccion.DELETE, "Usuario", id,
                    "Desactivación de usuario: " + existing.getUsername(), existing, null);

            return ResponseEntity.ok(Map.of("message", "Usuario desactivado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al desactivar usuario: " + e.getMessage()));
        }
    }

    @GetMapping("/trabajadores-disponibles")
    public ResponseEntity<List<TrabajadorResponse>> getTrabajadoresDisponibles() {
        // Get all active trabajadores with relations loaded
        List<TrabajadorResponse> disponibles = trabajadorService.findAllActivos().stream()
                .filter(t -> !usuarioService.existsByTrabajadorId(t.getId()))
                .map(TrabajadorResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(disponibles);
    }
}
