package com.kynsoft.report.domain.dto;

/**
 * Tipos de cuenta según el Nomenclador Cubano (Res. 494/2016 MFP)
 */
public enum TipoCuenta {
    ACTIVO("Activo"),
    PASIVO("Pasivo"),
    PATRIMONIO("Patrimonio"),
    INGRESO("Ingreso"),
    GASTO("Gasto"),
    COSTO("Costo");

    private final String descripcion;

    TipoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
