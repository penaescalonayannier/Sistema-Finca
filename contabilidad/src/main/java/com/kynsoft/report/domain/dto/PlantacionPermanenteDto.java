package com.kynsoft.report.domain.dto;

import com.kynsoft.report.domain.dto.enums.TipoCepa;
import com.kynsoft.report.domain.dto.enums.TipoPlantacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para Plantación Permanente (Grupos 12 y 13 de AFT).
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP): Grupos 12 y 13 - Plantaciones
 * - Estructura de las Cepas (Características Generales UBPC)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantacionPermanenteDto {

    private UUID id;
    private String numeroInventario;
    private TipoPlantacion tipoPlantacion;
    private Integer bloque;
    private Integer campo;
    private BigDecimal areaHectareas;
    private TipoCepa tipoCepa;
    private String codigoVariedad;
    private Integer aniosCepa;
    private UUID fincaId;
    private String fincaNombre;
    private BigDecimal valorAdquisicion;
    private BigDecimal depreciacionAcumulada;
    private BigDecimal valorResidual;
    private BigDecimal valorTasacion;
    private String destino;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Obtiene la ubicación formateada (Bloque-Campo).
     */
    public String getUbicacion() {
        if (bloque == null && campo == null) return null;
        StringBuilder sb = new StringBuilder();
        if (bloque != null) sb.append("B").append(bloque);
        if (campo != null) {
            if (sb.length() > 0) sb.append("-");
            sb.append("C").append(campo);
        }
        return sb.toString();
    }

    /**
     * Obtiene el código de cepa legible.
     */
    public String getTipoCepaCodigo() {
        return tipoCepa != null ? tipoCepa.getCodigo() : null;
    }

    /**
     * Obtiene el nombre del tipo de plantación.
     */
    public String getTipoPlantacionNombre() {
        return tipoPlantacion != null ? tipoPlantacion.getNombre() : null;
    }
}
