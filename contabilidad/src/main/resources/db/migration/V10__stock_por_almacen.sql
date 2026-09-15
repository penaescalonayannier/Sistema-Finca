-- V10: Stock por Almacén
-- Migra el control de stock desde nivel de finca a nivel de almacén
-- Cada almacén mantiene su propio stock por producto

-- 1. Agregar columnas de stock y metadata a la tabla join
ALTER TABLE almacen_finca_producto
ADD COLUMN IF NOT EXISTS id UUID,
ADD COLUMN IF NOT EXISTS stock INTEGER NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS stock_minimo INTEGER DEFAULT 0,
ADD COLUMN IF NOT EXISTS stock_maximo INTEGER,
ADD COLUMN IF NOT EXISTS activo BOOLEAN NOT NULL DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- 2. Generar UUIDs para registros existentes
UPDATE almacen_finca_producto SET id = gen_random_uuid() WHERE id IS NULL;

-- 3. Hacer id NOT NULL después de asignar valores
ALTER TABLE almacen_finca_producto ALTER COLUMN id SET NOT NULL;

-- 4. Eliminar la primary key compuesta existente si existe
ALTER TABLE almacen_finca_producto DROP CONSTRAINT IF EXISTS almacen_finca_producto_pkey;

-- 5. Agregar id como PRIMARY KEY
ALTER TABLE almacen_finca_producto ADD PRIMARY KEY (id);

-- 6. Crear constraint UNIQUE para la combinación almacen_id + finca_producto_id
ALTER TABLE almacen_finca_producto
ADD CONSTRAINT uk_almacen_finca_producto UNIQUE (almacen_id, finca_producto_id);

-- 7. Índices para búsquedas frecuentes
CREATE INDEX IF NOT EXISTS idx_almacen_fp_almacen ON almacen_finca_producto(almacen_id);
CREATE INDEX IF NOT EXISTS idx_almacen_fp_finca_producto ON almacen_finca_producto(finca_producto_id);
CREATE INDEX IF NOT EXISTS idx_almacen_fp_activo ON almacen_finca_producto(activo);

-- 8. Agregar tipos de movimiento para transferencias
DO $$
BEGIN
    -- Verificar si ya existen los tipos de transferencia en movimiento_stock
    -- (asumiendo que TipoMovimientoStock es un enum en Java, no necesita cambios en BD)
END $$;

-- 9. Agregar campo almacen_id a movimiento_stock si no existe (para auditoría)
ALTER TABLE movimiento_stock
ADD COLUMN IF NOT EXISTS almacen_id UUID REFERENCES almacenes(id);

-- 10. Agregar campo almacen_destino_id para transferencias
ALTER TABLE movimiento_stock
ADD COLUMN IF NOT EXISTS almacen_destino_id UUID REFERENCES almacenes(id);

-- 11. Índice para movimientos por almacén
CREATE INDEX IF NOT EXISTS idx_movimiento_stock_almacen ON movimiento_stock(almacen_id);
CREATE INDEX IF NOT EXISTS idx_movimiento_stock_almacen_destino ON movimiento_stock(almacen_destino_id);

COMMENT ON COLUMN almacen_finca_producto.stock IS 'Stock actual de este producto en este almacén';
COMMENT ON COLUMN almacen_finca_producto.stock_minimo IS 'Nivel mínimo de stock para alertas';
COMMENT ON COLUMN almacen_finca_producto.stock_maximo IS 'Nivel máximo de stock para control';
COMMENT ON COLUMN movimiento_stock.almacen_id IS 'Almacén donde ocurrió el movimiento';
COMMENT ON COLUMN movimiento_stock.almacen_destino_id IS 'Almacén destino en caso de transferencia';
