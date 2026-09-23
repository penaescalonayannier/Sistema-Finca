-- Dato laboral complementario. Nullable para conservar los expedientes históricos.
ALTER TABLE trabajador
    ADD COLUMN IF NOT EXISTS nivel_cultural VARCHAR(100);
