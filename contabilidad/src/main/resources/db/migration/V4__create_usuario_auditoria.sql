-- V4: Create usuario and auditoria tables for SPEC-005 and SPEC-006

-- Usuario table (linked to trabajador)
CREATE TABLE IF NOT EXISTS usuario (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    trabajador_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    CONSTRAINT fk_usuario_trabajador FOREIGN KEY (trabajador_id) REFERENCES trabajador(id)
);

CREATE INDEX IF NOT EXISTS idx_usuario_username ON usuario(username);
CREATE INDEX IF NOT EXISTS idx_usuario_trabajador ON usuario(trabajador_id);
CREATE INDEX IF NOT EXISTS idx_usuario_activo ON usuario(activo);

-- Auditoria table for system audit trail
CREATE TABLE IF NOT EXISTS auditoria (
    id UUID PRIMARY KEY,
    usuario_id UUID,
    username VARCHAR(50),
    accion VARCHAR(30) NOT NULL,
    entidad VARCHAR(50) NOT NULL,
    entidad_id UUID,
    descripcion VARCHAR(500),
    valor_anterior TEXT,
    valor_nuevo TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_auditoria_usuario ON auditoria(usuario_id);
CREATE INDEX IF NOT EXISTS idx_auditoria_entidad ON auditoria(entidad);
CREATE INDEX IF NOT EXISTS idx_auditoria_created_at ON auditoria(created_at);
CREATE INDEX IF NOT EXISTS idx_auditoria_accion ON auditoria(accion);
