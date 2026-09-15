-- Permite controlar existencias fraccionarias (hasta cuatro decimales) en finca,
-- almacén y su trazabilidad de movimientos.
ALTER TABLE finca_producto
    ALTER COLUMN stock TYPE NUMERIC(19,4) USING stock::NUMERIC(19,4),
    ALTER COLUMN stock_minimo TYPE NUMERIC(19,4) USING stock_minimo::NUMERIC(19,4),
    ALTER COLUMN stock_maximo TYPE NUMERIC(19,4) USING stock_maximo::NUMERIC(19,4);

ALTER TABLE almacen_finca_producto
    ALTER COLUMN stock TYPE NUMERIC(19,4) USING stock::NUMERIC(19,4),
    ALTER COLUMN stock_minimo TYPE NUMERIC(19,4) USING stock_minimo::NUMERIC(19,4),
    ALTER COLUMN stock_maximo TYPE NUMERIC(19,4) USING stock_maximo::NUMERIC(19,4);

ALTER TABLE movimiento_stock
    ALTER COLUMN cantidad TYPE NUMERIC(19,4) USING cantidad::NUMERIC(19,4),
    ALTER COLUMN stock_anterior TYPE NUMERIC(19,4) USING stock_anterior::NUMERIC(19,4),
    ALTER COLUMN stock_nuevo TYPE NUMERIC(19,4) USING stock_nuevo::NUMERIC(19,4);
