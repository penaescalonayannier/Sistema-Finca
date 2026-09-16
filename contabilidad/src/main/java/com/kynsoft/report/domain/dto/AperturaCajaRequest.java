package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Convierte el efectivo histórico registrado sin billetes en efectivo físico
 * controlado. No altera el saldo monetario de caja: solamente lo desglosa.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AperturaCajaRequest {
    private UUID fincaId;
    private LocalDateTime fecha;
    private String observaciones;
    private List<DenominacionCajaDto> denominaciones;
}
