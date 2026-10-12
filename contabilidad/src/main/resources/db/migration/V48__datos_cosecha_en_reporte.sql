ALTER TABLE reporte
    ADD COLUMN IF NOT EXISTS producto_id UUID,
    ADD COLUMN IF NOT EXISTS cantidad_cosechada NUMERIC(19,4),
    ADD COLUMN IF NOT EXISTS unidad_medida_snapshot VARCHAR(30),
    ADD COLUMN IF NOT EXISTS precio_unitario_referencia NUMERIC(19,4),
    ADD COLUMN IF NOT EXISTS tipo_precio_referencia VARCHAR(30);

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_reporte_producto') THEN
    ALTER TABLE reporte ADD CONSTRAINT fk_reporte_producto FOREIGN KEY (producto_id) REFERENCES productos(id);
  END IF;
END $$;
