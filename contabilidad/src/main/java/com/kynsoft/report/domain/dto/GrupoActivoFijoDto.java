package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO para Grupos de Activos Fijos Tangibles.
 *
 * Referencia: NCC No. 7 (Resolución 1038/2017 MFP)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrupoActivoFijoDto {

    private UUID id;
    private String codigo;
    private String nombre;
    private BigDecimal tasaDepreciacion;
    private Integer vidaUtilAnios;
    private String descripcion;
    private Boolean activo;

    /**
     * Calcula la depreciación mensual basada en la tasa anual.
     */
    public BigDecimal getDepreciacionMensual() {
        if (tasaDepreciacion == null) return null;
        return tasaDepreciacion.divide(BigDecimal.valueOf(12), 4, java.math.RoundingMode.HALF_UP);
    }
}
