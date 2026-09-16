-- Control documental de Caja y Banco.  No genera asientos: estos documentos son
-- evidencia y conciliación, conservando la contabilización en sus procesos origen.
CREATE SEQUENCE IF NOT EXISTS documento_caja_numero_seq START WITH 1;
CREATE TABLE IF NOT EXISTS documento_caja (
 id UUID PRIMARY KEY, numero BIGINT NOT NULL UNIQUE DEFAULT nextval('documento_caja_numero_seq'), finca_id UUID NOT NULL REFERENCES fincas(id),
 tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('RECIBO_EFECTIVO','VALE_PAGO_MENOR','ANTICIPO','LIQUIDACION_ANTICIPO','REEMBOLSO')),
 fecha TIMESTAMP NOT NULL, importe DOUBLE PRECISION NOT NULL CHECK (importe > 0), beneficiario VARCHAR(200) NOT NULL,
 concepto VARCHAR(1000) NOT NULL, entregado_por VARCHAR(150), recibido_por VARCHAR(150), autorizado_por VARCHAR(150),
 referencia VARCHAR(100), observaciones VARCHAR(1000), estado VARCHAR(15) NOT NULL DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO','ANULADO')), usuario_id UUID
);
CREATE INDEX IF NOT EXISTS idx_documento_caja_finca_fecha ON documento_caja(finca_id, fecha DESC);

CREATE SEQUENCE IF NOT EXISTS cheque_transferencia_numero_seq START WITH 1;
CREATE TABLE IF NOT EXISTS cheque_transferencia (
 id UUID PRIMARY KEY, numero BIGINT NOT NULL UNIQUE DEFAULT nextval('cheque_transferencia_numero_seq'), finca_id UUID NOT NULL REFERENCES fincas(id),
 tipo VARCHAR(15) NOT NULL CHECK (tipo IN ('CHEQUE','TRANSFERENCIA')), fecha_emision TIMESTAMP NOT NULL, importe DOUBLE PRECISION NOT NULL CHECK (importe > 0),
 beneficiario VARCHAR(200) NOT NULL, concepto VARCHAR(1000) NOT NULL, referencia_bancaria VARCHAR(100), autorizado_por VARCHAR(150), emitido_por VARCHAR(150),
 estado VARCHAR(15) NOT NULL DEFAULT 'EMITIDO' CHECK (estado IN ('EMITIDO','COBRADO','ANULADO')), fecha_confirmacion TIMESTAMP, observaciones VARCHAR(1000), usuario_id UUID
);
CREATE INDEX IF NOT EXISTS idx_cheque_transferencia_finca_fecha ON cheque_transferencia(finca_id, fecha_emision DESC);

CREATE SEQUENCE IF NOT EXISTS conciliacion_bancaria_numero_seq START WITH 1;
CREATE TABLE IF NOT EXISTS conciliacion_bancaria (
 id UUID PRIMARY KEY, numero BIGINT NOT NULL UNIQUE DEFAULT nextval('conciliacion_bancaria_numero_seq'), finca_id UUID NOT NULL REFERENCES fincas(id),
 periodo DATE NOT NULL, fecha TIMESTAMP NOT NULL, saldo_extracto DOUBLE PRECISION NOT NULL, saldo_libros DOUBLE PRECISION NOT NULL,
 estado VARCHAR(15) NOT NULL DEFAULT 'ABIERTA' CHECK (estado IN ('ABIERTA','CERRADA')), responsable VARCHAR(150) NOT NULL, observaciones VARCHAR(1000), usuario_id UUID,
 CONSTRAINT uq_conciliacion_bancaria_periodo UNIQUE(finca_id, periodo)
);
CREATE TABLE IF NOT EXISTS movimiento_conciliacion_bancaria (
 id UUID PRIMARY KEY, conciliacion_id UUID NOT NULL REFERENCES conciliacion_bancaria(id), fecha DATE NOT NULL, origen VARCHAR(15) NOT NULL CHECK (origen IN ('EXTRACTO','LIBROS')),
 descripcion VARCHAR(500) NOT NULL, referencia VARCHAR(100), importe DOUBLE PRECISION NOT NULL, conciliado BOOLEAN NOT NULL DEFAULT FALSE, observaciones VARCHAR(500)
);
CREATE INDEX IF NOT EXISTS idx_movimiento_conciliacion_conciliacion ON movimiento_conciliacion_bancaria(conciliacion_id);
