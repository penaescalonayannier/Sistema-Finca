package com.kynsoft.report.domain.dto.enums;

/**
 * Tipos de plantaciones permanentes según clasificación agrícola cubana.
 * Referencia: NCC No. 7 - Grupo 12 y 13 Plantaciones.
 */
public enum TipoPlantacion {
    CANA("Caña", "Caña de azúcar"),
    PLATANO("Plátano", "Plátano burro y otras variedades"),
    MANGO("Mango", "Árboles de mango"),
    GUAYABA("Guayaba", "Árboles de guayaba"),
    CITRICOS("Cítricos", "Naranjas, limones, toronjas"),
    CAFE("Café", "Plantaciones de café"),
    CACAO("Cacao", "Plantaciones de cacao"),
    OTROS_FRUTALES("Otros Frutales", "Otros árboles frutales");

    private final String nombre;
    private final String descripcion;

    TipoPlantacion(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
