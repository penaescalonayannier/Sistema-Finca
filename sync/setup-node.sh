#!/usr/bin/env bash
# Instala el registro de cambios local. Debe ejecutarse una vez por PC.
set -euo pipefail

if [[ $# -ne 2 ]]; then
    echo "Uso: $0 <uuid-del-nodo> <nombre-del-nodo>" >&2
    exit 64
fi

node_id=$1
node_name=$2
script_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
: "${SYNC_DATABASE_URL:?Defina SYNC_DATABASE_URL (use ~/.pgpass o certificados).}"

psql -X -v ON_ERROR_STOP=1 "$SYNC_DATABASE_URL" -f "$script_dir/install.sql"
psql -X -v ON_ERROR_STOP=1 -v node_id="$node_id" -v node_name="$node_name" \
    "$SYNC_DATABASE_URL" \
    -c "SELECT finca_sync.configure_node(:'node_id'::uuid, :'node_name');"

echo "Nodo '$node_name' instalado. Tablas monitorizadas:"
psql -X -At "$SYNC_DATABASE_URL" -c \
    "SELECT table_name FROM finca_sync.register_triggers() ORDER BY table_name;"
