package com.kynsoft.report.infrastructure.security;

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
public class TenantInfo {
    private UUID usuarioId;
    private UUID fincaId;
    private UUID trabajadorId;
    private Rol rol;
    private UUID fincaSeleccionada; // For ADMIN filtering by specific finca
}
