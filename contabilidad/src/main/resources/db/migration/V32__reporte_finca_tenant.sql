-- Los partes de trabajo pertenecen a una finca. Se conserva nullable mientras
-- existan históricos sin responsable para no impedir su consulta administrativa.
ALTER TABLE reporte ADD COLUMN IF NOT EXISTS finca_id UUID;

UPDATE reporte r
SET finca_id = t.finca_id
FROM trabajador t
WHERE r.finca_id IS NULL
  AND r.trabajador_responsable_id = t.id;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_reporte_finca'
    ) THEN
        ALTER TABLE reporte
            ADD CONSTRAINT fk_reporte_finca
            FOREIGN KEY (finca_id) REFERENCES fincas(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_reporte_finca_periodo
    ON reporte(finca_id, year, mes);

COMMENT ON COLUMN reporte.finca_id IS
    'Finca propietaria del parte de trabajo; derivada del trabajador responsable.';
