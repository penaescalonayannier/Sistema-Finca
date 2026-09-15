#!/usr/bin/env bash
# Compatibilidad: se deshabilitó la copia completa porque eliminaba datos remotos.
set -euo pipefail

cat <<'EOF'
push-db.sh ya no restaura una base de datos remota.

La copia completa de store podía borrar cambios realizados sin conexión en RRHH o
Contabilidad. Configure la sincronización eventual una vez por PC y luego use:

  export SYNC_CONFIG="$HOME/.config/sistema-finca/peers.env"
  ./sync/sync-now.sh

Consulte sync/README.md para preparar SSH por llave, identificadores de nodo y
la conciliación inicial de las tres bases.
EOF

exit 2
