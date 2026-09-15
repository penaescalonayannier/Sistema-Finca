package com.kynsoft.report.domain.dto;

public enum EstadoStock {
    CRITICO,   // stock = 0
    BAJO,      // 0 < stock < stockMinimo
    NORMAL,    // stockMinimo <= stock <= stockMaximo (or no max)
    EXCESO;    // stock > stockMaximo (optional)

    public String getColor() {
        return switch (this) {
            case CRITICO -> "#c62828";
            case BAJO -> "#e65100";
            case NORMAL -> "#2e7d32";
            case EXCESO -> "#1565c0";
        };
    }

    public String getBgColor() {
        return switch (this) {
            case CRITICO -> "#ffebee";
            case BAJO -> "#fff3e0";
            case NORMAL -> "#e8f5e9";
            case EXCESO -> "#e3f2fd";
        };
    }
}
