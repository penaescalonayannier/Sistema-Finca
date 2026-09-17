-- Conserva las series ya emitidas por el contador histórico. Las nuevas
-- emisiones del motor de formas comienzan después del último número conocido
-- y no cambian la representación visible de documentos anteriores.
INSERT INTO serie_forma_numerada
    (id, forma_id, alcance_tipo, alcance_id, anio, prefijo,
     numero_inicial, ultimo_numero, estado, fecha_inicio, creado_en, actualizado_en, version)
SELECT
    gen_random_uuid(),
    forma.id,
    'FINCA',
    configuracion.finca_id,
    configuracion.anio,
    configuracion.prefijo,
    configuracion.ultimo_numero + 1,
    configuracion.ultimo_numero,
    'ACTIVA',
    make_date(configuracion.anio, 1, 1),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
FROM configuracion_numeracion configuracion
JOIN forma_numerada forma ON forma.codigo = CASE configuracion.tipo
    WHEN 'FACTURA' THEN 'FACTURA'
    WHEN 'VALE' THEN 'VALE_SALIDA'
    WHEN 'PRODUCCION' THEN 'PRODUCCION_TERMINADA'
    WHEN 'RECEPCION' THEN 'INFORME_RECEPCION'
    WHEN 'TRANSFERENCIA_ALMACEN' THEN 'TRANSFERENCIA_ALMACEN'
    WHEN 'CONTEO_FISICO' THEN 'CONTEO_FISICO'
    WHEN 'AJUSTE_INVENTARIO' THEN 'AJUSTE_INVENTARIO'
    WHEN 'RECIBO' THEN 'RECIBO_COBRO'
    ELSE NULL
END
WHERE NOT EXISTS (
    SELECT 1
    FROM serie_forma_numerada serie
    WHERE serie.forma_id = forma.id
      AND serie.alcance_tipo = 'FINCA'
      AND serie.alcance_id = configuracion.finca_id
      AND serie.anio = configuracion.anio
      AND serie.estado = 'ACTIVA'
);
