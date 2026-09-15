-- Migración: Agregar columnas de precios diferenciados a la tabla productos
-- Ejecutar en PostgreSQL

-- Agregar columna price_trabajador
ALTER TABLE productos ADD COLUMN IF NOT EXISTS price_trabajador DOUBLE PRECISION NOT NULL DEFAULT 0;

-- Agregar columna price_comedor
ALTER TABLE productos ADD COLUMN IF NOT EXISTS price_comedor DOUBLE PRECISION NOT NULL DEFAULT 0;

-- Opcional: Copiar el precio base a los nuevos campos si ya existen productos
UPDATE productos SET price_trabajador = price, price_comedor = price WHERE price_trabajador = 0;

-- Verificar las columnas
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 'productos';
