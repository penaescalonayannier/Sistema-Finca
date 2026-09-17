-- Las liquidaciones de salidas y entregas de efectivo al banco son documentos
-- primarios distintos. Los registros históricos se preservan sin renumerar:
-- numero_documento queda NULL hasta que el motor de formas emita su serie.
--
-- La forma se identifica desde emision_forma_numerada por
-- (documento_tipo, documento_id); no se guarda una FK aquí para permitir la
-- implantación progresiva y evitar referencias circulares durante el alta.

ALTER TABLE liquidacion_salida
    ADD COLUMN IF NOT EXISTS numero_documento VARCHAR(80);

ALTER TABLE entrega_banco
    ADD COLUMN IF NOT EXISTS numero_documento VARCHAR(80);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'ck_liquidacion_salida_numero_documento_no_vacio'
          AND conrelid = 'liquidacion_salida'::regclass
    ) THEN
        ALTER TABLE liquidacion_salida
            ADD CONSTRAINT ck_liquidacion_salida_numero_documento_no_vacio
            CHECK (numero_documento IS NULL OR BTRIM(numero_documento) <> '');
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'ck_entrega_banco_numero_documento_no_vacio'
          AND conrelid = 'entrega_banco'::regclass
    ) THEN
        ALTER TABLE entrega_banco
            ADD CONSTRAINT ck_entrega_banco_numero_documento_no_vacio
            CHECK (numero_documento IS NULL OR BTRIM(numero_documento) <> '');
    END IF;
END $$;

-- Índices parciales: no cambian ni bloquean históricos sin número. Cuando un
-- documento sea emitido, su representación visible será única en su finca.
CREATE UNIQUE INDEX IF NOT EXISTS uq_liquidacion_salida_finca_numero_documento
    ON liquidacion_salida (finca_id, numero_documento)
    WHERE numero_documento IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_entrega_banco_finca_numero_documento
    ON entrega_banco (finca_id, numero_documento)
    WHERE numero_documento IS NOT NULL;

COMMENT ON COLUMN liquidacion_salida.numero_documento IS
    'Consecutivo visible de la forma ENTREGA_DOCUMENTOS_CAJA; NULL solo para legado previo al registro de formas.';

COMMENT ON COLUMN entrega_banco.numero_documento IS
    'Consecutivo visible de la forma ENTREGA_BANCO; NULL solo para legado previo al registro de formas.';
