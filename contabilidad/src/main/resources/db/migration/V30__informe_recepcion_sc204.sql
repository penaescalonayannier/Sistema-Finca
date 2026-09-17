-- SC-2-04: cada recepción por factura o conduce se conserva como expediente
-- inmutable y se enlaza en forma uno-a-uno al movimiento físico que origina.
CREATE TABLE IF NOT EXISTS informe_recepcion (
    id UUID PRIMARY KEY,
    finca_id UUID NOT NULL REFERENCES fincas(id),
    almacen_id UUID NOT NULL REFERENCES almacenes(id),
    numero_documento VARCHAR(30) NOT NULL,
    tipo_fuente VARCHAR(30) NOT NULL CHECK (tipo_fuente IN ('ENTRADA_FACTURA', 'ENTRADA_CONDUCE')),
    numero_fuente VARCHAR(100) NOT NULL,
    fecha_documento DATE NOT NULL,
    proveedor VARCHAR(180) NOT NULL,
    responsable_entrega VARCHAR(180) NOT NULL,
    responsable_recibe VARCHAR(180) NOT NULL,
    observaciones VARCHAR(1000),
    estado VARCHAR(20) NOT NULL DEFAULT 'REGISTRADO' CHECK (estado = 'REGISTRADO'),
    movimiento_stock_id UUID NOT NULL UNIQUE REFERENCES movimiento_stock(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_informe_recepcion_finca_numero UNIQUE (finca_id, numero_documento)
);

CREATE TABLE IF NOT EXISTS informe_recepcion_linea (
    id UUID PRIMARY KEY,
    informe_recepcion_id UUID NOT NULL REFERENCES informe_recepcion(id) ON DELETE RESTRICT,
    almacen_finca_producto_id UUID NOT NULL REFERENCES almacen_finca_producto(id),
    finca_producto_id UUID NOT NULL REFERENCES finca_producto(id),
    producto_id UUID NOT NULL REFERENCES productos(id),
    producto_codigo VARCHAR(60) NOT NULL,
    producto_nombre VARCHAR(180) NOT NULL,
    unidad_medida VARCHAR(30),
    cantidad NUMERIC(19,4) NOT NULL CHECK (cantidad > 0),
    costo_unitario NUMERIC(19,4) NOT NULL CHECK (costo_unitario >= 0),
    importe NUMERIC(19,4) NOT NULL CHECK (importe >= 0),
    saldo_posterior NUMERIC(19,4) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_informe_recepcion_finca_fecha ON informe_recepcion(finca_id, fecha_documento);
CREATE INDEX IF NOT EXISTS idx_informe_recepcion_linea_expediente ON informe_recepcion_linea(informe_recepcion_id);
COMMENT ON TABLE informe_recepcion IS 'Modelo SC-2-04 Informe de recepción, inmutable y enlazado al movimiento físico.';
