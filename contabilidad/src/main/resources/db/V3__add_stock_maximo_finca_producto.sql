-- SPEC-003: Add stock_maximo column to finca_producto table
-- This enables EXCESO state detection when stock > stockMaximo

ALTER TABLE finca_producto ADD COLUMN IF NOT EXISTS stock_maximo INTEGER;

-- Add comment for documentation
COMMENT ON COLUMN finca_producto.stock_maximo IS 'Maximum stock threshold. When stock > stockMaximo, estado = EXCESO';
