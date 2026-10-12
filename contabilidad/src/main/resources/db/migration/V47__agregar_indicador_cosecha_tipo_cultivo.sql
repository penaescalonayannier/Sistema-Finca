-- Identifica los tipos de cultivo que representan una actividad de cosecha.
-- Las actividades marcadas se usarán para validar, en un paso posterior, que la
-- producción terminada respalde el importe salarial de los trabajadores.
ALTER TABLE tipo_cultivo
    ADD COLUMN IF NOT EXISTS es_cosecha BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN tipo_cultivo.es_cosecha IS
    'Indica si el tipo de cultivo corresponde a una actividad de cosecha.';
