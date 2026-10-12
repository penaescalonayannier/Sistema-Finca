ALTER TABLE actividad_plan_mensual
    ADD COLUMN IF NOT EXISTS labor_agricola_id UUID,
    ADD COLUMN IF NOT EXISTS labor_agricola_snapshot VARCHAR(200),
    ADD COLUMN IF NOT EXISTS codigo_centro_costo VARCHAR(30),
    ADD COLUMN IF NOT EXISTS centro_costo_snapshot VARCHAR(150);

CREATE TABLE IF NOT EXISTS gasto_plan_mensual (
    id UUID PRIMARY KEY,
    actividad_plan_mensual_id UUID NOT NULL REFERENCES actividad_plan_mensual(id) ON DELETE CASCADE,
    orden INTEGER NOT NULL,
    categoria VARCHAR(30) NOT NULL,
    subcategoria VARCHAR(30),
    descripcion VARCHAR(250) NOT NULL,
    unidad_medida_snapshot VARCHAR(50),
    cantidad NUMERIC(19,4) NOT NULL,
    precio_unitario_snapshot NUMERIC(19,4) NOT NULL,
    importe_planificado NUMERIC(19,4) NOT NULL,
    observacion VARCHAR(500),
    CONSTRAINT ck_gasto_plan_mensual_valores CHECK (cantidad >= 0 AND precio_unitario_snapshot >= 0 AND importe_planificado >= 0),
    CONSTRAINT ck_gasto_plan_mensual_categoria CHECK (categoria IN ('COMBUSTIBLE', 'LUBRICANTE', 'ENERGIA', 'MATERIAL', 'INSUMO', 'SERVICIO', 'OTRO')),
    CONSTRAINT ck_gasto_plan_mensual_subcategoria CHECK (subcategoria IS NULL OR subcategoria IN ('HERBICIDA', 'FERTILIZANTE', 'PLAGUICIDA', 'SEMILLA', 'ABONO', 'OTRO'))
);
CREATE INDEX IF NOT EXISTS idx_gasto_plan_mensual_actividad ON gasto_plan_mensual(actividad_plan_mensual_id);
