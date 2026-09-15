package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO para Línea de Asiento Contable.
 * Representa una línea de débito o crédito en un asiento.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineaAsientoDto {

    private UUID id;
    private UUID asientoId;

    private String codigoCuenta;
    private String nombreCuenta;
    private String centroCosto;

    private BigDecimal debe;
    private BigDecimal haber;

    private String concepto;
    private Integer orden;
}
