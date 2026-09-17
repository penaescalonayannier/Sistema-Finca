-- Reserva atómica del consecutivo diario AS-AAAAMMDD-NNNN.
-- Sustituye el cálculo en memoria, que podía duplicar números ante operaciones
-- simultáneas o cuando el proceso se reiniciaba con asientos históricos.
CREATE TABLE IF NOT EXISTS consecutivo_asiento_diario (
    fecha DATE PRIMARY KEY,
    ultimo_numero INTEGER NOT NULL DEFAULT 0 CHECK (ultimo_numero >= 0),
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Inicializa cada día ya utilizado sin cambiar ni renumerar asientos históricos.
INSERT INTO consecutivo_asiento_diario (fecha, ultimo_numero)
SELECT fecha,
       MAX(split_part(a.numero, '-', 3)::INTEGER)
FROM asiento_contable a
WHERE a.numero ~ '^AS-[0-9]{8}-[0-9]+$'
GROUP BY a.fecha
ON CONFLICT (fecha) DO UPDATE
SET ultimo_numero = GREATEST(
    consecutivo_asiento_diario.ultimo_numero,
    EXCLUDED.ultimo_numero
);

CREATE OR REPLACE FUNCTION generar_numero_asiento_diario(p_fecha DATE)
RETURNS VARCHAR(30) AS $$
DECLARE
    v_numero INTEGER;
    v_patron TEXT := '^AS-' || to_char(p_fecha, 'YYYYMMDD') || '-[0-9]+$';
BEGIN
    INSERT INTO consecutivo_asiento_diario (fecha, ultimo_numero)
    VALUES (p_fecha, 0)
    ON CONFLICT (fecha) DO NOTHING;

    -- UPDATE bloquea la fila del día hasta el final de la transacción. Además
    -- compara contra los históricos para admitir importaciones documentales.
    UPDATE consecutivo_asiento_diario
    SET ultimo_numero = GREATEST(
            ultimo_numero,
            COALESCE((
                SELECT MAX(split_part(a.numero, '-', 3)::INTEGER)
                FROM asiento_contable a
                WHERE a.numero ~ v_patron
            ), 0)
        ) + 1,
        actualizado_en = CURRENT_TIMESTAMP
    WHERE fecha = p_fecha
    RETURNING ultimo_numero INTO v_numero;

    RETURN 'AS-' || to_char(p_fecha, 'YYYYMMDD') || '-' || lpad(v_numero::TEXT, 4, '0');
END;
$$ LANGUAGE plpgsql;

COMMENT ON TABLE consecutivo_asiento_diario IS
    'Reserva transaccional de consecutivos de asientos AS-AAAAMMDD-NNNN.';
COMMENT ON FUNCTION generar_numero_asiento_diario(DATE) IS
    'Genera el siguiente número de asiento diario bajo bloqueo de fila.';
