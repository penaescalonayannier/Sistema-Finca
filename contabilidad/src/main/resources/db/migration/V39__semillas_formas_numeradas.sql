-- Catálogo inicial de formas. Las series se abren al emitir por primera vez
-- en su alcance; esta migración no altera números históricos.
INSERT INTO forma_numerada
    (id, codigo, nombre, referencia_modelo, prefijo, digitos, reinicio, alcance_predeterminado, modo_emision, activa)
VALUES
    ('10000000-0000-4000-8000-000000000001', 'FACTURA', 'Factura comercial', 'SC-2-12 / Factura digital', 'FAC', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000002', 'VALE_SALIDA', 'Vale de salida', 'SC-2-08', 'VALE', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000003', 'PRODUCCION_TERMINADA', 'Producción terminada', 'SC-2-06', 'PT', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000004', 'INFORME_RECEPCION', 'Informe de recepción', 'SC-2-04', 'IR', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000005', 'TRANSFERENCIA_ALMACEN', 'Transferencia entre almacenes', 'SC-2-09', 'SC2-09', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000006', 'CONTEO_FISICO', 'Conteo físico de almacén', 'SC-2-15', 'SC2-15', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000007', 'AJUSTE_INVENTARIO', 'Ajuste de inventario', 'SC-2-16', 'SC2-16', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000008', 'RECIBO_COBRO', 'Recibo de cobro', 'SC-3-01', 'REC', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000009', 'INGRESO_CAJA', 'Recibo de efectivo de caja', NULL, 'IC', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000010', 'VALE_PAGO_MENOR', 'Vale de pago menor', NULL, 'VPM', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000011', 'ANTICIPO_CAJA', 'Anticipo de caja', NULL, 'ANT', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000012', 'LIQUIDACION_ANTICIPO', 'Liquidación de anticipo', NULL, 'LA', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000013', 'REEMBOLSO_CAJA', 'Reembolso de caja', NULL, 'REEM', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000014', 'ENTREGA_BANCO', 'Entrega de efectivo al banco', NULL, 'EB', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000015', 'ENTREGA_DOCUMENTOS_CAJA', 'Entrega de documentos a caja', NULL, 'EDC', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000016', 'ARQUEO_CAJA', 'Arqueo de caja', 'SC-3-06', 'ARQ', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000017', 'ACTA_RESPONSABILIDAD_CAJA', 'Acta de responsabilidad de caja', NULL, 'ARC', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000018', 'CHEQUE', 'Cheque', NULL, 'CH', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000019', 'TRANSFERENCIA_BANCARIA', 'Transferencia bancaria', NULL, 'TRB', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000020', 'CONCILIACION_BANCARIA', 'Conciliación bancaria', NULL, 'CB', 5, 'ANUAL', 'FINCA', 'SISTEMA', TRUE),
    ('10000000-0000-4000-8000-000000000021', 'ASIENTO_CONTABLE', 'Asiento contable', NULL, 'AS', 4, 'ANUAL', 'ENTIDAD', 'SISTEMA', TRUE)
ON CONFLICT (codigo) DO NOTHING;
