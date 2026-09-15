package com.kynsoft.report.domain.dto.enums;

/**
 * Tipos de ganado según clasificación agrícola cubana.
 * Referencia: NCC No. 7 - Grupo 08 Animales.
 */
public enum TipoGanado {
    VACUNO("Vacuno", "Ganado bovino"),
    EQUINO("Equino", "Caballos y yeguas"),
    PORCINO("Porcino", "Cerdos"),
    OVINO("Ovino", "Ovejas"),
    CAPRINO("Caprino", "Cabras"),
    AVICOLA("Avícola", "Aves de corral"),
    CUNICOLA("Cunícola", "Conejos");

    private final String nombre;
    private final String descripcion;

    TipoGanado(String nombre, String descripcion) {
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
