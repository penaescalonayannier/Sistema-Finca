package com.kynsoft.report.infrastructure.config;

import com.kynsoft.report.domain.dto.Rol;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantInfo;
import com.kynsoft.report.infrastructure.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // Set TenantContext from JWT claims
                    setTenantContext(jwt, request);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private void setTenantContext(String jwt, HttpServletRequest request) {
        try {
            String usuarioIdStr = jwtService.extractUsuarioId(jwt);
            String fincaIdStr = jwtService.extractFincaId(jwt);
            String rolStr = jwtService.extractRol(jwt);
            String trabajadorIdStr = jwtService.extractTrabajadorId(jwt);

            TenantInfo.TenantInfoBuilder builder = TenantInfo.builder();

            if (usuarioIdStr != null) {
                builder.usuarioId(UUID.fromString(usuarioIdStr));
            }
            if (fincaIdStr != null) {
                builder.fincaId(UUID.fromString(fincaIdStr));
            }
            if (rolStr != null) {
                builder.rol(Rol.valueOf(rolStr));
            }
            if (trabajadorIdStr != null) {
                builder.trabajadorId(UUID.fromString(trabajadorIdStr));
            }

            TenantInfo info = builder.build();

            // Handle X-Finca-Id header for ADMIN users
            String fincaSeleccionada = request.getHeader("X-Finca-Id");
            if (fincaSeleccionada != null && !fincaSeleccionada.isEmpty() && info.getRol() == Rol.ADMIN) {
                try {
                    info.setFincaSeleccionada(UUID.fromString(fincaSeleccionada));
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid X-Finca-Id header: " + fincaSeleccionada);
                }
            }

            TenantContext.set(info);
        } catch (Exception e) {
            logger.warn("Error setting tenant context: " + e.getMessage());
        }
    }
}
