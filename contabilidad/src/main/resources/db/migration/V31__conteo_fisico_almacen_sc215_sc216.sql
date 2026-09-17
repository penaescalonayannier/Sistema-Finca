-- Modelos SC-2-15 (inventario físico) y SC-2-16 (ajustes por diferencias).
CREATE TABLE conteo_fisico_almacen (
 id UUID PRIMARY KEY, finca_id UUID NOT NULL REFERENCES fincas(id), almacen_id UUID NOT NULL REFERENCES almacenes(id),
 numero VARCHAR(40) NOT NULL UNIQUE, estado VARCHAR(15) NOT NULL, fecha_apertura TIMESTAMP NOT NULL, fecha_cierre TIMESTAMP,
 responsable_conteo VARCHAR(150) NOT NULL, verificado_por VARCHAR(150), autorizado_por VARCHAR(150),
 observaciones_apertura VARCHAR(1000), observaciones_cierre VARCHAR(1000), numero_ajuste VARCHAR(40), usuario_id UUID
);
CREATE INDEX idx_conteo_fisico_finca_fecha ON conteo_fisico_almacen(finca_id, fecha_apertura DESC);
CREATE INDEX idx_conteo_fisico_almacen_estado ON conteo_fisico_almacen(almacen_id, estado);
CREATE UNIQUE INDEX uk_conteo_fisico_almacen_abierto ON conteo_fisico_almacen(almacen_id) WHERE estado = 'ABIERTO';
CREATE TABLE conteo_fisico_linea (
 id UUID PRIMARY KEY, conteo_id UUID NOT NULL REFERENCES conteo_fisico_almacen(id), almacen_finca_producto_id UUID NOT NULL REFERENCES almacen_finca_producto(id),
 producto_codigo VARCHAR(80) NOT NULL, producto_nombre VARCHAR(200) NOT NULL, unidad_medida VARCHAR(60),
 existencia_teorica NUMERIC(19,4) NOT NULL, existencia_fisica NUMERIC(19,4), diferencia NUMERIC(19,4), observaciones VARCHAR(500),
 CONSTRAINT uk_conteo_fisico_linea_producto UNIQUE(conteo_id, almacen_finca_producto_id)
);
COMMENT ON TABLE conteo_fisico_almacen IS 'Expediente de conteo físico por almacén (SC-2-15); el ajuste se emite solo desde un cierre.';
COMMENT ON TABLE conteo_fisico_linea IS 'Instantánea teórica y conteo físico; diferencias respaldan únicamente el documento SC-2-16.';
