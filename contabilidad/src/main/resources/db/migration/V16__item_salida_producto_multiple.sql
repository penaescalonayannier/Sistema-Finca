-- Permite que un mismo vale contenga líneas de productos diferentes.
-- Los campos son opcionales para mantener íntegros los vales históricos.
ALTER TABLE item_salida ADD COLUMN IF NOT EXISTS finca_producto_id UUID;
ALTER TABLE item_salida ADD COLUMN IF NOT EXISTS almacen_finca_producto_id UUID;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_item_salida_finca_producto') THEN
        ALTER TABLE item_salida
            ADD CONSTRAINT fk_item_salida_finca_producto
            FOREIGN KEY (finca_producto_id) REFERENCES finca_producto(id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_item_salida_almacen_finca_producto') THEN
        ALTER TABLE item_salida
            ADD CONSTRAINT fk_item_salida_almacen_finca_producto
            FOREIGN KEY (almacen_finca_producto_id) REFERENCES almacen_finca_producto(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_item_salida_finca_producto ON item_salida(finca_producto_id);
CREATE INDEX IF NOT EXISTS idx_item_salida_almacen_finca_producto ON item_salida(almacen_finca_producto_id);
