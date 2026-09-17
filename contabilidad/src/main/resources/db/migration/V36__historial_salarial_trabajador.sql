-- Historial individual de condiciones salariales. No sustituye la nómina ni la prenómina.
CREATE TABLE IF NOT EXISTS historial_salario_trabajador (
    id UUID PRIMARY KEY,
    trabajador_id UUID NOT NULL REFERENCES trabajador(id),
    finca_id UUID NOT NULL REFERENCES fincas(id),
    cargo_id UUID REFERENCES cargo(id),
    fecha_vigencia DATE NOT NULL,
    salario_escala NUMERIC(19,4) NOT NULL CHECK (salario_escala >= 0),
    anticipo_diario NUMERIC(19,4) NOT NULL DEFAULT 0 CHECK (anticipo_diario >= 0),
    tasa NUMERIC(19,4) NOT NULL DEFAULT 0 CHECK (tasa >= 0),
    motivo VARCHAR(500) NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('ACTIVO', 'ANULADO')),
    autorizado_por_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_historial_salario_trabajador_fecha UNIQUE (trabajador_id, fecha_vigencia)
);
CREATE INDEX IF NOT EXISTS idx_historial_salario_trabajador_fecha
    ON historial_salario_trabajador(trabajador_id, fecha_vigencia DESC);
CREATE INDEX IF NOT EXISTS idx_historial_salario_finca_estado
    ON historial_salario_trabajador(finca_id, estado);
COMMENT ON TABLE historial_salario_trabajador IS
    'Vigencias salariales individuales, auditables e independientes del cálculo de nómina.';
