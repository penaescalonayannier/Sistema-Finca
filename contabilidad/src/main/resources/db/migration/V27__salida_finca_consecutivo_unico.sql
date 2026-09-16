-- Un consecutivo de vale/factura es único dentro de la finca que lo emite.
-- La finca se materializa en el documento para que PostgreSQL pueda imponer
-- la regla, sin depender de un JOIN con finca_producto en un índice.
ALTER TABLE salida ADD COLUMN IF NOT EXISTS finca_id UUID;

UPDATE salida s
SET finca_id = fp.finca_id
FROM finca_producto fp
WHERE s.finca_producto_id = fp.id
  AND s.finca_id IS NULL;

ALTER TABLE salida ALTER COLUMN finca_id SET NOT NULL;

-- Instalaciones antiguas podían tener números repetidos dentro de una finca.
-- Se conserva el primer documento por fecha/id y se marca únicamente los
-- duplicados históricos con un sufijo estable antes de imponer la unicidad.
WITH duplicados AS (
    SELECT id, ROW_NUMBER() OVER (
        PARTITION BY finca_id, numero
        ORDER BY fecha, id
    ) AS posicion
    FROM salida
)
UPDATE salida s
SET numero = s.numero || '-HIST-DUP-' || duplicados.posicion
FROM duplicados
WHERE s.id = duplicados.id
  AND duplicados.posicion > 1;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_salida_finca') THEN
        ALTER TABLE salida ADD CONSTRAINT fk_salida_finca
            FOREIGN KEY (finca_id) REFERENCES fincas(id);
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS uq_salida_finca_numero
    ON salida (finca_id, numero);
