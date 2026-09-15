package com.kynsoft.report.domain.dto;

public enum TipoMovimientoStock {
    // Stock inicial
    STOCK_INICIAL,

    // Entradas
    ENTRADA_PRODUCCION,
    ENTRADA_FACTURA,
    ENTRADA_CONDUCE,
    ENTRADA_AJUSTE,
    TRANSFERENCIA_ENTRADA,

    // Salidas
    SALIDA_VENTA,
    SALIDA_AUTOCONSUMO,
    SALIDA_COMEDOR,
    SALIDA_AJUSTE,
    TRANSFERENCIA_SALIDA,

    // Ajustes
    AJUSTE_MANUAL,
    AJUSTE_EDICION,

    // Reversiones
    DEVOLUCION,
    REVERSION_PRODUCCION,
    REVERSION_SALIDA;

    public boolean isEntrada() {
        return this == STOCK_INICIAL ||
               this == ENTRADA_PRODUCCION ||
               this == ENTRADA_FACTURA ||
               this == ENTRADA_CONDUCE ||
               this == ENTRADA_AJUSTE ||
               this == TRANSFERENCIA_ENTRADA ||
               this == REVERSION_SALIDA;
    }

    public boolean isSalida() {
        return this == SALIDA_VENTA ||
               this == SALIDA_AUTOCONSUMO ||
               this == SALIDA_COMEDOR ||
               this == SALIDA_AJUSTE ||
               this == TRANSFERENCIA_SALIDA ||
               this == REVERSION_PRODUCCION ||
               this == DEVOLUCION ||
               this == AJUSTE_EDICION;
    }

    public boolean isAjuste() {
        return this == ENTRADA_AJUSTE ||
               this == SALIDA_AJUSTE ||
               this == AJUSTE_MANUAL ||
               this == AJUSTE_EDICION;
    }
}
