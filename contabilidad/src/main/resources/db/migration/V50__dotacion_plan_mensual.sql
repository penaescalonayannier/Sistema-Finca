CREATE TABLE IF NOT EXISTS dotacion_plan_mensual (
    id UUID PRIMARY KEY,
    actividad_plan_mensual_id UUID NOT NULL REFERENCES actividad_plan_mensual(id) ON DELETE CASCADE,
    cargo_id UUID NOT NULL,
    cargo_nombre_snapshot VARCHAR(150) NOT NULL,
    salario_escala_snapshot NUMERIC(19,4) NOT NULL,
    cantidad_trabajadores INTEGER NOT NULL,
    dias_planificados NUMERIC(19,4) NOT NULL,
    horas_por_dia NUMERIC(19,4) NOT NULL,
    salario_planificado NUMERIC(19,4) NOT NULL,
    CONSTRAINT ck_dotacion_plan_mensual_valores CHECK (
        cantidad_trabajadores > 0 AND dias_planificados >= 0 AND horas_por_dia >= 0
        AND salario_escala_snapshot >= 0 AND salario_planificado >= 0
    )
);
CREATE INDEX IF NOT EXISTS idx_dotacion_plan_mensual_actividad ON dotacion_plan_mensual(actividad_plan_mensual_id);
