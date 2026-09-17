-- Estructura organizativa y plantilla. Todo es aditivo: los grupos y trabajadores
-- existentes siguen funcionando aunque no se les asigne área ni plaza.
CREATE TABLE IF NOT EXISTS area_trabajo (
    id UUID PRIMARY KEY,
    finca_id UUID NOT NULL REFERENCES fincas(id),
    area_padre_id UUID NULL REFERENCES area_trabajo(id),
    responsable_id UUID NULL REFERENCES trabajador(id),
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500),
    tipo VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_inicio DATE NULL,
    fecha_fin DATE NULL,
    CONSTRAINT uq_area_trabajo_finca_codigo UNIQUE (finca_id, codigo)
);

CREATE INDEX IF NOT EXISTS idx_area_trabajo_finca ON area_trabajo(finca_id);
CREATE INDEX IF NOT EXISTS idx_area_trabajo_padre ON area_trabajo(area_padre_id);

ALTER TABLE grupo ADD COLUMN IF NOT EXISTS area_id UUID NULL REFERENCES area_trabajo(id);
CREATE INDEX IF NOT EXISTS idx_grupo_area ON grupo(area_id);

CREATE TABLE IF NOT EXISTS plaza (
    id UUID PRIMARY KEY,
    finca_id UUID NOT NULL REFERENCES fincas(id),
    area_id UUID NULL REFERENCES area_trabajo(id),
    grupo_id UUID NULL REFERENCES grupo(id),
    cargo_id UUID NOT NULL REFERENCES cargo(id),
    responsable_id UUID NULL REFERENCES trabajador(id),
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(150) NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_inicio DATE NULL,
    fecha_fin DATE NULL,
    observaciones VARCHAR(500),
    CONSTRAINT uq_plaza_finca_codigo UNIQUE (finca_id, codigo)
);

CREATE INDEX IF NOT EXISTS idx_plaza_finca ON plaza(finca_id);
CREATE INDEX IF NOT EXISTS idx_plaza_cargo ON plaza(cargo_id);
CREATE INDEX IF NOT EXISTS idx_plaza_area ON plaza(area_id);

ALTER TABLE trabajador ADD COLUMN IF NOT EXISTS plaza_id UUID NULL REFERENCES plaza(id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_trabajador_plaza_ocupada
    ON trabajador(plaza_id) WHERE plaza_id IS NOT NULL AND activo = TRUE;
