package com.kynsoft.report.domain.dto;

/**
 * Naturaleza de la cuenta contable
 * - DEUDORA: Aumenta por el DEBE, disminuye por el HABER (Activos, Gastos, Costos)
 * - ACREEDORA: Aumenta por el HABER, disminuye por el DEBE (Pasivos, Patrimonio, Ingresos)
 */
public enum NaturalezaCuenta {
    DEUDORA("Deudora"),
    ACREEDORA("Acreedora");

    private final String descripcion;

    NaturalezaCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
