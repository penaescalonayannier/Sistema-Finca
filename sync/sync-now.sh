#!/usr/bin/env bash
# Envía los cambios pendientes a cada par disponible; nunca reemplaza una BD.
set -euo pipefail

: "${SYNC_CONFIG:?Defina SYNC_CONFIG con la ruta absoluta de peers.env.}"
# shellcheck source=/dev/null
source "$SYNC_CONFIG"
: "${SYNC_DATABASE_URL:?Falta SYNC_DATABASE_URL en la configuración local.}"

configured_node=$(psql -X -qAt -v ON_ERROR_STOP=1 "$SYNC_DATABASE_URL" \
    -c "SELECT finca_sync.local_node_id();")
if [[ "$configured_node" != "$SYNC_NODE_ID" ]]; then
    printf 'SYNC_NODE_ID no coincide con el nodo configurado en esta base.\n' >&2
    exit 78
fi

script_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
batch_size=${SYNC_BATCH_SIZE:-1000}
work_dir=$(mktemp -d)
trap 'rm -rf "$work_dir"' EXIT

for peer in "${SYNC_PEERS[@]}"; do
    peer_id=${PEER_IDS[$peer]:-}
    peer_ssh=${PEER_SSH[$peer]:-}
    if [[ ! "$peer_id" =~ ^[0-9a-fA-F-]{36}$ || -z "$peer_ssh" ]]; then
        printf 'Configuración inválida para el par %s\n' "$peer" >&2
        continue
    fi

    payload_file="$work_dir/$peer.payload"
    ack_file="$work_dir/$peer.ack"
    psql -X -qAt -F $'\t' -v ON_ERROR_STOP=1 -v peer_id="$peer_id" -v batch_size="$batch_size" \
        "$SYNC_DATABASE_URL" \
        -c "SELECT change_id, payload FROM finca_sync.export_changes(:'peer_id'::uuid, :'batch_size'::integer);" \
        > "$payload_file"

    if [[ ! -s "$payload_file" ]]; then
        printf '%s: sin cambios pendientes\n' "$peer"
        continue
    fi

    printf '%s: enviando %s cambio(s)…\n' "$peer" "$(wc -l < "$payload_file")"
    if ! ssh -o BatchMode=yes -o StrictHostKeyChecking=yes "$peer_ssh" \
        "SYNC_CONFIG=\"$REMOTE_SYNC_CONFIG\" \"$REMOTE_SYNC_DIR/sync/receive-changes.sh\"" \
        < "$payload_file" > "$ack_file"; then
        printf '%s: no disponible o hubo cambios rechazados; se reintentará después.\n' "$peer" >&2
        continue
    fi

    while IFS= read -r change_id; do
        [[ "$change_id" =~ ^[0-9a-fA-F-]{36}$ ]] || continue
        psql -X -q -v ON_ERROR_STOP=1 -v peer_id="$peer_id" -v change_id="$change_id" \
            "$SYNC_DATABASE_URL" \
            -c "SELECT finca_sync.mark_delivered(:'peer_id'::uuid, :'change_id'::uuid);"
    done < "$ack_file"
done
