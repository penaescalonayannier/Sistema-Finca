ALTER TABLE plan_ingreso_venta DROP CONSTRAINT IF EXISTS ck_plan_ingreso_venta_estado;
ALTER TABLE plan_ingreso_venta ADD CONSTRAINT ck_plan_ingreso_venta_estado
    CHECK (estado IN ('BORRADOR', 'EN_REVISION', 'APROBADO', 'CERRADO', 'ANULADO'));

CREATE TABLE IF NOT EXISTS actividad_plan_mensual (
    id UUID PRIMARY KEY,
    plan_ingreso_venta_id UUID NOT NULL REFERENCES plan_ingreso_venta(id) ON DELETE CASCADE,
    orden INTEGER NOT NULL,
    tipo_cultivo_id UUID, cultivo_snapshot VARCHAR(150), bloque_snapshot VARCHAR(100), campo_snapshot VARCHAR(100),
    actividad_snapshot VARCHAR(200) NOT NULL, unidad_medida_snapshot VARCHAR(50),
    area_planificada NUMERIC(19,4), cantidad_planificada NUMERIC(19,4),
    costo_salario NUMERIC(19,4) NOT NULL DEFAULT 0, compras NUMERIC(19,4) NOT NULL DEFAULT 0,
    servicios NUMERIC(19,4) NOT NULL DEFAULT 0, total NUMERIC(19,4) NOT NULL DEFAULT 0,
    CONSTRAINT uq_actividad_plan_mensual_orden UNIQUE(plan_ingreso_venta_id, orden),
    CONSTRAINT ck_actividad_plan_mensual_importes CHECK (costo_salario >= 0 AND compras >= 0 AND servicios >= 0 AND total >= 0)
);
CREATE INDEX IF NOT EXISTS idx_actividad_plan_mensual_plan ON actividad_plan_mensual(plan_ingreso_venta_id);
