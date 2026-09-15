-- =============================================================================
-- V15: Crear tipo_reporte, tipo_animal y agregar categoria a tipo_cultivo
-- =============================================================================

-- 1. Agregar columna categoria a tipo_cultivo
ALTER TABLE tipo_cultivo ADD COLUMN IF NOT EXISTS categoria VARCHAR(20) NOT NULL DEFAULT 'OTRO';

-- Actualizar categorías de los tipos de cultivo existentes
UPDATE tipo_cultivo SET categoria = 'CANNA' WHERE codigo = 'CANA';
UPDATE tipo_cultivo SET categoria = 'VIANDA' WHERE codigo IN ('YUCA', 'MAIZ', 'BONIATO', 'ARROZ', 'PLATANO', 'FRIJOL');
UPDATE tipo_cultivo SET categoria = 'OTRO' WHERE categoria IS NULL OR categoria = 'OTRO';

-- 2. Crear tabla tipo_reporte
CREATE TABLE IF NOT EXISTS tipo_reporte (
    id UUID PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    codigo_centro_costo VARCHAR(50),
    tipo_subclasificacion VARCHAR(20) NOT NULL DEFAULT 'NINGUNO',
    tipo_cultivo_categoria_filtro VARCHAR(20),
    tipo_cultivo_auto_id UUID,
    requiere_campo BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    orden INTEGER,
    CONSTRAINT fk_tipo_reporte_tipo_cultivo_auto FOREIGN KEY (tipo_cultivo_auto_id) REFERENCES tipo_cultivo(id)
);

CREATE INDEX IF NOT EXISTS idx_tipo_reporte_codigo ON tipo_reporte(codigo);
CREATE INDEX IF NOT EXISTS idx_tipo_reporte_activo ON tipo_reporte(activo);

-- 3. Crear tabla tipo_animal
CREATE TABLE IF NOT EXISTS tipo_animal (
    id UUID PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    orden INTEGER
);

CREATE INDEX IF NOT EXISTS idx_tipo_animal_codigo ON tipo_animal(codigo);
CREATE INDEX IF NOT EXISTS idx_tipo_animal_activo ON tipo_animal(activo);

-- 4. Agregar columnas a reporte (nullable para compatibilidad con datos existentes)
ALTER TABLE reporte ADD COLUMN IF NOT EXISTS tipo_reporte_id UUID;
ALTER TABLE reporte ADD COLUMN IF NOT EXISTS tipo_animal_id UUID;

-- Foreign keys
ALTER TABLE reporte
    ADD CONSTRAINT fk_reporte_tipo_reporte
    FOREIGN KEY (tipo_reporte_id) REFERENCES tipo_reporte(id);

ALTER TABLE reporte
    ADD CONSTRAINT fk_reporte_tipo_animal
    FOREIGN KEY (tipo_animal_id) REFERENCES tipo_animal(id);

-- =============================================================================
-- Datos iniciales: Tipos de reporte
-- =============================================================================

