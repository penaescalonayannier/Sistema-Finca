package com.kynsoft.report.domain.dto;

/**
 * Tipo de subclasificación para reportes.
 * Determina qué nomenclador secundario usar en el reporte.
 */
public enum TipoSubclasificacion {
    /**
     * Usa TipoCultivo como subclasificación (para CANNA, PLAN_VIANDA)
     */
    CULTIVO,

    /**
     * Usa TipoAnimal como subclasificación (para VAQUERIA)
     */
    ANIMAL,

    /**
     * No requiere subclasificación (TALLER, DIRECCION, SERVICIO)
     */
    NINGUNO
}
