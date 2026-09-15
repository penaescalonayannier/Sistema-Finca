package com.kynsoft.report.domain.dto;

/**
 * Categoría de tipo de cultivo.
 * Permite agrupar cultivos para filtrado en tipos de reporte.
 */
public enum CategoriaTipoCultivo {
    /**
     * Caña de azúcar - requiere bloque y campo
     */
    CANNA,

    /**
     * Cultivos de vianda/alimentos (Yuca, Maíz, Boniato, etc.)
     */
    VIANDA,

    /**
     * Otros cultivos
     */
    OTRO
}
