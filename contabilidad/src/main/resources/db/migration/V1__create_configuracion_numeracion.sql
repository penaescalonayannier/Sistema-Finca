-- =====================================================
-- SPEC-001: Numeración Automática de Vales y Facturas
-- =====================================================

-- 1. Crear tabla de configuración de numeración
CREATE TABLE IF NOT EXISTS configuracion_numeracion (
    id UUID PRIMARY KEY,
    finca_id UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    prefijo VARCHAR(10) NOT NULL,
    anio INTEGER NOT NULL,
    ultimo_numero INTEGER NOT NULL DEFAULT 0,
    version BIGINT DEFAULT 0,

    CONSTRAINT fk_numeracion_finca FOREIGN KEY (finca_id) REFERENCES fincas(id),
    CONSTRAINT uk_numeracion_finca_tipo_anio UNIQUE (finca_id, tipo, anio)
);

-- 2. Índices para performance
CREATE INDEX IF NOT EXISTS idx_numeracion_finca ON configuracion_numeracion(finca_id);
CREATE INDEX IF NOT EXISTS idx_numeracion_tipo_anio ON configuracion_numeracion(tipo, anio);

-- 3. Migrar salidas existentes: asignar números secuenciales
-- Primero actualizamos las que no tienen número válido

-- 3.1 Crear función para extraer número de la secuencia
CREATE OR REPLACE FUNCTION extraer_numero_secuencia(numero_str VARCHAR)
RETURNS INTEGER AS $$
BEGIN
    -- Intenta extraer el número después del último guión
    IF numero_str IS NULL OR numero_str = '' THEN
        RETURN 0;
    END IF;

    -- Formato esperado: PREFIJO-XXXX o PREFIJO-YYYY-XXXXX
    RETURN COALESCE(
        NULLIF(REGEXP_REPLACE(numero_str, '^.*-0*', ''), '')::INTEGER,
        0
    );
EXCEPTION WHEN OTHERS THEN
    RETURN 0;
END;
$$ LANGUAGE plpgsql;

-- 3.2 Actualizar salidas existentes sin número con formato nuevo
WITH salidas_sin_numero AS (
    SELECT
        s.id,
        s.tipo,
        s.fecha,
        fp.finca_id,
        ROW_NUMBER() OVER (
            PARTITION BY fp.finca_id, s.tipo, EXTRACT(YEAR FROM s.fecha)
            ORDER BY s.fecha, s.id
        ) as num_secuencia,
        EXTRACT(YEAR FROM s.fecha)::INTEGER as anio
    FROM salida s
    JOIN finca_producto fp ON fp.id = s.finca_producto_id
    WHERE s.numero IS NULL OR s.numero = '' OR s.numero NOT LIKE '%-%'
)
UPDATE salida s
SET numero = CASE
    WHEN ssn.tipo = 'VALE' THEN 'VALE-' || ssn.anio || '-' || LPAD(ssn.num_secuencia::text, 5, '0')
    ELSE 'FAC-' || ssn.anio || '-' || LPAD(ssn.num_secuencia::text, 5, '0')
END
FROM salidas_sin_numero ssn
WHERE s.id = ssn.id;

-- 3.3 Inicializar configuración de numeración basada en salidas existentes
INSERT INTO configuracion_numeracion (id, finca_id, tipo, prefijo, anio, ultimo_numero)
SELECT
    gen_random_uuid(),
    fp.finca_id,
    CASE WHEN s.tipo = 'VALE' THEN 'VALE' ELSE 'FACTURA' END,
    CASE WHEN s.tipo = 'VALE' THEN 'VALE' ELSE 'FAC' END,
    EXTRACT(YEAR FROM s.fecha)::INTEGER,
    MAX(extraer_numero_secuencia(s.numero))
FROM salida s
JOIN finca_producto fp ON fp.id = s.finca_producto_id
WHERE s.numero IS NOT NULL AND s.numero != ''
GROUP BY fp.finca_id, s.tipo, EXTRACT(YEAR FROM s.fecha)
ON CONFLICT (finca_id, tipo, anio) DO UPDATE
SET ultimo_numero = GREATEST(configuracion_numeracion.ultimo_numero, EXCLUDED.ultimo_numero);

-- 3.4 Limpiar función temporal
DROP FUNCTION IF EXISTS extraer_numero_secuencia(VARCHAR);

-- 4. Agregar comentarios
COMMENT ON TABLE configuracion_numeracion IS 'Configuración de numeración secuencial por finca, tipo de documento y año';
COMMENT ON COLUMN configuracion_numeracion.tipo IS 'Tipo de documento: VALE, FACTURA, RECIBO';
COMMENT ON COLUMN configuracion_numeracion.prefijo IS 'Prefijo del número: VALE, FAC, REC';
COMMENT ON COLUMN configuracion_numeracion.ultimo_numero IS 'Último número usado en la secuencia';
COMMENT ON COLUMN configuracion_numeracion.version IS 'Versión para control de concurrencia optimista';
