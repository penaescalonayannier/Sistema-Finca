package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** Cambio físico de billetes en caja. No modifica el importe total disponible. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CambioDenominacionesCajaRequest {
    private UUID fincaId;
    private LocalDateTime fecha;
    private String entregadoPor;
    private String recibidoPor;
    private String observaciones;
    private List<DenominacionCajaDto> denominacionesEntregadas;
    private List<DenominacionCajaDto> denominacionesRecibidas;
}
