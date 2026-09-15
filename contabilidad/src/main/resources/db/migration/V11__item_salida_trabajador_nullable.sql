-- V11: Make trabajador_id nullable in item_salida
-- Required for COMEDOR type salidas where there's no specific worker

ALTER TABLE item_salida ALTER COLUMN trabajador_id DROP NOT NULL;
