-- Cantidad real de actividad que respalda el consumo de combustible imputado.
ALTER TABLE item_salida ADD COLUMN cantidad_actividad NUMERIC(19,4);
