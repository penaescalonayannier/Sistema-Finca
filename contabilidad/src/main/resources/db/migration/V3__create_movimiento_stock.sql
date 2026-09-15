-- Tabla de auditoría de movimientos de stock
CREATE TABLE IF NOT EXISTS movimiento_stock (
    id UUID PRIMARY KEY,
    finca_producto_id UUID NOT NULL,
    finca_id UUID NOT NULL,
    producto_id UUID NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    cantidad INTEGER NOT NULL,
    stock_anterior INTEGER NOT NULL,
    stock_nuevo INTEGER NOT NULL,
    referencia_id UUID,
    referencia_tabla VARCHAR(100),
    descripcion TEXT,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_movimiento_finca_producto
        FOREIGN KEY (finca_producto_id) REFERENCES finca_producto(id),
    CONSTRAINT fk_movimiento_finca
        FOREIGN KEY (finca_id) REFERENCES fincas(id),
    CONSTRAINT fk_movimiento_producto
        FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- Índices para consultas frecuentes
CREATE INDEX IF NOT EXISTS idx_movimiento_finca_producto ON movimiento_stock(finca_producto_id);
CREATE INDEX IF NOT EXISTS idx_movimiento_finca ON movimiento_stock(finca_id);
CREATE INDEX IF NOT EXISTS idx_movimiento_producto ON movimiento_stock(producto_id);
CREATE INDEX IF NOT EXISTS idx_movimiento_fecha ON movimiento_stock(fecha);
CREATE INDEX IF NOT EXISTS idx_movimiento_tipo ON movimiento_stock(tipo);
CREATE INDEX IF NOT EXISTS idx_movimiento_referencia ON movimiento_stock(referencia_id, referencia_tabla);
