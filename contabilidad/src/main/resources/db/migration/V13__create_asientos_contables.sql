-- ============================================================================
-- V13: Asientos Contables y Reglas de Contabilización Automática
-- ============================================================================
-- Este módulo genera asientos contables automáticamente desde movimientos físicos.
-- El usuario NO interactúa con estas tablas - son generadas por el sistema.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Tabla: regla_contabilizacion
-- Mapeo: TipoMovimientoStock → Cuentas Débito/Crédito
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS regla_contabilizacion (
    id UUID PRIMARY KEY,

    -- Condiciones del movimiento físico
    tipo_movimiento VARCHAR(50) NOT NULL,           -- TipoMovimientoStock enum
    almacen_id UUID REFERENCES almacen(id),         -- NULL = aplica a todos
    finca_id UUID REFERENCES finca(id),             -- NULL = aplica a todas
    tipo_producto VARCHAR(50),                      -- PRODUCCION, INSUMO, NULL = todos

    -- Cuentas resultantes
    cuenta_debito VARCHAR(20) NOT NULL,             -- Código cuenta que RECIBE
    cuenta_credito VARCHAR(20) NOT NULL,            -- Código cuenta que ENTREGA

    -- Centro de costo (opcional)
    centro_costo_debito VARCHAR(20),                -- Ej: "700.20" para Comedor
    centro_costo_credito VARCHAR(20),               -- Ej: "700.01" para Plan Vianda

    -- Configuración
    descripcion_plantilla VARCHAR(255),             -- "Entrada de {producto} desde {origen}"
    prioridad INTEGER DEFAULT 100,                  -- Mayor prioridad = se evalúa primero
    activo BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_regla_unica UNIQUE (tipo_movimiento, almacen_id, finca_id, tipo_producto)
);

CREATE INDEX idx_regla_tipo_mov ON regla_contabilizacion(tipo_movimiento);
CREATE INDEX idx_regla_almacen ON regla_contabilizacion(almacen_id);
CREATE INDEX idx_regla_finca ON regla_contabilizacion(finca_id);
CREATE INDEX idx_regla_prioridad ON regla_contabilizacion(prioridad DESC);
CREATE INDEX idx_regla_activo ON regla_contabilizacion(activo);

COMMENT ON TABLE regla_contabilizacion IS 'Reglas de mapeo MovimientoStock → AsientoContable';
COMMENT ON COLUMN regla_contabilizacion.prioridad IS 'Mayor número = mayor prioridad. Primera regla que coincida se aplica.';

