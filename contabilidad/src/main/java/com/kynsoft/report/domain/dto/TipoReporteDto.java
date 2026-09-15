package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO para Tipo de Reporte.
 * Determina el centro de costo contable y qué subclasificación usar.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TipoReporteDto {
    private UUID id;

    /**
     * Código único: REPORTE_CANNA, REPORTE_PLAN_VIANDA, REPORTE_VAQUERIA, etc.
     */
    private String codigo;

    /**
     * Nombre para mostrar: "Reporte de Caña", "Plan Vianda", etc.
     */
    private String nombre;

    private String descripcion;

    /**
     * Código del centro de costo contable asociado
     */
    private String codigoCentroCosto;

    /**
     * Tipo de subclasificación: CULTIVO, ANIMAL, NINGUNO
     */
    private TipoSubclasificacion tipoSubclasificacion;

    /**
     * Categoría de TipoCultivo a filtrar (solo si tipoSubclasificacion = CULTIVO)
     */
    private CategoriaTipoCultivo tipoCultivoCategoriaFiltro;

    /**
     * ID del TipoCultivo a auto-seleccionar (solo si aplica)
     */
    private UUID tipoCultivoAutoId;

    /**
     * Nombre del TipoCultivo auto-seleccionado (solo lectura)
     */
    private String tipoCultivoAutoNombre;

    /**
     * Indica si requiere Bloque y Campo
     */
    private Boolean requiereCampo;

    private Boolean activo;

    private Integer orden;
}
