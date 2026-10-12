-- Amplía el nomenclador existente de labores para que sirva como plantilla
-- técnica reutilizable de planificación agrícola mensual.
ALTER TABLE labor ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE labor ADD COLUMN tipo_cultivo_id UUID;
ALTER TABLE labor ADD COLUMN codigo_centro_costo VARCHAR(20);
ALTER TABLE labor ADD COLUMN unidad_medida VARCHAR(30);
ALTER TABLE labor ADD COLUMN norma_rendimiento NUMERIC(19,4);
ALTER TABLE labor ADD COLUMN dotacion_estandar NUMERIC(19,4);
ALTER TABLE labor ADD COLUMN dias_estimados NUMERIC(19,4);
ALTER TABLE labor ADD COLUMN horas_estimadas NUMERIC(19,4);

ALTER TABLE labor
    ADD CONSTRAINT fk_labor_tipo_cultivo
    FOREIGN KEY (tipo_cultivo_id) REFERENCES tipo_cultivo(id);

CREATE TABLE labor_cargo_requerido (
    labor_id UUID NOT NULL,
    cargo_id UUID NOT NULL,
    PRIMARY KEY (labor_id, cargo_id),
    CONSTRAINT fk_labor_cargo_requerido_labor FOREIGN KEY (labor_id) REFERENCES labor(id) ON DELETE CASCADE,
    CONSTRAINT fk_labor_cargo_requerido_cargo FOREIGN KEY (cargo_id) REFERENCES cargo(id)
);

CREATE INDEX idx_labor_tipo_cultivo ON labor(tipo_cultivo_id);
CREATE INDEX idx_labor_codigo_centro_costo ON labor(codigo_centro_costo);
