package com.kynsoft.report.domain.dto;

/**
 * Estados del ciclo documental de una evaluación de desempeño.
 * Los registros históricos sin estado se interpretan como BORRADOR.
 */
public enum EstadoEvaluacion {
    BORRADOR,
    ENVIADA,
    CERRADA,
    ANULADA
}
