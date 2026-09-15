package com.kynsoft.report.applications.command.auth;

import com.kynsoft.report.domain.dto.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String type;
    private UUID usuarioId;
    private String username;
    private Rol rol;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private UUID fincaId;
    private String fincaName;
    private long expiresIn;
}
