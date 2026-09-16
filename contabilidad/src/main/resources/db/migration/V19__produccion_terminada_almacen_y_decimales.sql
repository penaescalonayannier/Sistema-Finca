-- Una producción terminada puede originarse desde una entrada física de almacén.
-- La relación permite ajustar o anular el documento en el mismo inventario y
-- cantidad_terminada conserva unidades fraccionarias.
ALTER TABLE produccion_terminada
    ADD COLUMN IF NOT EXISTS almacen_finca_producto_id UUID;

ALTER TABLE produccion_terminada
    ALTER COLUMN cantidad_terminada TYPE NUMERIC(19,4)
    USING cantidad_terminada::NUMERIC(19,4);

CREATE INDEX IF NOT EXISTS idx_produccion_terminada_almacen_finca_producto
    ON produccion_terminada (almacen_finca_producto_id);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_produccion_terminada_almacen_finca_producto'
    ) THEN
        ALTER TABLE produccion_terminada
            ADD CONSTRAINT fk_produccion_terminada_almacen_finca_producto
            FOREIGN KEY (almacen_finca_producto_id)
            REFERENCES almacen_finca_producto (id);
    END IF;
END $$;