INSERT INTO tipo_reporte (id, codigo, nombre, descripcion, codigo_centro_costo, tipo_subclasificacion, tipo_cultivo_categoria_filtro, tipo_cultivo_auto_id, requiere_campo, activo, orden)
VALUES
    -- Caña: auto-selecciona tipo cultivo CANA, requiere bloque/campo
    ('b1000000-0000-0000-0000-000000000001', 'REPORTE_CANNA', 'Reporte de Caña',
     'Reportes de trabajo en cultivo de caña de azúcar', 'CC-CANNA',
     'CULTIVO', 'CANNA', 'a1000000-0000-0000-0000-000000000001', TRUE, TRUE, 1),

    -- Plan Vianda: permite seleccionar entre cultivos VIANDA
    ('b2000000-0000-0000-0000-000000000002', 'REPORTE_PLAN_VIANDA', 'Plan Vianda',
     'Reportes de producción de alimentos (yuca, maíz, boniato, etc.)', 'CC-VIANDA',
     'CULTIVO', 'VIANDA', NULL, FALSE, TRUE, 2),

    -- Vaquería: usa tipo_animal como subclasificación
    ('b3000000-0000-0000-0000-000000000003', 'REPORTE_VAQUERIA', 'Reporte de Vaquería',
     'Reportes de trabajo con ganado (vacas, ovejas, conejos, chivos, cerdos)', 'CC-GANADERIA',
     'ANIMAL', NULL, NULL, FALSE, TRUE, 3),

    -- Taller: sin subclasificación
    ('b4000000-0000-0000-0000-000000000004', 'REPORTE_TALLER', 'Reporte de Taller',
     'Reportes de trabajo en taller', 'CC-TALLER',
     'NINGUNO', NULL, NULL, FALSE, TRUE, 4),

    -- Dirección: sin subclasificación
    ('b5000000-0000-0000-0000-000000000005', 'REPORTE_DIRECCION', 'Reporte de Dirección',
     'Reportes de trabajo administrativo/dirección', 'CC-DIRECCION',
     'NINGUNO', NULL, NULL, FALSE, TRUE, 5),

    -- Servicio: sin subclasificación
    ('b6000000-0000-0000-0000-000000000006', 'REPORTE_SERVICIO', 'Reporte de Servicio',
     'Reportes de servicios varios', 'CC-SERVICIO',
     'NINGUNO', NULL, NULL, FALSE, TRUE, 6)
ON CONFLICT (codigo) DO NOTHING;

-- =============================================================================
-- Datos iniciales: Tipos de animal
-- =============================================================================

INSERT INTO tipo_animal (id, codigo, nombre, descripcion, activo, orden)
VALUES
    ('c1000000-0000-0000-0000-000000000001', 'VACA', 'Vacas', 'Ganado vacuno', TRUE, 1),
    ('c2000000-0000-0000-0000-000000000002', 'OVEJA', 'Ovejas', 'Ganado ovino', TRUE, 2),
    ('c3000000-0000-0000-0000-000000000003', 'CONEJO', 'Conejos', 'Cría de conejos', TRUE, 3),
    ('c4000000-0000-0000-0000-000000000004', 'CHIVO', 'Chivos', 'Ganado caprino', TRUE, 4),
    ('c5000000-0000-0000-0000-000000000005', 'CERDO', 'Cerdos', 'Ganado porcino', TRUE, 5),
    ('c6000000-0000-0000-0000-000000000006', 'CABALLO', 'Caballos', 'Equinos', TRUE, 6),
    ('c7000000-0000-0000-0000-000000000007', 'GALLINA', 'Gallinas', 'Aves de corral', TRUE, 7)
ON CONFLICT (codigo) DO NOTHING;

-- =============================================================================
-- Comentarios para documentación
-- =============================================================================

COMMENT ON TABLE tipo_reporte IS 'Nomenclador de tipos de reporte. Define el centro de costo y qué subclasificación usar.';
COMMENT ON COLUMN tipo_reporte.tipo_subclasificacion IS 'CULTIVO = usa tipo_cultivo, ANIMAL = usa tipo_animal, NINGUNO = sin subclasificación';
COMMENT ON COLUMN tipo_reporte.tipo_cultivo_categoria_filtro IS 'Categoría de tipo_cultivo a filtrar (CANNA, VIANDA, OTRO)';
COMMENT ON COLUMN tipo_reporte.tipo_cultivo_auto_id IS 'ID del tipo de cultivo a auto-seleccionar (ej: CANA para REPORTE_CANNA)';

COMMENT ON TABLE tipo_animal IS 'Nomenclador de tipos de animal para reportes de vaquería.';
COMMENT ON COLUMN tipo_cultivo.categoria IS 'Categoría del cultivo: CANNA, VIANDA, OTRO. Permite filtrar en tipos de reporte.';
