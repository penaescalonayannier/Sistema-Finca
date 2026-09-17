-- Conserva identificadores numéricos históricos y agrega el consecutivo visible
-- de cada forma para las nuevas emisiones de Caja y Banco.
ALTER TABLE documento_caja ADD COLUMN IF NOT EXISTS numero_documento VARCHAR(80);
ALTER TABLE arqueo_caja ADD COLUMN IF NOT EXISTS numero_documento VARCHAR(80);
ALTER TABLE acta_responsabilidad_caja ADD COLUMN IF NOT EXISTS numero_documento VARCHAR(80);
ALTER TABLE cheque_transferencia ADD COLUMN IF NOT EXISTS numero_documento VARCHAR(80);
ALTER TABLE conciliacion_bancaria ADD COLUMN IF NOT EXISTS numero_documento VARCHAR(80);

CREATE UNIQUE INDEX IF NOT EXISTS uq_documento_caja_finca_numero_forma ON documento_caja (finca_id, numero_documento) WHERE numero_documento IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_arqueo_caja_finca_numero_forma ON arqueo_caja (finca_id, numero_documento) WHERE numero_documento IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_acta_responsabilidad_caja_finca_numero_forma ON acta_responsabilidad_caja (finca_id, numero_documento) WHERE numero_documento IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_cheque_transferencia_finca_numero_forma ON cheque_transferencia (finca_id, numero_documento) WHERE numero_documento IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_conciliacion_bancaria_finca_numero_forma ON conciliacion_bancaria (finca_id, numero_documento) WHERE numero_documento IS NOT NULL;
