#!/usr/bin/env bash

# Aplica los cambios de esquema que requiere el código distribuido.
# Es idempotente: puede ejecutarse en cada actualización de una PC.
set -Eeuo pipefail

SOURCE_DIR="${1:?Debe indicar el directorio fuente}"
BACKEND_CONFIG="${2:?Debe indicar application-dev.properties}"

read_property() {
    local key="$1"
    [[ -f "$BACKEND_CONFIG" ]] || return 0
    awk -F= -v key="$key" '$1 == key { sub(/^[^=]*=/, ""); print; exit }' "$BACKEND_CONFIG"
}

DB_URL="${SISTEMA_FINCA_DB_URL:-$(read_property spring.datasource.url)}"
DB_USER="${SISTEMA_FINCA_DB_USER:-$(read_property spring.datasource.username)}"
DB_PASSWORD="${SISTEMA_FINCA_DB_PASSWORD:-$(read_property spring.datasource.password)}"

[[ -n "$DB_URL" && -n "$DB_USER" ]] || {
    echo "No se pudo obtener la conexión de PostgreSQL para aplicar migraciones." >&2
    exit 1
}
command -v psql >/dev/null 2>&1 || {
    echo "Falta psql; no se puede actualizar el esquema de base de datos." >&2
    exit 1
}

# psql usa URI PostgreSQL; Spring usa el mismo valor con el prefijo jdbc:.
DB_URL="${DB_URL#jdbc:}"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V16__item_salida_producto_multiple.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V17__inventario_con_decimales.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V18__cantidades_decimales_salida.sql"
