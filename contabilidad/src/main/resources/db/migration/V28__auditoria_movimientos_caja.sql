-- Trazabilidad de usuario e instante técnico para los hechos que modifican efectivo.
-- "fecha" conserva la fecha económica del documento; created_at no se altera por ella.
ALTER TABLE movimiento_caja
    ADD COLUMN IF NOT EXISTS usuario_id UUID,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

UPDATE movimiento_caja
SET created_at = COALESCE(created_at, fecha, CURRENT_TIMESTAMP)
WHERE created_at IS NULL;

ALTER TABLE movimiento_caja
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE entrega_banco
    ADD COLUMN IF NOT EXISTS usuario_id UUID,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

UPDATE entrega_banco
SET created_at = COALESCE(created_at, fecha, CURRENT_TIMESTAMP)
WHERE created_at IS NULL;

ALTER TABLE entrega_banco
    ALTER COLUMN created_at SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_movimiento_caja_finca_fecha
    ON movimiento_caja(finca_id, fecha DESC);
CREATE INDEX IF NOT EXISTS idx_movimiento_caja_usuario
    ON movimiento_caja(usuario_id);
CREATE INDEX IF NOT EXISTS idx_entrega_banco_usuario
    ON entrega_banco(usuario_id);
