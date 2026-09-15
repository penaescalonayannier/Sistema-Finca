package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para Movimiento de Depreciación.
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP): Cálculo de depreciación
 * - Resolución 51/2021 MFP: Tasas máximas de depreciación
 * - Resolución 60/2011 CGR: Control interno
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDepreciacionDto {

    private UUID id;

    // Referencia al activo
    private UUID activoFijoId;
    private String numeroInventario;
    private String descripcionActivo;

    // Período
    private LocalDate fecha;
    private Integer mes;
    private Integer anio;

    // Valores de depreciación
    private BigDecimal montoDepreciacion;
    private BigDecimal depreciacionAcumuladaAnterior;
    private BigDecimal depreciacionAcumuladaNueva;
    private BigDecimal valorResidualResultante;
    private BigDecimal tasaAplicada;

    private String observacion;
    private LocalDateTime createdAt;

    /**
     * Obtiene el período formateado (MM/YYYY).
     */
    public String getPeriodo() {
        if (mes == null || anio == null) return null;
        return String.format("%02d/%d", mes, anio);
    }
}
