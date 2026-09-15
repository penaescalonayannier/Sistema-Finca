# Sincronización autónoma entre PCs

Este directorio reemplaza la transferencia de copias completas de `store`. Cada PC mantiene su PostgreSQL y puede operar sin red. Cuando vuelve a tener conexión, `sync-now.sh` transmite sus cambios pendientes a los otros dos nodos por SSH; un nodo también reenvía cambios recibidos a sus pares. No elimina ni restaura bases enteras.

La instalación observa tablas públicas con una columna `id UUID`. Las altas, actualizaciones y bajas se registran en una cola local. El receptor aplica cada cambio de forma idempotente. Si dos nodos editan la misma fila sin haberse visto, se conserva la versión más reciente por marca de tiempo; la versión descartada queda en `finca_sync.conflict` para revisión. No existe una fusión automática por campo.

## Preparación única por PC

1. Haga un respaldo verificable de cada base y concilie primero las diferencias históricas. Los datos existentes no se publican automáticamente: así la instalación no decide qué copia previa gana.
2. Genere un UUID por PC (`uuidgen`) y cree un archivo privado a partir de `peers.env.example`, fuera del repositorio. Establezca `SYNC_DATABASE_URL` mediante `.pgpass` o certificados, nunca con contraseñas guardadas en Git.
3. Configure llaves SSH y aliases `finca-rrhh`, `finca-contabilidad` y `finca-central` en `~/.ssh/config`. Verifique las huellas de host antes de usar el sistema.
4. En cada PC ejecute:

   ```bash
   export SYNC_DATABASE_URL='postgresql://postgres@localhost:5432/store'
   ./sync/setup-node.sh UUID_DEL_NODO nombre-del-nodo
   ```

   Luego exporte en cada PC la ruta local de su configuración: `export SYNC_CONFIG="$HOME/.config/sistema-finca/peers.env"`.

## Operación

Ejecute `./sync/sync-now.sh` en cada PC de forma periódica. Hay unidades de ejemplo en `sync/systemd/`; ajuste en ellas `User`, `WorkingDirectory`, `SYNC_CONFIG` y las rutas de `ExecStart`, cópielas a `/etc/systemd/system/` y active el temporizador con `sudo systemctl enable --now sistema-finca-sync.timer`. Cada nodo debe listar los otros dos en `SYNC_PEERS`; Central no es requisito para que RRHH y Contabilidad se sincronicen entre sí. Consulte `./sync/status.sh` y revise los conflictos antes de tomar decisiones contables.

Mantenga los tres equipos sincronizados por NTP/chrony: la regla de conflicto usa la marca de tiempo del cambio y el UUID del nodo como desempate.

No use `push-db.sh` para datos operativos: una restauración total elimina cambios de la PC destino.
