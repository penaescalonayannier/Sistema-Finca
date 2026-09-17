package com.kynsoft.report.infrastructure.services;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Normaliza las cantidades capturadas en los partes de trabajo sin modificar
 * el contrato público actual (las columnas siguen siendo VARCHAR). El valor
 * almacenado es canónico, por ejemplo {@code " 1,50 " -> "1.5"}.
 */
final class ValidacionParteTrabajo {

    private static final BigDecimal CERO = BigDecimal.ZERO;
    // El flujo vigente permite registrar jornadas extendidas. Ocho horas es una
    // alerta de control en los reportes, no una prohibición de captura.
    private static final BigDecimal MAXIMO_HORAS_DIARIAS = BigDecimal.valueOf(24);
    private static final int ESCALA_MAXIMA = 4;

    private ValidacionParteTrabajo() {
    }

    static String horas(String valor) {
        BigDecimal horas = decimalRequerido(valor, "horas");
        if (horas.compareTo(CERO) < 0 || horas.compareTo(MAXIMO_HORAS_DIARIAS) > 0) {
            throw new IllegalArgumentException("Las horas deben estar entre 0 y 24 por día.");
        }
        return canonico(horas);
    }

    static String normaRequerida(String valor) {
        BigDecimal norma = decimalRequerido(valor, "norma");
        if (norma.compareTo(CERO) < 0) {
            throw new IllegalArgumentException("La norma no puede ser negativa.");
        }
        return canonico(norma);
    }

    static String normaOpcional(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return normaRequerida(valor);
    }

    private static BigDecimal decimalRequerido(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar " + campo + ".");
        }
        String normalizado = valor.trim().replace(',', '.');
        if (!normalizado.matches("[+-]?\\d+(?:\\.\\d+)?")) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser un número decimal válido.");
        }
        try {
            BigDecimal decimal = new BigDecimal(normalizado);
            if (decimal.scale() > ESCALA_MAXIMA) {
                throw new IllegalArgumentException("El campo " + campo + " admite hasta " + ESCALA_MAXIMA + " decimales.");
            }
            return decimal.setScale(Math.max(decimal.scale(), 0), RoundingMode.UNNECESSARY);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser un número decimal válido.", ex);
        }
    }

    private static String canonico(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString();
    }
}
