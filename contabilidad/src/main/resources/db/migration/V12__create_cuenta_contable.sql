-- ============================================================================
-- V12: Cuentas Contables - Nomenclador Cubano
-- ============================================================================
-- Referencia normativa:
-- - Resolución 494/2016 MFP: Nomenclador de Cuentas para actividad empresarial
-- - Resolución 500/2016 MFP: Normas Cubanas de Contabilidad
-- - Manual de Normas Cubanas de Información Financiera (NCIF)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Tabla: cuenta_contable
-- Plan de cuentas según Nomenclador Nacional de Cuba
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cuenta_contable (
    id UUID PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    tipo VARCHAR(20) NOT NULL,              -- ACTIVO, PASIVO, PATRIMONIO, INGRESO, GASTO, COSTO
    naturaleza VARCHAR(10) NOT NULL,        -- DEUDORA, ACREEDORA
    nivel INTEGER NOT NULL DEFAULT 1,       -- 1=Grupo, 2=Cuenta, 3=Subcuenta, 4=Análisis
    cuenta_padre_id UUID REFERENCES cuenta_contable(id),
    descripcion TEXT,
    permite_movimiento BOOLEAN DEFAULT TRUE, -- false = solo agrupa
    es_centro_costo BOOLEAN DEFAULT FALSE,  -- true = centro de costo
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cuenta_contable_codigo ON cuenta_contable(codigo);
CREATE INDEX idx_cuenta_contable_tipo ON cuenta_contable(tipo);
CREATE INDEX idx_cuenta_contable_padre ON cuenta_contable(cuenta_padre_id);
CREATE INDEX idx_cuenta_contable_activo ON cuenta_contable(activo);
CREATE INDEX idx_cuenta_contable_nivel ON cuenta_contable(nivel);

COMMENT ON TABLE cuenta_contable IS 'Plan de Cuentas según Nomenclador Cubano (Res. 494/2016 MFP)';
COMMENT ON COLUMN cuenta_contable.nivel IS '1=Grupo, 2=Cuenta, 3=Subcuenta, 4=Análisis';
COMMENT ON COLUMN cuenta_contable.naturaleza IS 'DEUDORA (aumenta por Debe), ACREEDORA (aumenta por Haber)';

-- ============================================================================
-- SEED: Nomenclador de Cuentas Cubano
-- Estructura jerárquica: Grupo → Cuenta → Subcuenta → Análisis
-- ============================================================================

-- ============================================================================
-- ACTIVO CIRCULANTE (100-199)
-- ============================================================================

-- Grupo: Efectivo en Caja
INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '100', 'Efectivo en Caja', 'ACTIVO', 'DEUDORA', 1, NULL,
     'Efectivo en poder de la entidad', FALSE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '101', 'Caja en Moneda Nacional', 'ACTIVO', 'DEUDORA', 2, id,
       'Efectivo en CUP', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '100';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '102', 'Caja en Moneda Libremente Convertible', 'ACTIVO', 'DEUDORA', 2, id,
       'Efectivo en MLC', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '100';

-- Grupo: Efectivo en Banco
INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '110', 'Efectivo en Banco', 'ACTIVO', 'DEUDORA', 1, NULL,
     'Depósitos en instituciones bancarias', FALSE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '111', 'Banco Moneda Nacional', 'ACTIVO', 'DEUDORA', 2, id,
       'Cuenta corriente en CUP', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '110';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '112', 'Banco Moneda Libremente Convertible', 'ACTIVO', 'DEUDORA', 2, id,
       'Cuenta corriente en MLC', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '110';

-- Grupo: Cuentas por Cobrar
INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '135', 'Cuentas por Cobrar a Corto Plazo', 'ACTIVO', 'DEUDORA', 1, NULL,
     'Derechos de cobro a clientes y otros', FALSE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '136', 'Cuentas por Cobrar Clientes', 'ACTIVO', 'DEUDORA', 2, id,
       'Ventas a crédito a clientes', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '135';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '138', 'Cuentas por Cobrar Trabajadores', 'ACTIVO', 'DEUDORA', 2, id,
       'Adeudos de trabajadores (deudas por productos)', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '135';

-- ============================================================================
-- INVENTARIOS (183-199)
-- ============================================================================

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '183', 'Materias Primas y Materiales', 'ACTIVO', 'DEUDORA', 1, NULL,
     'Insumos para la producción', FALSE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '183.01', 'Materias Primas', 'ACTIVO', 'DEUDORA', 2, id,
       'Materiales principales de producción', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '183';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '183.02', 'Materiales Auxiliares', 'ACTIVO', 'DEUDORA', 2, id,
       'Materiales secundarios', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '183';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '183.03', 'Combustibles', 'ACTIVO', 'DEUDORA', 2, id,
       'Combustibles y lubricantes', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '183';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '183.04', 'Piezas de Repuesto', 'ACTIVO', 'DEUDORA', 2, id,
       'Repuestos para equipos', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '183';

-- Mercancías para la Venta
INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '189', 'Mercancías para la Venta', 'ACTIVO', 'DEUDORA', 1, NULL,
     'Productos terminados y mercancías destinados a la venta', TRUE, FALSE);

