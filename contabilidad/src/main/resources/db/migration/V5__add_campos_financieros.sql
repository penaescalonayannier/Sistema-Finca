-- V5: Add financial fields to Campo and fincaId to Bloque

-- Add fincaId to bloque table
ALTER TABLE bloque ADD COLUMN IF NOT EXISTS finca_id UUID;
ALTER TABLE bloque ADD CONSTRAINT fk_bloque_finca
    FOREIGN KEY (finca_id) REFERENCES fincas(id) ON DELETE SET NULL;

-- Add financial fields to campo table
ALTER TABLE campo ADD COLUMN IF NOT EXISTS valor_adquisicion DOUBLE PRECISION;
ALTER TABLE campo ADD COLUMN IF NOT EXISTS depreciacion_acumulada DOUBLE PRECISION;
ALTER TABLE campo ADD COLUMN IF NOT EXISTS valor_residual DOUBLE PRECISION;
ALTER TABLE campo ADD COLUMN IF NOT EXISTS anos_cepa INTEGER;

-- Add depreciation rate fields to campo table
ALTER TABLE campo ADD COLUMN IF NOT EXISTS tasa_depreciacion_anual DOUBLE PRECISION;
ALTER TABLE campo ADD COLUMN IF NOT EXISTS vida_util_anios INTEGER;
ALTER TABLE campo ADD COLUMN IF NOT EXISTS fecha_ultima_depreciacion DATE;
ALTER TABLE campo ADD COLUMN IF NOT EXISTS fecha_inicio_depreciacion DATE;

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_bloque_finca_id ON bloque(finca_id);
CREATE INDEX IF NOT EXISTS idx_campo_bloque_id ON campo(bloque_id);
