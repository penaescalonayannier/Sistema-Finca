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
esquema_existe() {
    local relation="$1"
    PGPASSWORD="$DB_PASSWORD" psql -At "$DB_URL" -U "$DB_USER" \
        -c "SELECT to_regclass('public.${relation}') IS NOT NULL;"
}

PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V16__item_salida_producto_multiple.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V17__inventario_con_decimales.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V18__cantidades_decimales_salida.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V19__produccion_terminada_almacen_y_decimales.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V20__liquidacion_salida_y_caja.sql"
if [[ "$(esquema_existe saldo_caja_denominacion)" != "t" ]]; then
    PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
        -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V21__control_caja_denominaciones.sql"
else
    echo "V21 ya está instalada; se omite su recreación para conservar los índices existentes."
fi
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V22__arqueos_sorpresivos_caja.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V23__caja_oficial_control.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V24__control_banco_y_documentos_caja.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V25__documento_caja_movimiento_efectivo.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V26__documento_produccion_snapshot.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V27__salida_finca_consecutivo_unico.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V28__auditoria_movimientos_caja.sql"
if [[ "$(esquema_existe transferencia_almacen)" != "t" ]]; then
    PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
        -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V29__transferencias_almacen_sc209.sql"
else
    echo "V29 ya está instalada; se conserva el expediente de transferencias existente."
fi
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V30__informe_recepcion_sc204.sql"
if [[ "$(esquema_existe conteo_fisico_almacen)" != "t" ]]; then
    PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
        -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V31__conteo_fisico_almacen_sc215_sc216.sql"
else
    echo "V31 ya está instalada; se conservan los expedientes de inventario físico existentes."
fi
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V32__reporte_finca_tenant.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V33__estructura_organizativa_y_plazas.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V34__expediente_disciplina_laboral.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V35__ciclo_formal_evaluacion_desempeno.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V36__historial_salarial_trabajador.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V37__consecutivo_asiento_contable_seguro.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V38__registro_formas_numeradas.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V39__semillas_formas_numeradas.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V40__numeros_documentales_liquidacion_y_entrega_banco.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V41__inicializar_series_formas_desde_consecutivos.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V42__formas_numeradas_caja_banco_control.sql"
PGPASSWORD="$DB_PASSWORD" psql -v ON_ERROR_STOP=1 "$DB_URL" -U "$DB_USER" \
    -f "$SOURCE_DIR/contabilidad/src/main/resources/db/migration/V43__unicidad_formas_por_finca.sql"
