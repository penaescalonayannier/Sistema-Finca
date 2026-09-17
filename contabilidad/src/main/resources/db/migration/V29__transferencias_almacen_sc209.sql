-- Modelo SC-2-09. Las transferencias históricas continúan exclusivamente en
-- movimiento_stock; desde esta migración cada despacho tiene su documento y
-- debe ser recibido, rechazado o revertido explícitamente.
CREATE TABLE transferencia_almacen (
    id UUID PRIMARY KEY,
    finca_id UUID NOT NULL REFERENCES fincas(id),
    numero_documento VARCHAR(40) NOT NULL,
    origen_almacen_id UUID NOT NULL REFERENCES almacenes(id),
    destino_almacen_id UUID NOT NULL REFERENCES almacenes(id),
    estado VARCHAR(20) NOT NULL,
    fecha_despacho TIMESTAMP NOT NULL,
    fecha_recepcion TIMESTAMP,
    observaciones VARCHAR(1000),
    despachado_por_id UUID,
    recibido_por_id UUID,
    motivo_cierre VARCHAR(1000),
    CONSTRAINT ck_transferencia_almacen_distinta CHECK (origen_almacen_id <> destino_almacen_id),
    CONSTRAINT ck_transferencia_almacen_estado CHECK (estado IN ('EN_TRANSITO','RECIBIDA','RECHAZADA','REVERSADA'))
);
CREATE UNIQUE INDEX uk_transferencia_almacen_finca_numero ON transferencia_almacen(finca_id, numero_documento);
CREATE INDEX ix_transferencia_almacen_destino_estado ON transferencia_almacen(destino_almacen_id, estado, fecha_despacho);

CREATE TABLE transferencia_almacen_linea (
    id UUID PRIMARY KEY,
    transferencia_id UUID NOT NULL REFERENCES transferencia_almacen(id),
    finca_producto_id UUID NOT NULL REFERENCES finca_producto(id),
    origen_almacen_finca_producto_id UUID NOT NULL REFERENCES almacen_finca_producto(id),
    destino_almacen_finca_producto_id UUID REFERENCES almacen_finca_producto(id),
    cantidad_despachada NUMERIC(19,4) NOT NULL CHECK (cantidad_despachada > 0),
    cantidad_recibida NUMERIC(19,4),
    cantidad_rechazada NUMERIC(19,4),
    observaciones VARCHAR(1000),
    CONSTRAINT ck_transferencia_linea_cantidades CHECK (
      (cantidad_recibida IS NULL OR cantidad_recibida >= 0) AND
      (cantidad_rechazada IS NULL OR cantidad_rechazada >= 0)
    )
);
CREATE INDEX ix_transferencia_linea_transferencia ON transferencia_almacen_linea(transferencia_id);

COMMENT ON TABLE transferencia_almacen IS 'SC-2-09: transferencia en dos fases; no incrementa destino hasta recepción.';
