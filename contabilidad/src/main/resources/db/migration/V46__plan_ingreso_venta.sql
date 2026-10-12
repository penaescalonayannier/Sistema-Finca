CREATE TABLE IF NOT EXISTS plan_ingreso_venta (
    id UUID PRIMARY KEY,
    finca_id UUID NOT NULL REFERENCES fincas(id),
    periodo DATE NOT NULL,
    estado VARCHAR(20) NOT NULL,
    pagos_salario NUMERIC(19, 4) NOT NULL DEFAULT 0,
    porcentaje_aporte_presupuesto NUMERIC(7, 4) NOT NULL DEFAULT 10,
    aporte_presupuesto NUMERIC(19, 4) NOT NULL DEFAULT 0,
    pagos_compras NUMERIC(19, 4) NOT NULL DEFAULT 0,
    compras_servicios NUMERIC(19, 4) NOT NULL DEFAULT 0,
    total_ingresos_planificados NUMERIC(19, 4) NOT NULL DEFAULT 0,
    total_egresos NUMERIC(19, 4) NOT NULL DEFAULT 0,
    disponible NUMERIC(19, 4) NOT NULL DEFAULT 0,
    observaciones VARCHAR(1000),
    creado_por_id UUID,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_plan_ingreso_venta_finca_periodo UNIQUE (finca_id, periodo),
    CONSTRAINT ck_plan_ingreso_venta_estado CHECK (estado IN ('BORRADOR', 'APROBADO', 'CERRADO', 'ANULADO')),
    CONSTRAINT ck_plan_ingreso_venta_importes_no_negativos CHECK (
        pagos_salario >= 0 AND porcentaje_aporte_presupuesto >= 0 AND aporte_presupuesto >= 0
        AND pagos_compras >= 0 AND compras_servicios >= 0
    )
);

CREATE TABLE IF NOT EXISTS elemento_plan_ingreso_venta (
    id UUID PRIMARY KEY,
    plan_ingreso_venta_id UUID NOT NULL REFERENCES plan_ingreso_venta(id) ON DELETE CASCADE,
    orden INTEGER NOT NULL,
    producto_codigo_snapshot VARCHAR(50),
    producto_nombre_snapshot VARCHAR(150) NOT NULL,
    unidad_medida_snapshot VARCHAR(50) NOT NULL,
    tipo_precio_snapshot VARCHAR(30),
    cantidad_planificada NUMERIC(19, 4) NOT NULL,
    precio_unitario_snapshot NUMERIC(19, 4) NOT NULL,
    importe_planificado NUMERIC(19, 4) NOT NULL,
    CONSTRAINT uq_elemento_plan_ingreso_venta_orden UNIQUE (plan_ingreso_venta_id, orden),
    CONSTRAINT ck_elemento_plan_ingreso_venta_importes_no_negativos CHECK (
        cantidad_planificada >= 0 AND precio_unitario_snapshot >= 0 AND importe_planificado >= 0
    )
);

CREATE INDEX IF NOT EXISTS idx_plan_ingreso_venta_finca_periodo ON plan_ingreso_venta(finca_id, periodo DESC);
CREATE INDEX IF NOT EXISTS idx_elemento_plan_ingreso_venta_plan ON elemento_plan_ingreso_venta(plan_ingreso_venta_id);