-- Útiles y Herramientas
INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '191', 'Útiles y Herramientas', 'ACTIVO', 'DEUDORA', 1, NULL,
     'Herramientas y útiles de trabajo', TRUE, FALSE);

-- Producción Terminada
INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '193', 'Producción Terminada', 'ACTIVO', 'DEUDORA', 1, NULL,
     'Productos terminados de producción propia', FALSE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '193.01', 'Producción Agrícola Terminada', 'ACTIVO', 'DEUDORA', 2, id,
       'Cosechas y productos agrícolas terminados', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '193';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '193.02', 'Producción Pecuaria Terminada', 'ACTIVO', 'DEUDORA', 2, id,
       'Productos pecuarios terminados', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '193';

-- Producción en Proceso
INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '195', 'Producción en Proceso', 'ACTIVO', 'DEUDORA', 1, NULL,
     'Costos de producción en curso', FALSE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '195.01', 'Producción Agrícola en Proceso', 'ACTIVO', 'DEUDORA', 2, id,
       'Cultivos y labores agrícolas en curso', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '195';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '195.02', 'Producción Pecuaria en Proceso', 'ACTIVO', 'DEUDORA', 2, id,
       'Crianza y engorde en proceso', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '195';

-- Producción Agropecuaria en Desarrollo (cultivos jóvenes)
INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '197', 'Producción Agropecuaria en Desarrollo', 'ACTIVO', 'DEUDORA', 1, NULL,
     'Plantaciones jóvenes y ganado en desarrollo', TRUE, FALSE);

-- ============================================================================
-- PASIVO CIRCULANTE (400-499)
-- ============================================================================

-- Cuentas por Pagar
INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '410', 'Cuentas por Pagar Proveedores', 'PASIVO', 'ACREEDORA', 1, NULL,
     'Obligaciones con proveedores externos', TRUE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '411', 'Cuentas por Pagar a Corto Plazo', 'PASIVO', 'ACREEDORA', 1, NULL,
     'Otras obligaciones a corto plazo', TRUE, FALSE);

-- Obligaciones con el Presupuesto
INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '440', 'Obligaciones con el Presupuesto', 'PASIVO', 'ACREEDORA', 1, NULL,
     'Impuestos y contribuciones por pagar', FALSE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '441', 'Impuesto sobre Ingresos Personales', 'PASIVO', 'ACREEDORA', 2, id,
       'Retenciones de ISIP', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '440';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '443', 'Contribución a la Seguridad Social', 'PASIVO', 'ACREEDORA', 2, id,
       'Aportes a la seguridad social', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '440';

-- Nóminas por Pagar
INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '455', 'Nóminas por Pagar', 'PASIVO', 'ACREEDORA', 1, NULL,
     'Salarios y vacaciones pendientes de pago', TRUE, FALSE);

