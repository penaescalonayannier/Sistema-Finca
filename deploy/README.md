# Actualizaciones desde GitHub

GitHub es la fuente de verdad del código. Las bases de datos locales, los archivos `application*.properties` y los `.env*` no se descargan ni se sobrescriben desde GitHub.

Antes de la primera actualización, cada PC debe tener Java 21, Node.js con npm, Git, PostgreSQL client (`psql`) y los scripts locales `~/sistema-finca/start-all.sh` y, si corresponde, `~/sistema-finca/stop-all.sh`. El actualizador comprueba estas dependencias antes de descargar o reemplazar código.

En cada PC se instala `update-from-github.sh` en `~/sistema-finca/`. El script:

1. Descarga `main` de `Sistema-Finca` y `finca-frontend`.
2. Compila la librería `share` y el backend con Java 21.
3. Instala las dependencias del frontend.
4. Aplica los cambios de esquema requeridos por la versión antes de reiniciar.
5. Actualiza el JAR, reinicia los servicios y valida el health check.

Las migraciones de inventario se ejecutan de forma idempotente. Por tanto, esta actualización deja preparada cada base local para existencias fraccionarias —por ejemplo, `1.5`— tanto en productos de finca como en entradas, salidas y transferencias de almacén. No hay pasos SQL manuales para repetir en cada PC.

El código fuente se restablece exactamente a `origin/main`; por eso no se deben hacer cambios manuales dentro de `~/sistema-finca/source/Sistema-Finca` ni `~/sistema-finca/frontend`. Los archivos de configuración se guardan en `~/sistema-finca/config/`. La migración usa las propiedades de conexión de `application-dev.properties`; pueden sobrescribirse con `SISTEMA_FINCA_DB_URL`, `SISTEMA_FINCA_DB_USER` y `SISTEMA_FINCA_DB_PASSWORD`.

Para actualizar una PC ya configurada:

```bash
~/sistema-finca/update-from-github.sh
```

La primera instalación requiere las herramientas de compilación y el cliente de PostgreSQL. En Ubuntu:

```bash
sudo apt-get update
sudo apt-get install -y git postgresql-client
```
