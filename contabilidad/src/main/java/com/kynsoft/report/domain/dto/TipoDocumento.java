package com.kynsoft.report.domain.dto;

public enum TipoDocumento {
    VALE("VALE"),
    FACTURA("FAC"),
    RECIBO("REC"),
    PRODUCCION("PT"),
    /** Informe de recepción de almacén, modelo SC-2-04. */
    RECEPCION("IR"),
    /** Modelo SC-2-09: transferencia entre almacenes. */
    TRANSFERENCIA_ALMACEN("SC2-09"),
    /** Modelo SC-2-15: expediente de inventario físico por almacén. */
    CONTEO_FISICO("SC2-15"),
    /** Modelo SC-2-16: ajuste derivado de conteo físico cerrado. */
    AJUSTE_INVENTARIO("SC2-16");

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
