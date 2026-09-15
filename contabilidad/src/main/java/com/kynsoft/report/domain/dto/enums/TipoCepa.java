package com.kynsoft.report.domain.dto.enums;

/**
 * Tipos de cepa para plantaciones de caña según terminología agrícola cubana.
 * Referencia: Estructura de las Cepas - Características Generales de UBPC.
 *
 * La cepa indica el origen y edad de la plantación:
 * - Siembra: Plantación nueva del año indicado
 * - Retoño: Rebrote de plantación existente
 * - Primavera: Siembra de temporada primaveral
 * - Frío: Siembra de temporada invernal
 * - Quedada (Q): Plantación que permanece del ciclo anterior
 */
public enum TipoCepa {
    SIEMBRA_QUEDADA("S/Q", "Siembra Quedada", "Siembra que permanece del ciclo anterior"),
    RETONO_QUEDADO("R/Q", "Retoño Quedado", "Retoño que permanece del ciclo anterior"),
    RETONO_REBROTE_QUEDADO("R/RQ", "Retoño/Rebrote Quedado", "Retoño con rebrote del ciclo anterior"),
    PRIMAVERA_QUEDADA("P/Q", "Primavera Quedada", "Siembra primaveral que permanece"),
    FRIO_QUEDADO("F/Q", "Frío Quedado", "Siembra de frío que permanece"),
    SIEMBRA_2025("S/2025", "Siembra 2025", "Siembra nueva del año 2025"),
    RETONO_2025("R/2025", "Retoño 2025", "Retoño del año 2025"),
    PRIMAVERA_2025("P/2025", "Primavera 2025", "Siembra primaveral 2025");

    private final String codigo;
    private final String nombre;
    private final String descripcion;

    TipoCepa(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Busca el tipo de cepa por código.
     */
    public static TipoCepa fromCodigo(String codigo) {
        if (codigo == null) return null;
        for (TipoCepa cepa : values()) {
            if (cepa.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return cepa;
            }
        }
        return null;
    }
}
