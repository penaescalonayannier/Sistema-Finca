-- V9: Datos de prueba para Gestión de Productos por Finca
-- Este script crea datos de prueba para probar el flujo completo de entradas/salidas de stock

-- ====================================
-- 1. PRODUCTOS DE PRUEBA
-- ====================================

-- Productos de tipo INSUMO
INSERT INTO productos (id, code, name, unidad_medida, description, price, price_trabajador, price_comedor, stock, active, tipo_producto)
VALUES
    ('a1111111-1111-1111-1111-111111111111', 'INS-001', 'Fertilizante NPK 15-15-15', 'KG', 'Fertilizante balanceado para cultivos', 45.00, 40.00, 38.00, 0, true, 'INSUMO'),
    ('a2222222-2222-2222-2222-222222222222', 'INS-002', 'Herbicida Glifosato', 'UNIDAD', 'Herbicida sistémico no selectivo', 85.00, 80.00, 75.00, 0, true, 'INSUMO'),
    ('a3333333-3333-3333-3333-333333333333', 'INS-003', 'Insecticida Orgánico', 'UNIDAD', 'Control de plagas orgánico', 120.00, 110.00, 105.00, 0, true, 'INSUMO'),
    ('a4444444-4444-4444-4444-444444444444', 'INS-004', 'Semillas de Maíz Híbrido', 'KG', 'Semillas certificadas de alto rendimiento', 200.00, 180.00, 175.00, 0, true, 'INSUMO'),
    ('a5555555-5555-5555-5555-555555555555', 'INS-005', 'Abono Orgánico Compost', 'QUINTAL', 'Abono orgánico procesado', 35.00, 30.00, 28.00, 0, true, 'INSUMO')
ON CONFLICT (code) DO NOTHING;

-- Productos de tipo PRODUCCION
INSERT INTO productos (id, code, name, unidad_medida, description, price, price_trabajador, price_comedor, stock, active, tipo_producto)
VALUES
    ('b1111111-1111-1111-1111-111111111111', 'PRD-001', 'Café Pergamino Seco', 'QUINTAL', 'Café de primera calidad secado al sol', 2500.00, 2300.00, 2200.00, 0, true, 'PRODUCCION'),
    ('b2222222-2222-2222-2222-222222222222', 'PRD-002', 'Cacao en Grano', 'KG', 'Cacao fermentado y seco', 150.00, 140.00, 135.00, 0, true, 'PRODUCCION'),
    ('b3333333-3333-3333-3333-333333333333', 'PRD-003', 'Plátano Verde', 'UNIDAD', 'Plátano para cocinar', 5.00, 4.50, 4.00, 0, true, 'PRODUCCION'),
    ('b4444444-4444-4444-4444-444444444444', 'PRD-004', 'Yuca Fresca', 'KG', 'Yuca de cultivo local', 8.00, 7.00, 6.50, 0, true, 'PRODUCCION'),
    ('b5555555-5555-5555-5555-555555555555', 'PRD-005', 'Maíz en Mazorca', 'UNIDAD', 'Maíz tierno para consumo', 3.00, 2.50, 2.00, 0, true, 'PRODUCCION')
ON CONFLICT (code) DO NOTHING;

-- ====================================
-- 2. ASIGNACIÓN DE PRODUCTOS A FINCA
-- ====================================
-- Nota: Se usa la primera finca disponible en el sistema
-- Si no hay fincas, estos inserts fallarán silenciosamente

DO $$
DECLARE
    v_finca_id UUID;
