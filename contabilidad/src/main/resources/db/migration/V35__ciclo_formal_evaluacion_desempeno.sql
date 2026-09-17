-- Extensión aditiva: los registros existentes quedan como borrador y continúan editables.
ALTER TABLE evaluacion ADD COLUMN IF NOT EXISTS estado varchar(20) NOT NULL DEFAULT 'BORRADOR';
ALTER TABLE evaluacion ADD COLUMN IF NOT EXISTS evidencia varchar(4000);
ALTER TABLE evaluacion ADD COLUMN IF NOT EXISTS criterios_aplicados varchar(8000);
ALTER TABLE evaluacion ADD COLUMN IF NOT EXISTS constancia_jefe varchar(500);
ALTER TABLE evaluacion ADD COLUMN IF NOT EXISTS constancia_trabajador varchar(500);
ALTER TABLE evaluacion ADD COLUMN IF NOT EXISTS fecha_envio timestamp;
ALTER TABLE evaluacion ADD COLUMN IF NOT EXISTS fecha_cierre timestamp;
ALTER TABLE evaluacion ADD COLUMN IF NOT EXISTS observaciones_cierre varchar(2000);

CREATE TABLE IF NOT EXISTS criterio_evaluacion (
    id uuid PRIMARY KEY,
    nombre varchar(150) NOT NULL,
    descripcion varchar(1000),
    activo boolean NOT NULL DEFAULT true,
    orden integer NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_evaluacion_estado ON evaluacion(estado);
CREATE INDEX IF NOT EXISTS idx_criterio_evaluacion_activo_orden
    ON criterio_evaluacion(activo, orden, nombre);

-- Catálogo inicial editable; no se sustituye ni se borra si la entidad ya lo configuró.
INSERT INTO criterio_evaluacion (id, nombre, descripcion, activo, orden)
SELECT 'a1111111-1111-4111-8111-111111111111'::uuid, 'Resultados del trabajo',
       'Cumplimiento verificable de las tareas y metas del período.', true, 10
WHERE NOT EXISTS (SELECT 1 FROM criterio_evaluacion);

INSERT INTO criterio_evaluacion (id, nombre, descripcion, activo, orden)
SELECT 'a2222222-2222-4222-8222-222222222222'::uuid, 'Disciplina y conducta laboral',
       'Puntualidad, cumplimiento de normas y conducta profesional.', true, 20
WHERE NOT EXISTS (SELECT 1 FROM criterio_evaluacion WHERE nombre = 'Disciplina y conducta laboral');

INSERT INTO criterio_evaluacion (id, nombre, descripcion, activo, orden)
SELECT 'a3333333-3333-4333-8333-333333333333'::uuid, 'Calidad y colaboración',
       'Calidad del resultado, cooperación y aporte al colectivo.', true, 30
WHERE NOT EXISTS (SELECT 1 FROM criterio_evaluacion WHERE nombre = 'Calidad y colaboración');
