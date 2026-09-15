package com.kynsoft.report.domain.dto.enums;

/**
 * Categorías de animales según clasificación ganadera cubana.
 * Referencia: Certificación de Tenencia de Ganado Mayor - Delegación Agricultura Municipal.
 */
public enum CategoriaAnimal {
    // Machos vacunos
    TERNERO("Ternero", "Macho menor de 1 año"),
    ANOJO("Añojo", "Macho de 1 a 2 años"),
    TORETE("Torete", "Macho de 2 a 3 años"),
    BUEY("Buey", "Macho castrado para trabajo"),
    SEMENTAL("Semental", "Macho reproductor"),
    TORO("Toro", "Macho reproductor adulto"),

    // Hembras vacunas
    TERNERA("Ternera", "Hembra menor de 1 año"),
    ANOJA("Añoja", "Hembra de 1 a 2 años"),
    NOVILLA("Novilla", "Hembra de 2 a 3 años sin parir"),
    VACA("Vaca", "Hembra adulta"),

    // Equinos
    CABALLO("Caballo de Trabajo", "Equino macho para trabajo"),
    YEGUA("Yegua de Trabajo", "Equino hembra para trabajo"),
    POTRO("Potro", "Equino joven"),
    MULO("Mulo/Mula", "Híbrido equino"),

    // Porcinos
    CERDO("Cerdo", "Porcino"),

    // Ovinos
    OVEJA("Oveja", "Ovino"),

    // Caprinos
    CABRA("Cabra", "Caprino"),

    // Crías
    CRIA_MACHO("Cría Macho", "Cría macho sin clasificar"),
    CRIA_HEMBRA("Cría Hembra", "Cría hembra sin clasificar"),

    // Otros
    OTRO("Otro", "Categoría no clasificada");

    private final String nombre;
    private final String descripcion;

    CategoriaAnimal(String nombre, String descripcion) {
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
