-- SC-2-06: los datos impresos de la producción terminada deben conservarse
-- aunque posteriormente se modifique el catálogo, el costo o el inventario.
ALTER TABLE produccion_terminada
    ADD COLUMN IF NOT EXISTS numero_documento VARCHAR(30),
    ADD COLUMN IF NOT EXISTS producto_codigo_snapshot VARCHAR(50),
    ADD COLUMN IF NOT EXISTS producto_nombre_snapshot VARCHAR(150),
    ADD COLUMN IF NOT EXISTS unidad_medida_snapshot VARCHAR(30),
    ADD COLUMN IF NOT EXISTS almacen_nombre_snapshot VARCHAR(150),
    ADD COLUMN IF NOT EXISTS almacen_inventario_snapshot VARCHAR(50),
    ADD COLUMN IF NOT EXISTS costo_unitario NUMERIC(19,4),
    ADD COLUMN IF NOT EXISTS importe NUMERIC(19,4),
    ADD COLUMN IF NOT EXISTS saldo_posterior NUMERIC(19,4),
    ADD COLUMN IF NOT EXISTS lote VARCHAR(100),
    ADD COLUMN IF NOT EXISTS centro_costo VARCHAR(50);

CREATE UNIQUE INDEX IF NOT EXISTS uq_produccion_terminada_numero_documento
    ON produccion_terminada (finca_id, numero_documento)
    WHERE numero_documento IS NOT NULL;

COMMENT ON COLUMN produccion_terminada.numero_documento IS 'Consecutivo inmutable del modelo SC-2-06';
COMMENT ON COLUMN produccion_terminada.saldo_posterior IS 'Saldo físico al cerrar el documento, no saldo consultado al imprimir';