-- ----------------------------------------------------------------------------
-- Tabla: asiento_contable
-- Asientos generados automáticamente desde movimientos físicos
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS asiento_contable (
    id UUID PRIMARY KEY,

    numero VARCHAR(30) NOT NULL UNIQUE,             -- AST-2025-000001
    fecha DATE NOT NULL,
    descripcion VARCHAR(500),

    -- Trazabilidad al movimiento físico origen
    movimiento_stock_id UUID REFERENCES movimiento_stock(id),
    tabla_origen VARCHAR(50) DEFAULT 'movimiento_stock',

    -- Totales (calculados)
    total_debe DECIMAL(15,2) NOT NULL DEFAULT 0,
    total_haber DECIMAL(15,2) NOT NULL DEFAULT 0,

    -- Control
    asentado BOOLEAN DEFAULT TRUE,                  -- Auto-asentado al crear
    fecha_asentado TIMESTAMP,
    usuario_asento VARCHAR(100) DEFAULT 'SISTEMA',

    -- Referencia a regla aplicada
    regla_id UUID REFERENCES regla_contabilizacion(id),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_asiento_numero ON asiento_contable(numero);
CREATE INDEX idx_asiento_fecha ON asiento_contable(fecha);
CREATE INDEX idx_asiento_movimiento ON asiento_contable(movimiento_stock_id);
CREATE INDEX idx_asiento_asentado ON asiento_contable(asentado);
CREATE INDEX idx_asiento_regla ON asiento_contable(regla_id);

COMMENT ON TABLE asiento_contable IS 'Asientos contables auto-generados desde movimientos físicos';

-- ----------------------------------------------------------------------------
-- Tabla: linea_asiento
-- Líneas de cada asiento (partida doble: DEBE = HABER)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS linea_asiento (
    id UUID PRIMARY KEY,
    asiento_id UUID NOT NULL REFERENCES asiento_contable(id) ON DELETE CASCADE,

    codigo_cuenta VARCHAR(20) NOT NULL,             -- Referencia a cuenta_contable.codigo
    nombre_cuenta VARCHAR(150),                     -- Cache para reportes
    centro_costo VARCHAR(20),                       -- Código centro de costo

    debe DECIMAL(15,2) NOT NULL DEFAULT 0,
    haber DECIMAL(15,2) NOT NULL DEFAULT 0,

    concepto VARCHAR(255),

    orden INTEGER DEFAULT 1,                        -- Orden dentro del asiento

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_debe_o_haber CHECK (
        (debe > 0 AND haber = 0) OR (debe = 0 AND haber > 0) OR (debe = 0 AND haber = 0)
    )
);

CREATE INDEX idx_linea_asiento ON linea_asiento(asiento_id);
CREATE INDEX idx_linea_cuenta ON linea_asiento(codigo_cuenta);
CREATE INDEX idx_linea_centro_costo ON linea_asiento(centro_costo);

COMMENT ON TABLE linea_asiento IS 'Líneas del asiento contable (partida doble)';

-- ----------------------------------------------------------------------------
-- Tabla: configuracion_numeracion_asiento
-- Numeración automática de asientos por año
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS configuracion_numeracion_asiento (
    id UUID PRIMARY KEY,
    anio INTEGER NOT NULL UNIQUE,
    prefijo VARCHAR(10) DEFAULT 'AST',
    ultimo_numero INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inicializar año actual
INSERT INTO configuracion_numeracion_asiento (id, anio, prefijo, ultimo_numero)
VALUES (gen_random_uuid(), EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER, 'AST', 0)
ON CONFLICT (anio) DO NOTHING;

-- ============================================================================
-- SEED: Reglas de Contabilización por Defecto
-- Mapeo TipoMovimientoStock → Cuentas Contables
-- ============================================================================

-- ENTRADAS DE PRODUCCIÓN: D: 189 (Inventario) / H: 193 (Prod. Terminada)
INSERT INTO regla_contabilizacion (id, tipo_movimiento, cuenta_debito, cuenta_credito, centro_costo_credito, descripcion_plantilla, prioridad)
VALUES
    (gen_random_uuid(), 'ENTRADA_PRODUCCION', '189', '193.01', '700.01',
     'Entrada producción agrícola: {producto}', 100);

-- ENTRADA POR FACTURA (compra externa): D: 189 / H: 410 (Proveedores)
INSERT INTO regla_contabilizacion (id, tipo_movimiento, cuenta_debito, cuenta_credito, descripcion_plantilla, prioridad)
VALUES
    (gen_random_uuid(), 'ENTRADA_FACTURA', '189', '410',
     'Compra por factura: {producto}', 100);

-- ENTRADA POR CONDUCE: D: 189 / H: 410
INSERT INTO regla_contabilizacion (id, tipo_movimiento, cuenta_debito, cuenta_credito, descripcion_plantilla, prioridad)
VALUES
    (gen_random_uuid(), 'ENTRADA_CONDUCE', '189', '410',
     'Entrada por conduce: {producto}', 100);

-- STOCK INICIAL: D: 189 / H: 500 (Inversión Estatal/Capital)
INSERT INTO regla_contabilizacion (id, tipo_movimiento, cuenta_debito, cuenta_credito, descripcion_plantilla, prioridad)
VALUES
    (gen_random_uuid(), 'STOCK_INICIAL', '189', '500',
     'Stock inicial: {producto}', 100);

-- SALIDA AUTOCONSUMO (a trabajadores): D: 700.21 (Autoconsumo) / H: 189
INSERT INTO regla_contabilizacion (id, tipo_movimiento, cuenta_debito, cuenta_credito, centro_costo_debito, descripcion_plantilla, prioridad)
VALUES
    (gen_random_uuid(), 'SALIDA_AUTOCONSUMO', '819', '189', '700.21',
     'Salida autoconsumo trabajadores: {producto}', 100);

-- SALIDA VENTA: D: 810 (Costo de Ventas) / H: 189
INSERT INTO regla_contabilizacion (id, tipo_movimiento, cuenta_debito, cuenta_credito, descripcion_plantilla, prioridad)
VALUES
    (gen_random_uuid(), 'SALIDA_VENTA', '810', '189',
     'Venta de producto: {producto}', 100);

-- TRANSFERENCIA SALIDA: D: 189 (destino) / H: 189 (origen) - se maneja por almacén
INSERT INTO regla_contabilizacion (id, tipo_movimiento, cuenta_debito, cuenta_credito, descripcion_plantilla, prioridad)
VALUES
    (gen_random_uuid(), 'TRANSFERENCIA_SALIDA', '189', '189',
     'Transferencia entre almacenes: {producto}', 100);

-- TRANSFERENCIA ENTRADA: No genera asiento adicional (ya se generó en SALIDA)
-- Se omite intencionalmente para evitar duplicar

-- AJUSTE MANUAL ENTRADA: D: 189 / H: 690 (Otros Ingresos)
INSERT INTO regla_contabilizacion (id, tipo_movimiento, cuenta_debito, cuenta_credito, descripcion_plantilla, prioridad)
VALUES
    (gen_random_uuid(), 'ENTRADA_AJUSTE', '189', '690',
     'Ajuste positivo inventario: {producto}', 100);

-- AJUSTE MANUAL SALIDA: D: 890 (Otros Gastos) / H: 189
INSERT INTO regla_contabilizacion (id, tipo_movimiento, cuenta_debito, cuenta_credito, descripcion_plantilla, prioridad)
VALUES
    (gen_random_uuid(), 'SALIDA_AJUSTE', '890', '189',
     'Ajuste negativo inventario: {producto}', 100);

-- DEVOLUCIÓN: D: 189 / H: 136 (Cuentas por Cobrar Clientes)
INSERT INTO regla_contabilizacion (id, tipo_movimiento, cuenta_debito, cuenta_credito, descripcion_plantilla, prioridad)
VALUES
    (gen_random_uuid(), 'DEVOLUCION', '189', '136',
     'Devolución de producto: {producto}', 100);

-- ============================================================================
-- Función para generar número de asiento
-- ============================================================================
CREATE OR REPLACE FUNCTION generar_numero_asiento()
RETURNS VARCHAR(30) AS $$
DECLARE
    v_anio INTEGER;
    v_numero INTEGER;
    v_prefijo VARCHAR(10);
    v_resultado VARCHAR(30);
BEGIN
    v_anio := EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER;

    -- Obtener o crear configuración del año
    INSERT INTO configuracion_numeracion_asiento (id, anio, prefijo, ultimo_numero)
    VALUES (gen_random_uuid(), v_anio, 'AST', 0)
    ON CONFLICT (anio) DO NOTHING;

    -- Incrementar y obtener número
    UPDATE configuracion_numeracion_asiento
    SET ultimo_numero = ultimo_numero + 1,
        updated_at = CURRENT_TIMESTAMP
    WHERE anio = v_anio
    RETURNING ultimo_numero, prefijo INTO v_numero, v_prefijo;

    -- Formato: AST-2025-000001
    v_resultado := v_prefijo || '-' || v_anio || '-' || LPAD(v_numero::TEXT, 6, '0');

    RETURN v_resultado;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION generar_numero_asiento IS 'Genera número único de asiento: AST-AAAA-NNNNNN';

-- ============================================================================
-- Vista: Resumen de asientos por período
-- ============================================================================
CREATE OR REPLACE VIEW v_resumen_asientos AS
SELECT
    DATE_TRUNC('month', fecha) AS periodo,
    COUNT(*) AS total_asientos,
    SUM(total_debe) AS total_debe,
    SUM(total_haber) AS total_haber,
    COUNT(*) FILTER (WHERE asentado = true) AS asentados,
    COUNT(*) FILTER (WHERE asentado = false) AS pendientes
FROM asiento_contable
GROUP BY DATE_TRUNC('month', fecha)
ORDER BY periodo DESC;

-- ============================================================================
-- Vista: Movimientos por cuenta
-- ============================================================================
CREATE OR REPLACE VIEW v_movimientos_cuenta AS
SELECT
    la.codigo_cuenta,
    la.nombre_cuenta,
    la.centro_costo,
    ac.fecha,
    ac.numero AS numero_asiento,
    ac.descripcion,
    la.debe,
    la.haber,
    la.concepto
FROM linea_asiento la
JOIN asiento_contable ac ON la.asiento_id = ac.id
WHERE ac.asentado = true
ORDER BY la.codigo_cuenta, ac.fecha DESC;

COMMENT ON VIEW v_movimientos_cuenta IS 'Movimientos contables por cuenta para reportes';
