package com.kynsoft.report.domain.dto.enumerativos;

public enum TipoCargo {
    // Directivos
    PRESIDENTE("PRESIDENTE"),
    J_PRODUCCION_DE_CAÑA("J´ Producción de caña"),
    J_ECONOMICO("J´ Económico"),
    J_PRODUCCION_AGROPECUARIO("J´ Producción Agropecuario"),
    J_RECURSOS_HUMANOS("J´ Recursos Humanos"),
    J_MAQUINARIA("J´ Maquinaria"),
    J_ABASTECIMIENTO("J´ Abastecimiento"),
    CONTADOR("Contador"),

    // Administración
    PROMEDIO_ADMINISTRACION("PROMEDIO ADMINISTRACION"),
    J_LOTES_CAÑEROS("J´ Lotes Cañeros"),
    AUXILIAR_CONTABILIDAD("Auxiliar de Contabilidad"),
    AUXILIAR_RECURSOS_HUMANOS("Auxiliar de Recursos Humanos"),
    SECRETARIO("Secretario"),
    J_PROTECCION_FISICA("J´ Protección Física"),
    TECNICO_INTEGRAL("Técnico Integral"),
    PROGRAMACION_DE_CORTE("Programación de Corte"),
    AUXILIAR_B_ECONOMIA_Y_ALMACENERA("Auxiliar (B) Economía y Almacenera"),

    // Técnicos
    PROMEDIO_TECNICOS("PROMEDIO TECNICOS"),
    COCINERA("Cocinera"),
    AUXILIAR_LIMPIEZA_O_AYUDANTE_COCINA("Auxiliar de Limpieza o Ayudante de Cocina"),
    OPERARIOS_SERVICIO("Operarios Servicio"),
    SERENOS("Serenos"),
    GUARDIA_DIA_OFICINAS("Guardia del día Oficinas"),

    // KTP
    OPERADOR_KTP("Operador de KTP"),
    MECANICO_KTP("Mecánico de KTP"),
    J_PELOTON_KTP("J ´ Pelotón de KTP"),

    // Agrícola
    AGRICOLA_MANUAL_CAÑERO("Agrícola Manual Cañero"),
    AGRICOLA_BOYERO("Agrícola Boyero"),
    OPERADOR_TRACTOR("Operador de Tractor"),
    GUARAPERO("GUARAPERO"),

    // Vaquería
    JEFE_BRIGADA_VAQUERIA("Jefe de brigada Vaquería"),
    PASTOR_VACUNO("Pastor Vacuno"),
    PASTOR_EQUINO_Y_BUEYES_TRABAJO("Pastor Equino y Bueyes de trabajo"),
    ALIMENTADOR("Alimentador"),
    VETERINARIO("Veterinario"),
    INSEMINADOR("Inseminador"),

    // Taller
    MECANICO_TALLER("Mecánico Taller"),
    PISTERO_ELECTRICISTA("Pistero Electricista"),
    PONCHERO("Ponchero"),
    SOLDADOR("Soldador"),

    // Otros
    AUXILIAR_REUBICADO("Auxiliar Reubicado");

    private final String descripcion;

    TipoCargo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}