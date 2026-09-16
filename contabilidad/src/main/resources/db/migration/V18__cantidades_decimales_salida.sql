ALTER TABLE item_salida
    ALTER COLUMN cantidad TYPE NUMERIC(19,4) USING cantidad::NUMERIC(19,4);

ALTER TABLE deuda_trabajador_detalle
    ALTER COLUMN cantidad TYPE NUMERIC(19,4) USING cantidad::NUMERIC(19,4);
