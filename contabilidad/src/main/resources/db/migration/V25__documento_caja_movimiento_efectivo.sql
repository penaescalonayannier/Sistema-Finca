-- Un documento puede afectar Caja una sola vez y siempre queda enlazado al movimiento que lo originó.
ALTER TABLE movimiento_caja ADD COLUMN IF NOT EXISTS documento_caja_id UUID REFERENCES documento_caja(id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_movimiento_caja_documento_caja ON movimiento_caja(documento_caja_id) WHERE documento_caja_id IS NOT NULL;
