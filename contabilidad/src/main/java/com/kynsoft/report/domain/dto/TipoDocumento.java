package com.kynsoft.report.domain.dto;

public enum TipoDocumento {
    VALE("VALE"),
    FACTURA("FAC"),
    RECIBO("REC");

    private final String prefijo;

    TipoDocumento(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public static TipoDocumento fromTipoSalida(TipoSalida tipoSalida) {
        return switch (tipoSalida) {
            case VALE -> VALE;
            case FACTURA -> FACTURA;
        };
    }
}
