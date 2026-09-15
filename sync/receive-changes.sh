#!/usr/bin/env bash
# Recibe cambios JSONL por stdin. Lo invoca sync-now.sh a través de SSH.
set -euo pipefail

: "${SYNC_CONFIG:?Defina SYNC_CONFIG con la ruta absoluta de peers.env.}"
# shellcheck source=/dev/null
source "$SYNC_CONFIG"
: "${SYNC_DATABASE_URL:?Falta SYNC_DATABASE_URL en la configuración local.}"

failed=0
while IFS=$'\t' read -r change_id payload; do
    [[ -z "$change_id" ]] && continue
    if result=$(psql -X -qAt -v ON_ERROR_STOP=1 -v payload="$payload" "$SYNC_DATABASE_URL" \
        -c "SELECT finca_sync.apply_change(:'payload'::jsonb);" 2>&1); then
        [[ "$result" == "t" ]] && printf '%s\n' "$change_id"
    else
        printf 'No se aplicó cambio %s: %s\n' "$change_id" "$result" >&2
        failed=1
    fi
done

exit "$failed"
