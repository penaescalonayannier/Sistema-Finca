#!/usr/bin/env bash
set -euo pipefail

: "${SYNC_CONFIG:?Defina SYNC_CONFIG con la ruta absoluta de peers.env.}"
# shellcheck source=/dev/null
source "$SYNC_CONFIG"
: "${SYNC_DATABASE_URL:?Falta SYNC_DATABASE_URL en la configuración local.}"

psql -X -v ON_ERROR_STOP=1 "$SYNC_DATABASE_URL" -c "
SELECT n.name AS nodo, count(c.change_id) AS cambios_generados
FROM finca_sync.node n LEFT JOIN finca_sync.change_log c ON c.origin_node_id = n.id
GROUP BY n.name;"
psql -X -v ON_ERROR_STOP=1 "$SYNC_DATABASE_URL" -c "
SELECT table_name, count(*) AS conflictos_pendientes
FROM finca_sync.conflict GROUP BY table_name ORDER BY conflictos_pendientes DESC, table_name;"
