-- V6: Create table for company configuration (required for official Cuban documents SC-2-08, SC-2-12)
-- Fields based on Resolución 11/2007 and Resolución 55/2021 del MFP

CREATE TABLE IF NOT EXISTS configuracion_empresa (
    id UUID PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    codigo VARCHAR(50),
    nit VARCHAR(50) NOT NULL,
    direccion VARCHAR(500),
    municipio VARCHAR(100),
    provincia VARCHAR(100),
    cuenta_bancaria VARCHAR(50),
    banco VARCHAR(100),
    telefono VARCHAR(50),
    email VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE
);

-- Insert default configuration for "El Coloso S.A."
INSERT INTO configuracion_empresa (id, nombre, codigo, nit, direccion, municipio, provincia, cuenta_bancaria, activo)
VALUES (
    'a0000000-0000-0000-0000-000000000001',
    'El Coloso S.A.',
    '',
    '',  -- NIT must be filled by user
    'Delicias',
    'Puerto Padre',
    'Las Tunas',
    '',
    TRUE
);

COMMENT ON TABLE configuracion_empresa IS 'Company configuration for official Cuban documents (SC-2-08, SC-2-12)';
COMMENT ON COLUMN configuracion_empresa.nit IS 'Número de Identificación Tributaria - Required by Res. 55/2021';