BEGIN
    -- Obtener la primera finca activa
    SELECT id INTO v_finca_id FROM fincas WHERE activo = true LIMIT 1;

    IF v_finca_id IS NOT NULL THEN
        -- Asignar productos de INSUMO a la finca
        INSERT INTO finca_producto (id, finca_id, producto_id, stock, stock_minimo, stock_maximo, activo)
        VALUES
            ('c1111111-1111-1111-1111-111111111111', v_finca_id, 'a1111111-1111-1111-1111-111111111111', 150, 50, 500, true),
            ('c2222222-2222-2222-2222-222222222222', v_finca_id, 'a2222222-2222-2222-2222-222222222222', 30, 10, 100, true),
            ('c3333333-3333-3333-3333-333333333333', v_finca_id, 'a3333333-3333-3333-3333-333333333333', 25, 5, 50, true),
            ('c4444444-4444-4444-4444-444444444444', v_finca_id, 'a4444444-4444-4444-4444-444444444444', 80, 20, 200, true),
            ('c5555555-5555-5555-5555-555555555555', v_finca_id, 'a5555555-5555-5555-5555-555555555555', 200, 100, 1000, true)
        ON CONFLICT DO NOTHING;

        -- Asignar productos de PRODUCCION a la finca
        INSERT INTO finca_producto (id, finca_id, producto_id, stock, stock_minimo, stock_maximo, activo)
        VALUES
            ('d1111111-1111-1111-1111-111111111111', v_finca_id, 'b1111111-1111-1111-1111-111111111111', 45, 10, 100, true),
            ('d2222222-2222-2222-2222-222222222222', v_finca_id, 'b2222222-2222-2222-2222-222222222222', 120, 30, 500, true),
            ('d3333333-3333-3333-3333-333333333333', v_finca_id, 'b3333333-3333-3333-3333-333333333333', 500, 100, 2000, true),
            ('d4444444-4444-4444-4444-444444444444', v_finca_id, 'b4444444-4444-4444-4444-444444444444', 300, 50, 1000, true),
            ('d5555555-5555-5555-5555-555555555555', v_finca_id, 'b5555555-5555-5555-5555-555555555555', 800, 200, 3000, true)
        ON CONFLICT DO NOTHING;

        -- ====================================
        -- 3. MOVIMIENTOS DE STOCK (ENTRADAS)
        -- ====================================

        -- Entradas de producción
        INSERT INTO movimiento_stock (id, finca_producto_id, finca_id, producto_id, tipo, cantidad, stock_anterior, stock_nuevo, descripcion, observaciones, fecha)
        VALUES
            -- Fertilizante NPK: entrada inicial
            ('e1111111-1111-1111-1111-111111111111', 'c1111111-1111-1111-1111-111111111111', v_finca_id, 'a1111111-1111-1111-1111-111111111111',
             'STOCK_INICIAL', 100, 0, 100, 'Stock inicial de fertilizante', 'Inventario inicial del sistema', NOW() - INTERVAL '30 days'),
            ('e1111111-1111-1111-1111-111111111112', 'c1111111-1111-1111-1111-111111111111', v_finca_id, 'a1111111-1111-1111-1111-111111111111',
             'ENTRADA_FACTURA', 50, 100, 150, 'Compra factura #F-2024-001', 'Proveedor: Agroquímicos S.A.', NOW() - INTERVAL '15 days'),

            -- Café Pergamino: entradas de producción
            ('e2111111-1111-1111-1111-111111111111', 'd1111111-1111-1111-1111-111111111111', v_finca_id, 'b1111111-1111-1111-1111-111111111111',
             'STOCK_INICIAL', 20, 0, 20, 'Stock inicial de café', 'Inventario inicial', NOW() - INTERVAL '30 days'),
            ('e2111111-1111-1111-1111-111111111112', 'd1111111-1111-1111-1111-111111111111', v_finca_id, 'b1111111-1111-1111-1111-111111111111',
             'ENTRADA_PRODUCCION', 15, 20, 35, 'Cosecha semana 1', 'Lote Norte - Primera recolección', NOW() - INTERVAL '20 days'),
            ('e2111111-1111-1111-1111-111111111113', 'd1111111-1111-1111-1111-111111111111', v_finca_id, 'b1111111-1111-1111-1111-111111111111',
             'ENTRADA_PRODUCCION', 10, 35, 45, 'Cosecha semana 2', 'Lote Sur - Segunda recolección', NOW() - INTERVAL '10 days'),

            -- Plátano: entradas de producción variadas
            ('e3111111-1111-1111-1111-111111111111', 'd3333333-3333-3333-3333-333333333333', v_finca_id, 'b3333333-3333-3333-3333-333333333333',
             'STOCK_INICIAL', 200, 0, 200, 'Stock inicial plátano', 'Inventario inicial', NOW() - INTERVAL '30 days'),
            ('e3111111-1111-1111-1111-111111111112', 'd3333333-3333-3333-3333-333333333333', v_finca_id, 'b3333333-3333-3333-3333-333333333333',
             'ENTRADA_PRODUCCION', 150, 200, 350, 'Cosecha lote A', 'Parcela principal', NOW() - INTERVAL '25 days'),
            ('e3111111-1111-1111-1111-111111111113', 'd3333333-3333-3333-3333-333333333333', v_finca_id, 'b3333333-3333-3333-3333-333333333333',
             'ENTRADA_PRODUCCION', 200, 350, 550, 'Cosecha lote B', 'Parcela secundaria', NOW() - INTERVAL '18 days')
        ON CONFLICT DO NOTHING;

        -- ====================================
        -- 4. MOVIMIENTOS DE STOCK (SALIDAS)
        -- ====================================

        INSERT INTO movimiento_stock (id, finca_producto_id, finca_id, producto_id, tipo, cantidad, stock_anterior, stock_nuevo, descripcion, observaciones, fecha)
        VALUES
            -- Salidas de plátano
            ('f1111111-1111-1111-1111-111111111111', 'd3333333-3333-3333-3333-333333333333', v_finca_id, 'b3333333-3333-3333-3333-333333333333',
             'SALIDA_VENTA', 30, 550, 520, 'Venta a trabajadores', 'Distribución semanal', NOW() - INTERVAL '12 days'),
            ('f1111111-1111-1111-1111-111111111112', 'd3333333-3333-3333-3333-333333333333', v_finca_id, 'b3333333-3333-3333-3333-333333333333',
             'SALIDA_AUTOCONSUMO', 20, 520, 500, 'Consumo comedor', 'Almuerzo trabajadores', NOW() - INTERVAL '8 days'),

            -- Salida de fertilizante (uso en campo)
            ('f2111111-1111-1111-1111-111111111111', 'c1111111-1111-1111-1111-111111111111', v_finca_id, 'a1111111-1111-1111-1111-111111111111',
             'SALIDA_AUTOCONSUMO', 25, 150, 125, 'Aplicación en lote Norte', 'Fertilización de temporada', NOW() - INTERVAL '5 days')
        ON CONFLICT DO NOTHING;

        -- Actualizar stock actual en finca_producto basado en movimientos
        UPDATE finca_producto SET stock = 150 WHERE id = 'c1111111-1111-1111-1111-111111111111';
        UPDATE finca_producto SET stock = 45 WHERE id = 'd1111111-1111-1111-1111-111111111111';
        UPDATE finca_producto SET stock = 500 WHERE id = 'd3333333-3333-3333-3333-333333333333';

        RAISE NOTICE 'Datos de prueba insertados correctamente para finca: %', v_finca_id;
    ELSE
        RAISE NOTICE 'No se encontró ninguna finca activa. No se insertaron datos de prueba.';
    END IF;
END $$;
