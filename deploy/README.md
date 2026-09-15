# Actualizaciones desde GitHub

GitHub es la fuente de verdad del código. Las bases de datos locales, los archivos `application*.properties` y los `.env*` no se descargan ni se sobrescriben desde GitHub.

En cada PC se instala `update-from-github.sh` en `~/sistema-finca/`. El script:

1. Descarga `main` de `Sistema-Finca` y `finca-frontend`.
2. Compila la librería `share` y el backend con Java 21.
3. Instala las dependencias del frontend.
4. Actualiza el JAR, reinicia los servicios y valida el health check.

El código fuente se restablece exactamente a `origin/main`; por eso no se deben hacer cambios manuales dentro de `~/sistema-finca/source/Sistema-Finca` ni `~/sistema-finca/frontend`. Los archivos de configuración se guardan en `~/sistema-finca/config/`.

Para actualizar una PC ya configurada:

```bash
~/sistema-finca/update-from-github.sh
```

La primera instalación requiere Git. En Ubuntu:

```bash
sudo apt-get update
sudo apt-get install -y git
```
