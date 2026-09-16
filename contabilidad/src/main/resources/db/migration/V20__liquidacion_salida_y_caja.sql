-- Liquidación trazable de vales/facturas. Cada aplicación puede ser parcial y
-- tener su propia forma de pago, sin destruir el documento de salida original.
ALTER TABLE salida ADD COLUMN IF NOT EXISTS pagado BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS liquidacion_salida (
    id UUID PRIMARY KEY,
    salida_id UUID NOT NULL REFERENCES salida(id),
    finca_id UUID NOT NULL REFERENCES fincas(id),
    fecha TIMESTAMP NOT NULL,
    entregado_por VARCHAR(150),
    recibido_por VARCHAR(150),
    observaciones VARCHAR(500),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS liquidacion_item_salida (
    id UUID PRIMARY KEY,
    liquidacion_salida_id UUID NOT NULL REFERENCES liquidacion_salida(id),
    item_salida_id UUID NOT NULL REFERENCES item_salida(id),
    forma_pago VARCHAR(30) NOT NULL,
    importe DOUBLE PRECISION NOT NULL CHECK (importe > 0),
    referencia_bancaria VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS entrega_banco (
    id UUID PRIMARY KEY,
    finca_id UUID NOT NULL REFERENCES fincas(id),
    fecha TIMESTAMP NOT NULL,
    importe DOUBLE PRECISION NOT NULL CHECK (importe > 0),
    referencia_bancaria VARCHAR(150),
    entregado_por VARCHAR(150),
    recibido_por VARCHAR(150),
    observaciones VARCHAR(500),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS movimiento_caja (
    id UUID PRIMARY KEY,
    finca_id UUID NOT NULL REFERENCES fincas(id),
    fecha TIMESTAMP NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    importe DOUBLE PRECISION NOT NULL,
    liquidacion_item_salida_id UUID REFERENCES liquidacion_item_salida(id),
    entrega_banco_id UUID REFERENCES entrega_banco(id),
    observaciones VARCHAR(500)
);

ALTER TABLE deuda_trabajador_detalle ADD COLUMN IF NOT EXISTS liquidacion_item_salida_id UUID
    REFERENCES liquidacion_item_salida(id);

CREATE INDEX IF NOT EXISTS idx_liquidacion_salida_salida ON liquidacion_salida(salida_id);
CREATE INDEX IF NOT EXISTS idx_liquidacion_salida_finca_fecha ON liquidacion_salida(finca_id, fecha);
CREATE INDEX IF NOT EXISTS idx_liquidacion_item_item ON liquidacion_item_salida(item_salida_id);
CREATE INDEX IF NOT EXISTS idx_movimiento_caja_finca_fecha ON movimiento_caja(finca_id, fecha);
CREATE INDEX IF NOT EXISTS idx_entrega_banco_finca_fecha ON entrega_banco(finca_id, fecha);
