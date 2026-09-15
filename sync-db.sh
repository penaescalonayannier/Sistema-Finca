#!/usr/bin/env bash
# Compatibilidad: el antiguo script solo descargaba respaldos y guardaba claves.
set -euo pipefail

if [[ $# -gt 0 ]]; then
    printf 'sync-db.sh no acepta destino: la sincronización se realiza con todos los pares configurados.\n' >&2
fi

cat <<'EOF'
El respaldo remoto manual fue reemplazado por sincronización incremental segura.

  export SYNC_CONFIG="$HOME/.config/sistema-finca/peers.env"
  ./sync/sync-now.sh

El proceso conserva las bases locales y reintenta los cambios pendientes cuando
un equipo vuelve a estar disponible. Consulte sync/README.md.
EOF

exit 2
