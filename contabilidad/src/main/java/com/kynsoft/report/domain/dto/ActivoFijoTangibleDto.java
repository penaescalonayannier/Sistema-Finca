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
 * DTO para Activo Fijo Tangible.
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP): Activos Fijos Tangibles
 * - Resolución 51/2021 MFP: Tasas de depreciación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivoFijoTangibleDto {

    private UUID id;
    private String numeroInventario;
    private String descripcion;

    // Relación con grupo
    private UUID grupoId;
    private String grupoCodigo;
    private String grupoNombre;

    // Relación con finca
    private UUID fincaId;
    private String fincaNombre;

    // Valores contables según NCC No. 7
    private BigDecimal valorAdquisicion;
    private BigDecimal depreciacionAcumulada;
    private BigDecimal valorResidual;

    // Estado
    private Integer estadoTecnicoPorcentaje;
    private BigDecimal valorTasacion;

    // Fechas
    private LocalDate fechaAdquisicion;
    private LocalDate fechaBaja;

    // Otros
    private String destino;
    private String observaciones;
    private Boolean activo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Calcula el porcentaje de depreciación consumida.
     */
    public BigDecimal getPorcentajeDepreciado() {
        if (valorAdquisicion == null || valorAdquisicion.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (depreciacionAcumulada == null) {
            return BigDecimal.ZERO;
        }
        return depreciacionAcumulada
                .multiply(BigDecimal.valueOf(100))
                .divide(valorAdquisicion, 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Indica si el activo está totalmente depreciado.
     */
    public boolean isTotalmenteDepreciado() {
        if (valorResidual == null) return false;
        return valorResidual.compareTo(BigDecimal.ZERO) <= 0;
    }

    /**
     * Indica si el activo está dado de baja.
     */
    public boolean isDadoDeBaja() {
        return fechaBaja != null;
    }
}
