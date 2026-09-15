-- =====================================================
-- SPEC-002: Comprobante de Pago de Deuda (PDF)
-- =====================================================

-- 1. Add new columns to pago_deuda table
ALTER TABLE pago_deuda
ADD COLUMN IF NOT EXISTS numero_recibo VARCHAR(20),
ADD COLUMN IF NOT EXISTS saldo_anterior DOUBLE PRECISION,
ADD COLUMN IF NOT EXISTS saldo_nuevo DOUBLE PRECISION,
ADD COLUMN IF NOT EXISTS concepto VARCHAR(255),
ADD COLUMN IF NOT EXISTS finca_id UUID;

-- 2. Add unique constraint for numero_recibo
ALTER TABLE pago_deuda
ADD CONSTRAINT uk_pago_deuda_numero_recibo UNIQUE (numero_recibo);

-- 3. Add foreign key to fincas
ALTER TABLE pago_deuda
ADD CONSTRAINT fk_pago_deuda_finca FOREIGN KEY (finca_id) REFERENCES fincas(id);

-- 4. Create index for performance
CREATE INDEX IF NOT EXISTS idx_pago_deuda_finca ON pago_deuda(finca_id);
CREATE INDEX IF NOT EXISTS idx_pago_deuda_fecha ON pago_deuda(fecha);

-- 5. Initialize configuracion_numeracion for RECIBO type from existing payments
-- First, get the finca_id from trabajador for existing payments
WITH pagos_con_finca AS (
    SELECT
        pd.id,
        pd.fecha,
        t.finca_id,
        ROW_NUMBER() OVER (
            PARTITION BY t.finca_id, EXTRACT(YEAR FROM pd.fecha)
            ORDER BY pd.fecha, pd.id
        ) as num_secuencia,
        EXTRACT(YEAR FROM pd.fecha)::INTEGER as anio
    FROM pago_deuda pd
    JOIN trabajador t ON t.id = pd.trabajador_id
    WHERE pd.numero_recibo IS NULL
      AND t.finca_id IS NOT NULL
)
UPDATE pago_deuda p
SET numero_recibo = 'REC-' || pcf.anio || '-' || LPAD(pcf.num_secuencia::text, 5, '0'),
    finca_id = pcf.finca_id,
    concepto = 'Pago de deuda'
FROM pagos_con_finca pcf
WHERE p.id = pcf.id;

-- 6. Initialize configuracion_numeracion for RECIBO type
INSERT INTO configuracion_numeracion (id, finca_id, tipo, prefijo, anio, ultimo_numero)
SELECT
    gen_random_uuid(),
    t.finca_id,
    'RECIBO',
    'REC',
    EXTRACT(YEAR FROM pd.fecha)::INTEGER,
    COUNT(*)
FROM pago_deuda pd
JOIN trabajador t ON t.id = pd.trabajador_id
WHERE pd.numero_recibo IS NOT NULL
  AND t.finca_id IS NOT NULL
GROUP BY t.finca_id, EXTRACT(YEAR FROM pd.fecha)
ON CONFLICT (finca_id, tipo, anio) DO UPDATE
SET ultimo_numero = GREATEST(configuracion_numeracion.ultimo_numero, EXCLUDED.ultimo_numero);

-- 7. Add comments
COMMENT ON COLUMN pago_deuda.numero_recibo IS 'Receipt number in format REC-YYYY-NNNNN';
COMMENT ON COLUMN pago_deuda.saldo_anterior IS 'Debt balance before this payment';
COMMENT ON COLUMN pago_deuda.saldo_nuevo IS 'Debt balance after this payment';
COMMENT ON COLUMN pago_deuda.concepto IS 'Payment description/concept';
COMMENT ON COLUMN pago_deuda.finca_id IS 'Farm associated with the worker/payment';
