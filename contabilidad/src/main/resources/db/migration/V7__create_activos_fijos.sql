-- ============================================================================
-- V7: Activos Fijos Tangibles
-- ============================================================================
-- Referencia normativa:
-- - NCC No. 7 (Resolución 1038/2017 MFP): Control y registro de activos fijos
-- - Resolución 51/2021 MFP: Tasas máximas de depreciación por grupo
-- - Resolución 60/2011 CGR: Control interno de recursos
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Tabla: grupo_activo_fijo
-- Grupos de clasificación según NCC No. 7
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS grupo_activo_fijo (
    id UUID PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tasa_depreciacion DECIMAL(5,2),
    vida_util_anios INTEGER,
    cuenta_contable VARCHAR(20),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Datos iniciales según Resolución 51/2021 MFP
INSERT INTO grupo_activo_fijo (id, codigo, nombre, descripcion, tasa_depreciacion, vida_util_anios, cuenta_contable, activo)
VALUES
    (gen_random_uuid(), '01', 'Edificios', 'Edificaciones e instalaciones', 3.00, 33, '240', TRUE),
    (gen_random_uuid(), '02', 'Otras Construcciones', 'Construcciones auxiliares, cercas, pozos', 5.00, 20, '241', TRUE),
    (gen_random_uuid(), '04', 'Máquinas y Equipos', 'Maquinaria agrícola e industrial', 10.00, 10, '243', TRUE),
    (gen_random_uuid(), '05', 'Aparatos', 'Equipos de medición, control y comunicación', 12.00, 8, '244', TRUE),
    (gen_random_uuid(), '07', 'Muebles y Enseres', 'Mobiliario de oficina y otros', 10.00, 10, '246', TRUE),
    (gen_random_uuid(), '08', 'Animales', 'Ganado vacuno, equino y otros', 20.00, 5, '247', TRUE),
    (gen_random_uuid(), '12', 'Plantaciones Permanentes Caña', 'Cultivos de caña de azúcar', 12.50, 8, '251', TRUE),
    (gen_random_uuid(), '13', 'Plantaciones Permanentes Frutales', 'Cultivos de frutales y otros permanentes', 10.00, 10, '252', TRUE)
ON CONFLICT (codigo) DO NOTHING;

-- ----------------------------------------------------------------------------
-- Tabla: activo_fijo_tangible
-- Registro maestro de activos fijos según NCC No. 7
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS activo_fijo_tangible (
    id UUID PRIMARY KEY,
    numero_inventario VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL,
    grupo_id UUID REFERENCES grupo_activo_fijo(id),
    finca_id UUID REFERENCES finca(id),
    valor_adquisicion DECIMAL(15,2) NOT NULL DEFAULT 0,
    depreciacion_acumulada DECIMAL(15,2) DEFAULT 0,
    valor_residual DECIMAL(15,2),
    estado_tecnico_porcentaje DECIMAL(5,2),
    valor_tasacion DECIMAL(15,2),
    fecha_adquisicion DATE,
    fecha_baja DATE,
    destino VARCHAR(255),
    observaciones TEXT,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_activo_fijo_grupo ON activo_fijo_tangible(grupo_id);
CREATE INDEX idx_activo_fijo_finca ON activo_fijo_tangible(finca_id);
CREATE INDEX idx_activo_fijo_activo ON activo_fijo_tangible(activo);
CREATE INDEX idx_activo_fijo_numero_inventario ON activo_fijo_tangible(numero_inventario);

-- ----------------------------------------------------------------------------
-- Tabla: activo_animal
-- Animales como activos fijos (Grupo 08) según NCC No. 7
-- Tabla independiente para facilitar el registro de inventario ganadero
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS activo_animal (
    id UUID PRIMARY KEY,
    numero_inventario VARCHAR(50),
    codigo_arete VARCHAR(50),
    hierro VARCHAR(50),
    categoria VARCHAR(50) NOT NULL,
    tipo_ganado VARCHAR(50) NOT NULL,
    finca_id UUID REFERENCES finca(id),
    valor_adquisicion DECIMAL(15,2) NOT NULL DEFAULT 0,
    depreciacion_acumulada DECIMAL(15,2) DEFAULT 0,
    valor_residual DECIMAL(15,2),
    valor_tasacion DECIMAL(15,2),
    anios_vida INTEGER,
    peso_promedio DECIMAL(10,2),
    destino VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_activo_animal_categoria ON activo_animal(categoria);
CREATE INDEX idx_activo_animal_tipo_ganado ON activo_animal(tipo_ganado);
CREATE INDEX idx_activo_animal_finca ON activo_animal(finca_id);
CREATE INDEX idx_activo_animal_activo ON activo_animal(activo);

COMMENT ON TABLE activo_animal IS 'Animales como AFT - Grupo 08 según NCC No. 7';
COMMENT ON COLUMN activo_animal.categoria IS 'Categoría: TERNERO, ANOJO, TORETE, NOVILLA, BUEY, VACA, TORO, SEMENTAL, etc.';
COMMENT ON COLUMN activo_animal.tipo_ganado IS 'Tipo: VACUNO, EQUINO, PORCINO, OVINO, CAPRINO, AVICOLA';

-- ----------------------------------------------------------------------------
-- Tabla: plantacion_permanente
-- Plantaciones Permanentes (Grupos 12 y 13) según NCC No. 7
-- Tabla independiente para facilitar el registro de cultivos
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS plantacion_permanente (
    id UUID PRIMARY KEY,
    numero_inventario VARCHAR(50),
    tipo_plantacion VARCHAR(50) NOT NULL,
    bloque INTEGER,
    campo INTEGER,
    area_hectareas DECIMAL(10,4),
    tipo_cepa VARCHAR(50),
    codigo_variedad VARCHAR(50),
    anios_cepa INTEGER,
    finca_id UUID REFERENCES finca(id),
    valor_adquisicion DECIMAL(15,2) NOT NULL DEFAULT 0,
    depreciacion_acumulada DECIMAL(15,2) DEFAULT 0,
    valor_residual DECIMAL(15,2),
    valor_tasacion DECIMAL(15,2),
    destino VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_plantacion_tipo ON plantacion_permanente(tipo_plantacion);
CREATE INDEX idx_plantacion_bloque ON plantacion_permanente(bloque);
CREATE INDEX idx_plantacion_finca ON plantacion_permanente(finca_id);
CREATE INDEX idx_plantacion_activo ON plantacion_permanente(activo);

COMMENT ON TABLE plantacion_permanente IS 'Plantaciones Permanentes - Grupos 12 y 13 según NCC No. 7';
COMMENT ON COLUMN plantacion_permanente.tipo_cepa IS 'S/Q=Siembra Quedada, R/Q=Retoño Quedado, R/1-R/5=Retoños';
COMMENT ON COLUMN plantacion_permanente.tipo_plantacion IS 'CANA, PLATANO, MANGO, GUAYABA, CITRICOS, CAFE, CACAO, OTROS';

-- ----------------------------------------------------------------------------
-- Tabla: movimiento_depreciacion
-- Registro de depreciación mensual según NCC No. 7
-- Fórmula: Depreciación Mensual = (Valor Adquisición × Tasa%) / 12
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS movimiento_depreciacion (
    id UUID PRIMARY KEY,
    activo_fijo_id UUID NOT NULL REFERENCES activo_fijo_tangible(id),
    fecha DATE NOT NULL,
    mes INTEGER NOT NULL CHECK (mes BETWEEN 1 AND 12),
    anio INTEGER NOT NULL,
    monto_depreciacion DECIMAL(15,2) NOT NULL,
    depreciacion_acumulada_anterior DECIMAL(15,2),
    depreciacion_acumulada_nueva DECIMAL(15,2),
    valor_residual_resultante DECIMAL(15,2),
    tasa_aplicada DECIMAL(5,2),
    observacion TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_mov_deprec_activo ON movimiento_depreciacion(activo_fijo_id);
CREATE INDEX idx_mov_deprec_periodo ON movimiento_depreciacion(mes, anio);
CREATE INDEX idx_mov_deprec_fecha ON movimiento_depreciacion(fecha);
CREATE UNIQUE INDEX idx_mov_deprec_activo_periodo ON movimiento_depreciacion(activo_fijo_id, mes, anio);

COMMENT ON TABLE movimiento_depreciacion IS 'Cierre mensual de depreciación según NCC No. 7 (Res. 1038/2017 MFP)';
COMMENT ON COLUMN movimiento_depreciacion.monto_depreciacion IS 'Calculado: (Valor Adquisición × Tasa Anual%) / 12';
