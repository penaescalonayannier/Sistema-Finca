-- Control físico de billetes CUP. Los importes históricos se conservan en
-- movimiento_caja y se regularizan mediante un movimiento de APERTURA_CAJA.
CREATE TABLE IF NOT EXISTS movimiento_caja_denominacion (
    id UUID PRIMARY KEY,
    movimiento_caja_id UUID NOT NULL REFERENCES movimiento_caja(id),
    denominacion INTEGER NOT NULL CHECK (denominacion IN (5,10,20,50,100,200,500,1000,2000,5000,10000,20000)),
    cantidad INTEGER NOT NULL CHECK (cantidad <> 0),
    CONSTRAINT uq_movimiento_caja_denominacion UNIQUE (movimiento_caja_id, denominacion)
);

CREATE TABLE IF NOT EXISTS saldo_caja_denominacion (
    id UUID PRIMARY KEY,
    finca_id UUID NOT NULL REFERENCES fincas(id),
    denominacion INTEGER NOT NULL CHECK (denominacion IN (5,10,20,50,100,200,500,1000,2000,5000,10000,20000)),
    cantidad INTEGER NOT NULL CHECK (cantidad >= 0),
    CONSTRAINT uq_saldo_caja_finca_denominacion UNIQUE (finca_id, denominacion)
);

CREATE INDEX idx_movimiento_caja_denominacion_movimiento ON movimiento_caja_denominacion(movimiento_caja_id);
CREATE INDEX idx_saldo_caja_denominacion_finca ON saldo_caja_denominacion(finca_id);
