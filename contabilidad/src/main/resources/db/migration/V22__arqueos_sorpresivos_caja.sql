-- Arqueos de caja: evidencia de conteo independiente; no modifica movimiento_caja ni saldo_caja_denominacion.
CREATE SEQUENCE IF NOT EXISTS arqueo_caja_numero_seq START WITH 1;

CREATE TABLE IF NOT EXISTS arqueo_caja (
    id UUID PRIMARY KEY,
    numero BIGINT NOT NULL UNIQUE DEFAULT nextval('arqueo_caja_numero_seq'),
    finca_id UUID NOT NULL REFERENCES fincas(id),
    fecha_apertura TIMESTAMP NOT NULL,
    fecha_cierre TIMESTAMP,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('ABIERTO', 'CERRADO')),
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('PARCIAL', 'TOTAL')),
    contador_responsable VARCHAR(150) NOT NULL,
    contador_usuario_id UUID,
    custodio VARCHAR(150),
    recibido_por VARCHAR(150),
    observaciones VARCHAR(1000),
    observaciones_apertura VARCHAR(1000),
    observaciones_cierre VARCHAR(1000),
    total_esperado DOUBLE PRECISION NOT NULL,
    total_fisico DOUBLE PRECISION,
    diferencia DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS arqueo_caja_denominacion (
    id UUID PRIMARY KEY,
    arqueo_caja_id UUID NOT NULL REFERENCES arqueo_caja(id),
    denominacion INTEGER NOT NULL CHECK (denominacion IN (5,10,20,50,100,200,500,1000,2000,5000,10000,20000)),
    cantidad_esperada INTEGER NOT NULL,
    cantidad_fisica INTEGER,
    CONSTRAINT uq_arqueo_caja_denominacion UNIQUE (arqueo_caja_id, denominacion)
);

CREATE INDEX IF NOT EXISTS idx_arqueo_caja_finca_fecha ON arqueo_caja(finca_id, fecha_apertura DESC);
CREATE UNIQUE INDEX IF NOT EXISTS uq_arqueo_caja_finca_abierto ON arqueo_caja(finca_id) WHERE estado = 'ABIERTO';
CREATE INDEX IF NOT EXISTS idx_arqueo_caja_denominacion_arqueo ON arqueo_caja_denominacion(arqueo_caja_id);