-- ============================================================================
-- PATRIMONIO (500-599)
-- ============================================================================

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '500', 'Inversión Estatal', 'PATRIMONIO', 'ACREEDORA', 1, NULL,
     'Capital aportado por el Estado', TRUE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '510', 'Utilidades Retenidas', 'PATRIMONIO', 'ACREEDORA', 1, NULL,
     'Resultados acumulados de ejercicios anteriores', TRUE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '520', 'Utilidad o Pérdida del Período', 'PATRIMONIO', 'ACREEDORA', 1, NULL,
     'Resultado del ejercicio en curso', TRUE, FALSE);

-- ============================================================================
-- INGRESOS (600-699)
-- ============================================================================

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '600', 'Ventas', 'INGRESO', 'ACREEDORA', 1, NULL,
     'Ingresos por ventas de productos y servicios', FALSE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '601', 'Ventas de Producción Agrícola', 'INGRESO', 'ACREEDORA', 2, id,
       'Ventas de cosechas y productos agrícolas', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '600';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '602', 'Ventas de Producción Pecuaria', 'INGRESO', 'ACREEDORA', 2, id,
       'Ventas de productos pecuarios', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '600';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '609', 'Otras Ventas', 'INGRESO', 'ACREEDORA', 2, id,
       'Otras ventas menores', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '600';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '690', 'Otros Ingresos', 'INGRESO', 'ACREEDORA', 1, NULL,
     'Ingresos no operacionales', TRUE, FALSE);

-- ============================================================================
-- CENTROS DE COSTO (700-799)
-- ============================================================================

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '700', 'Centros de Costo', 'COSTO', 'DEUDORA', 1, NULL,
     'Acumulación de costos por centro', FALSE, TRUE);

-- Centros de Costo Agrícolas
INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '700.01', 'Plan Vianda', 'COSTO', 'DEUDORA', 2, id,
       'Centro de costo - Producción de viandas', TRUE, TRUE
FROM cuenta_contable WHERE codigo = '700';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '700.02', 'Plan Hortalizas', 'COSTO', 'DEUDORA', 2, id,
       'Centro de costo - Producción de hortalizas', TRUE, TRUE
FROM cuenta_contable WHERE codigo = '700';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '700.03', 'Plan Granos', 'COSTO', 'DEUDORA', 2, id,
       'Centro de costo - Producción de granos', TRUE, TRUE
FROM cuenta_contable WHERE codigo = '700';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '700.04', 'Plan Frutales', 'COSTO', 'DEUDORA', 2, id,
       'Centro de costo - Producción de frutales', TRUE, TRUE
FROM cuenta_contable WHERE codigo = '700';

-- Centros de Costo Pecuarios
INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '700.10', 'Vaquería', 'COSTO', 'DEUDORA', 2, id,
       'Centro de costo - Producción de leche', TRUE, TRUE
FROM cuenta_contable WHERE codigo = '700';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '700.11', 'Ceba Vacuna', 'COSTO', 'DEUDORA', 2, id,
       'Centro de costo - Engorde de ganado vacuno', TRUE, TRUE
FROM cuenta_contable WHERE codigo = '700';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '700.12', 'Cría Porcina', 'COSTO', 'DEUDORA', 2, id,
       'Centro de costo - Crianza de cerdos', TRUE, TRUE
FROM cuenta_contable WHERE codigo = '700';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '700.13', 'Avicultura', 'COSTO', 'DEUDORA', 2, id,
       'Centro de costo - Producción avícola', TRUE, TRUE
FROM cuenta_contable WHERE codigo = '700';

-- Centros de Costo de Servicios
INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '700.20', 'Comedor', 'COSTO', 'DEUDORA', 2, id,
       'Centro de costo - Alimentación de trabajadores', TRUE, TRUE
FROM cuenta_contable WHERE codigo = '700';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '700.21', 'Autoconsumo', 'COSTO', 'DEUDORA', 2, id,
       'Centro de costo - Consumo interno de trabajadores', TRUE, TRUE
