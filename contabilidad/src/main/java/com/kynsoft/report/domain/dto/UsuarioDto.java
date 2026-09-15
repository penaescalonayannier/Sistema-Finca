package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {
    private UUID id;
    private String username;
    private String password;
    private Rol rol;
    private Boolean activo;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private String trabajadorRuc;
    private UUID fincaId;
    private String fincaName;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
