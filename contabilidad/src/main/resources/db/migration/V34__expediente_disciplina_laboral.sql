CREATE TABLE IF NOT EXISTS expediente_disciplinario (
    id UUID PRIMARY KEY,
    finca_id UUID NOT NULL REFERENCES fincas(id),
    trabajador_id UUID NOT NULL REFERENCES trabajador(id),
    aprobador_id UUID REFERENCES trabajador(id),
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('TARDANZA','AUSENCIA','INCUMPLIMIENTO','OTRA')),
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('BORRADOR','NOTIFICADA','RESUELTA','ANULADA')),
    fecha DATE NOT NULL,
    descripcion VARCHAR(3000) NOT NULL,
    evidencia VARCHAR(3000),
    observaciones VARCHAR(3000),
    medida VARCHAR(1000),
    resolucion VARCHAR(3000),
    fecha_notificacion TIMESTAMP,
    fecha_resolucion TIMESTAMP,
    fecha_anulacion TIMESTAMP,
    motivo_anulacion VARCHAR(3000),
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_expediente_disciplina_finca_fecha ON expediente_disciplinario(finca_id, fecha DESC);
CREATE INDEX IF NOT EXISTS idx_expediente_disciplina_trabajador ON expediente_disciplinario(trabajador_id, fecha DESC);