FROM cuenta_contable WHERE codigo = '700';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '700.30', 'Transporte', 'COSTO', 'DEUDORA', 2, id,
       'Centro de costo - Servicios de transporte', TRUE, TRUE
FROM cuenta_contable WHERE codigo = '700';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '700.31', 'Mantenimiento', 'COSTO', 'DEUDORA', 2, id,
       'Centro de costo - Mantenimiento y reparaciones', TRUE, TRUE
FROM cuenta_contable WHERE codigo = '700';

-- ============================================================================
-- GASTOS (800-899)
-- ============================================================================

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '731', 'Gastos de Fuerza de Trabajo', 'GASTO', 'DEUDORA', 1, NULL,
     'Salarios y prestaciones laborales', FALSE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '731.01', 'Salarios', 'GASTO', 'DEUDORA', 2, id,
       'Salarios básicos', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '731';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '731.02', 'Vacaciones', 'GASTO', 'DEUDORA', 2, id,
       'Provisión de vacaciones', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '731';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '731.03', 'Contribución Seguridad Social', 'GASTO', 'DEUDORA', 2, id,
       'Aporte patronal a la seguridad social', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '731';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '732', 'Gastos de Materias Primas y Materiales', 'GASTO', 'DEUDORA', 1, NULL,
     'Consumo de materiales en producción', TRUE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '733', 'Gastos de Combustible', 'GASTO', 'DEUDORA', 1, NULL,
     'Consumo de combustibles', TRUE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '734', 'Gastos de Energía', 'GASTO', 'DEUDORA', 1, NULL,
     'Consumo de energía eléctrica', TRUE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '738', 'Gastos de Depreciación', 'GASTO', 'DEUDORA', 1, NULL,
     'Depreciación de activos fijos', TRUE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '810', 'Costo de Ventas', 'GASTO', 'DEUDORA', 1, NULL,
     'Costo de los productos vendidos', TRUE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '819', 'Gastos de Operaciones', 'GASTO', 'DEUDORA', 1, NULL,
     'Gastos generales de operación', FALSE, FALSE);

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '819.01', 'Gastos de Administración', 'GASTO', 'DEUDORA', 2, id,
       'Gastos administrativos generales', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '819';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
SELECT gen_random_uuid(), '819.02', 'Gastos de Distribución', 'GASTO', 'DEUDORA', 2, id,
       'Gastos de distribución y ventas', TRUE, FALSE
FROM cuenta_contable WHERE codigo = '819';

INSERT INTO cuenta_contable (id, codigo, nombre, tipo, naturaleza, nivel, cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo)
VALUES
    (gen_random_uuid(), '890', 'Otros Gastos', 'GASTO', 'DEUDORA', 1, NULL,
     'Gastos no operacionales', TRUE, FALSE);

-- ============================================================================
-- Vista auxiliar para consultar jerarquía completa
-- ============================================================================
CREATE OR REPLACE VIEW v_cuenta_contable_jerarquia AS
WITH RECURSIVE jerarquia AS (
    SELECT
        id, codigo, nombre, tipo, naturaleza, nivel,
        cuenta_padre_id, descripcion, permite_movimiento, es_centro_costo, activo,
        codigo AS codigo_completo,
        nombre AS ruta_nombre,
        1 AS profundidad
    FROM cuenta_contable
    WHERE cuenta_padre_id IS NULL

    UNION ALL

    SELECT
        cc.id, cc.codigo, cc.nombre, cc.tipo, cc.naturaleza, cc.nivel,
        cc.cuenta_padre_id, cc.descripcion, cc.permite_movimiento, cc.es_centro_costo, cc.activo,
        j.codigo_completo || ' > ' || cc.codigo,
        j.ruta_nombre || ' > ' || cc.nombre,
        j.profundidad + 1
    FROM cuenta_contable cc
    INNER JOIN jerarquia j ON cc.cuenta_padre_id = j.id
)
SELECT * FROM jerarquia
ORDER BY codigo;

COMMENT ON VIEW v_cuenta_contable_jerarquia IS 'Vista jerárquica del plan de cuentas';
