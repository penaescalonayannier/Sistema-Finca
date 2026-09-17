-- Recibo y conteo se numeran por finca. Se elimina la unicidad global legado
-- para que dos fincas puedan emitir la misma forma sin colisión.
ALTER TABLE pago_deuda DROP CONSTRAINT IF EXISTS uk_pago_deuda_numero_recibo;
ALTER TABLE conteo_fisico_almacen DROP CONSTRAINT IF EXISTS conteo_fisico_almacen_numero_key;

CREATE UNIQUE INDEX IF NOT EXISTS uq_pago_deuda_finca_numero_recibo
    ON pago_deuda (finca_id, numero_recibo) WHERE numero_recibo IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_conteo_fisico_finca_numero
    ON conteo_fisico_almacen (finca_id, numero);
