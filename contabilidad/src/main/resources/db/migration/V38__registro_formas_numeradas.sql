-- Registro auditable de formas numeradas. Es aditivo: no altera las secuencias
-- actuales mientras cada documento se migra de forma explícita al nuevo motor.

CREATE TABLE IF NOT EXISTS forma_numerada (
    id UUID PRIMARY KEY,
    codigo VARCHAR(60) NOT NULL UNIQUE,
    nombre VARCHAR(160) NOT NULL,
    referencia_modelo VARCHAR(80),
    prefijo VARCHAR(30) NOT NULL,
    digitos INTEGER NOT NULL CHECK (digitos BETWEEN 1 AND 12),
    reinicio VARCHAR(15) NOT NULL CHECK (reinicio IN ('ANUAL', 'CONTINUO')),
    alcance_predeterminado VARCHAR(15) NOT NULL CHECK (alcance_predeterminado IN ('ENTIDAD', 'FINCA', 'ALMACEN', 'CAJA', 'BANCO')),
    modo_emision VARCHAR(15) NOT NULL CHECK (modo_emision IN ('SISTEMA', 'PREIMPRESO', 'MIXTO')),
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_inicio DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_fin DATE,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_forma_numerada_fechas CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio)
);

CREATE TABLE IF NOT EXISTS serie_forma_numerada (
    id UUID PRIMARY KEY,
    forma_id UUID NOT NULL REFERENCES forma_numerada(id),
    alcance_tipo VARCHAR(15) NOT NULL CHECK (alcance_tipo IN ('ENTIDAD', 'FINCA', 'ALMACEN', 'CAJA', 'BANCO')),
    alcance_id UUID NULL,
    anio INTEGER NULL CHECK (anio BETWEEN 2000 AND 9999),
    prefijo VARCHAR(30),
    numero_inicial INTEGER NOT NULL DEFAULT 1 CHECK (numero_inicial > 0),
    ultimo_numero INTEGER NOT NULL DEFAULT 0 CHECK (ultimo_numero >= 0),
    numero_final INTEGER NULL CHECK (numero_final IS NULL OR numero_final > 0),
    estado VARCHAR(15) NOT NULL DEFAULT 'ACTIVA' CHECK (estado IN ('ACTIVA', 'SUSPENDIDA', 'CERRADA')),
    fecha_inicio DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_fin DATE,
    version BIGINT NOT NULL DEFAULT 0,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_serie_forma_rango CHECK (ultimo_numero >= numero_inicial - 1
        AND (numero_final IS NULL OR numero_final >= numero_inicial)),
    CONSTRAINT ck_serie_forma_fechas CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio)
);

-- PostgreSQL considera NULL distinto en UNIQUE. COALESCE protege una única
-- serie activa por forma, alcance y período, también cuando el alcance es entidad.
CREATE UNIQUE INDEX IF NOT EXISTS uq_serie_forma_activa_ambito_periodo
    ON serie_forma_numerada (forma_id, alcance_tipo,
        COALESCE(alcance_id, '00000000-0000-0000-0000-000000000000'::UUID),
        COALESCE(anio, -1))
    WHERE estado = 'ACTIVA';

CREATE INDEX IF NOT EXISTS idx_serie_forma_forma ON serie_forma_numerada(forma_id);
CREATE INDEX IF NOT EXISTS idx_serie_forma_ambito ON serie_forma_numerada(alcance_tipo, alcance_id);

CREATE TABLE IF NOT EXISTS emision_forma_numerada (
    id UUID PRIMARY KEY,
    forma_id UUID NOT NULL REFERENCES forma_numerada(id),
    serie_id UUID NOT NULL REFERENCES serie_forma_numerada(id),
    numero INTEGER NOT NULL CHECK (numero > 0),
    numero_formateado VARCHAR(80) NOT NULL,
    documento_tipo VARCHAR(80) NOT NULL,
    documento_id UUID NOT NULL,
    estado VARCHAR(15) NOT NULL DEFAULT 'EMITIDA' CHECK (estado IN ('EMITIDA', 'ANULADA')),
    fecha_emision TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_emisor_id UUID NULL,
    fecha_anulacion TIMESTAMP NULL,
    usuario_anulacion_id UUID NULL,
    motivo_anulacion VARCHAR(1000),
    total_reimpresiones INTEGER NOT NULL DEFAULT 0 CHECK (total_reimpresiones >= 0),
    ultima_reimpresion_en TIMESTAMP NULL,
    ultimo_usuario_reimpresion_id UUID NULL,
    CONSTRAINT uq_emision_forma_serie_numero UNIQUE (serie_id, numero),
    CONSTRAINT uq_emision_forma_documento UNIQUE (serie_id, documento_tipo, documento_id),
    CONSTRAINT ck_emision_forma_anulacion CHECK (
        (estado = 'EMITIDA' AND fecha_anulacion IS NULL AND usuario_anulacion_id IS NULL AND motivo_anulacion IS NULL)
        OR (estado = 'ANULADA' AND fecha_anulacion IS NOT NULL AND motivo_anulacion IS NOT NULL)
    )
);

CREATE INDEX IF NOT EXISTS idx_emision_forma_forma_fecha
    ON emision_forma_numerada(forma_id, fecha_emision DESC);
CREATE INDEX IF NOT EXISTS idx_emision_forma_documento
    ON emision_forma_numerada(documento_tipo, documento_id);

COMMENT ON TABLE forma_numerada IS
    'Catálogo de modelos oficiales o internos con numeración independiente.';
COMMENT ON TABLE serie_forma_numerada IS
    'Series por alcance y período; la reserva debe bloquear la fila de la serie.';
COMMENT ON TABLE emision_forma_numerada IS
    'Libro de emisión. Los números anulados se preservan y nunca se reutilizan.';
