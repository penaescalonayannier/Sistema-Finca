-- =============================================================================
-- V14: Crear tabla tipo_cultivo y relacionar con reporte
-- =============================================================================

-- Nomenclador de tipos de cultivo
CREATE TABLE IF NOT EXISTS tipo_cultivo (
    id UUID PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    requiere_campo BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    orden INTEGER
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_tipo_cultivo_codigo ON tipo_cultivo(codigo);
CREATE INDEX IF NOT EXISTS idx_tipo_cultivo_activo ON tipo_cultivo(activo);

-- Agregar columna a reporte
ALTER TABLE reporte ADD COLUMN IF NOT EXISTS tipo_cultivo_id UUID;

-- Foreign key
ALTER TABLE reporte
    ADD CONSTRAINT fk_reporte_tipo_cultivo
    FOREIGN KEY (tipo_cultivo_id) REFERENCES tipo_cultivo(id);

-- Hacer bloque y campo nullable (antes eran NOT NULL)
ALTER TABLE reporte ALTER COLUMN bloque DROP NOT NULL;
ALTER TABLE reporte ALTER COLUMN campo DROP NOT NULL;
ALTER TABLE reporte ALTER COLUMN area DROP NOT NULL;

-- =============================================================================
-- Datos iniciales: Tipos de cultivo comunes en agricultura cubana
-- =============================================================================

INSERT INTO tipo_cultivo (id, codigo, nombre, descripcion, requiere_campo, activo, orden)
VALUES
    -- Caña de azúcar: requiere selección de bloque y campo
    ('a1000000-0000-0000-0000-000000000001', 'CANA', 'Caña de Azúcar', 'Cultivo de caña, requiere selección de bloque y campo', TRUE, TRUE, 1),

    -- Cultivos varios: NO requieren bloque y campo
    ('a2000000-0000-0000-0000-000000000002', 'YUCA', 'Yuca', 'Cultivo de yuca', FALSE, TRUE, 2),
    ('a3000000-0000-0000-0000-000000000003', 'MAIZ', 'Maíz', 'Cultivo de maíz', FALSE, TRUE, 3),
    ('a4000000-0000-0000-0000-000000000004', 'BONIATO', 'Boniato', 'Cultivo de boniato', FALSE, TRUE, 4),
    ('a5000000-0000-0000-0000-000000000005', 'ARROZ', 'Arroz', 'Cultivo de arroz', FALSE, TRUE, 5),
    ('a6000000-0000-0000-0000-000000000006', 'PLATANO', 'Plátano Burro', 'Cultivo de plátano burro', FALSE, TRUE, 6),
    ('a7000000-0000-0000-0000-000000000007', 'FRIJOL', 'Frijol', 'Cultivo de frijol', FALSE, TRUE, 7),
    ('a8000000-0000-0000-0000-000000000008', 'TABACO', 'Tabaco', 'Cultivo de tabaco', FALSE, TRUE, 8),
    ('a9000000-0000-0000-0000-000000000009', 'HORTALIZA', 'Hortalizas', 'Cultivos de hortalizas variadas', FALSE, TRUE, 9),
    ('aa000000-0000-0000-0000-000000000010', 'OTROS', 'Otros Cultivos', 'Otros cultivos no clasificados', FALSE, TRUE, 99)
ON CONFLICT (codigo) DO NOTHING;

-- Comentario para documentación
COMMENT ON TABLE tipo_cultivo IS 'Nomenclador de tipos de cultivo. requiere_campo indica si se debe seleccionar bloque/campo en el reporte de trabajo.';
COMMENT ON COLUMN tipo_cultivo.requiere_campo IS 'Si es TRUE, el reporte debe incluir bloque y campo (ej: Caña). Si es FALSE, no se requiere (ej: Yuca, Maíz).';
