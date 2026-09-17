package com.kynsoft.report.infrastructure.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidacionParteTrabajoTest {

    @Test
    void normalizaHorasConComaYEspacios() {
        assertEquals("1.5", ValidacionParteTrabajo.horas(" 1,5000 "));
    }

    @Test
    void rechazaHorasNegativasOMayoresDeVeinticuatroYConservaJornadasExtendidas() {
        assertThrows(IllegalArgumentException.class, () -> ValidacionParteTrabajo.horas("-0.1"));
        assertEquals("8.5", ValidacionParteTrabajo.horas("8.5"));
        assertThrows(IllegalArgumentException.class, () -> ValidacionParteTrabajo.horas("24.0001"));
    }

    @Test
    void rechazaHorasNoNumericasYPrecisionExcesiva() {
        assertThrows(IllegalArgumentException.class, () -> ValidacionParteTrabajo.horas("ocho"));
        assertThrows(IllegalArgumentException.class, () -> ValidacionParteTrabajo.horas("1.12345"));
    }

    @Test
    void normalizaNormaYConservaNulaCuandoEsOpcional() {
        assertEquals("12.25", ValidacionParteTrabajo.normaRequerida(" 12,2500 "));
        assertNull(ValidacionParteTrabajo.normaOpcional("  "));
    }

    @Test
    void rechazaNormaNegativaOVaciaCuandoEsObligatoria() {
        assertThrows(IllegalArgumentException.class, () -> ValidacionParteTrabajo.normaRequerida("-1"));
        assertThrows(IllegalArgumentException.class, () -> ValidacionParteTrabajo.normaRequerida(""));
    }
}
