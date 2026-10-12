-- La actividad agrícola es opcional y permite trazar el consumo de un insumo.
ALTER TABLE item_salida ADD COLUMN labor_id UUID;

ALTER TABLE item_salida
    ADD CONSTRAINT fk_item_salida_labor
    FOREIGN KEY (labor_id) REFERENCES labor(id);

CREATE INDEX idx_item_salida_labor ON item_salida(labor_id);
