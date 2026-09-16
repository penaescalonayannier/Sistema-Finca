#!/usr/bin/env bash

# Actualiza una instalación de Sistema Finca desde los repositorios oficiales.
# No modifica PostgreSQL, Redis ni los archivos de configuración locales.
set -Eeuo pipefail

BASE_DIR="${SISTEMA_FINCA_HOME:-$HOME/sistema-finca}"
BRANCH="${SISTEMA_FINCA_BRANCH:-main}"
MAIN_REPO_URL="${SISTEMA_FINCA_REPO_URL:-https://github.com/penaescalonayannier/Sistema-Finca.git}"
FRONTEND_REPO_URL="${SISTEMA_FINCA_FRONTEND_REPO_URL:-https://github.com/penaescalonayannier/finca-frontend.git}"
SOURCE_DIR="$BASE_DIR/source/Sistema-Finca"
FRONTEND_DIR="$BASE_DIR/frontend"
BACKEND_DIR="$BASE_DIR/backend"
CONFIG_DIR="$BASE_DIR/config"
LOCK_FILE="$BASE_DIR/.update-from-github.lock"

log() {
    printf '[%s] %s\n' "$(date '+%F %T')" "$*"
}

fail() {
    log "ERROR: $*"
    exit 1
}

require_command() {
    command -v "$1" >/dev/null 2>&1 || fail "Falta el comando requerido: $1"
}

update_repository() {
    local directory="$1"
    local url="$2"

    if [[ -d "$directory/.git" ]]; then
        log "Actualizando $(basename "$directory") desde $BRANCH"
        git -C "$directory" fetch --prune origin "$BRANCH"
        git -C "$directory" checkout "$BRANCH"
        git -C "$directory" reset --hard "origin/$BRANCH"
        git -C "$directory" clean -fd
    else
        log "Clonando $(basename "$directory") desde GitHub"
        mkdir -p "$(dirname "$directory")"
        git clone --branch "$BRANCH" --single-branch "$url" "$directory"
    fi
}

backup_frontend_environment() {
    local timestamp
    timestamp="$(date '+%Y%m%d-%H%M%S')"
    mkdir -p "$CONFIG_DIR/frontend"

    for environment_file in .env .env.development .env.production; do
        if [[ -f "$FRONTEND_DIR/$environment_file" && ! -f "$CONFIG_DIR/frontend/$environment_file" ]]; then
            cp -p "$FRONTEND_DIR/$environment_file" "$CONFIG_DIR/frontend/$environment_file"
        fi
    done

    if [[ -d "$FRONTEND_DIR" && ! -d "$FRONTEND_DIR/.git" ]]; then
        log "Resguardando el frontend anterior"
        mv "$FRONTEND_DIR" "$BASE_DIR/frontend.legacy-$timestamp"
    fi
}

restore_frontend_environment() {
    for environment_file in .env .env.development .env.production; do
        if [[ -f "$CONFIG_DIR/frontend/$environment_file" ]]; then
            cp -p "$CONFIG_DIR/frontend/$environment_file" "$FRONTEND_DIR/$environment_file"
        fi
    done
}

install_backend() {
    local source_jar="$SOURCE_DIR/contabilidad/target/contabilidad-1.0.0.jar"
    local temporary_jar="$BACKEND_DIR/contabilidad-1.0.0.jar.new"

    [[ -f "$source_jar" ]] || fail "No se generó el JAR del backend"
    mkdir -p "$BACKEND_DIR"
    cp "$source_jar" "$temporary_jar"
    mv -f "$temporary_jar" "$BACKEND_DIR/contabilidad-1.0.0.jar"
}

main() {
    require_command git
    require_command java
    require_command node
    require_command npm
    require_command psql
    require_command flock

    mkdir -p "$BASE_DIR" "$CONFIG_DIR"
    exec 9>"$LOCK_FILE"
    flock -n 9 || fail "Ya existe una actualización en ejecución"

    # Los properties del backend siempre viven fuera del repositorio fuente.
    mkdir -p "$CONFIG_DIR/backend"
    for properties_file in application.properties application-dev.properties; do
        if [[ -f "$BACKEND_DIR/$properties_file" && ! -f "$CONFIG_DIR/backend/$properties_file" ]]; then
            cp -p "$BACKEND_DIR/$properties_file" "$CONFIG_DIR/backend/$properties_file"
        fi
        if [[ -f "$CONFIG_DIR/backend/$properties_file" ]]; then
            mkdir -p "$BACKEND_DIR"
            cp -p "$CONFIG_DIR/backend/$properties_file" "$BACKEND_DIR/$properties_file"
        fi
    done

    backup_frontend_environment
    update_repository "$SOURCE_DIR" "$MAIN_REPO_URL"

    # La siguiente ejecución usará la versión del actualizador descargada de GitHub.
    install -m 755 "$SOURCE_DIR/deploy/update-from-github.sh" "$BASE_DIR/update-from-github.sh"
    update_repository "$FRONTEND_DIR" "$FRONTEND_REPO_URL"
    restore_frontend_environment

    log "Instalando la librería compartida"
    bash "$SOURCE_DIR/contabilidad/mvnw" -f "$SOURCE_DIR/share/pom.xml" -DskipTests install

    log "Compilando backend"
    bash "$SOURCE_DIR/contabilidad/mvnw" -f "$SOURCE_DIR/contabilidad/pom.xml" -DskipTests clean package

    log "Instalando dependencias del frontend"
    npm --prefix "$FRONTEND_DIR" ci --legacy-peer-deps

    install_backend

    log "Aplicando migraciones de base de datos"
    bash "$SOURCE_DIR/deploy/apply-db-schema.sh" "$SOURCE_DIR" "$BACKEND_DIR/application-dev.properties"

    log "Reiniciando servicios"
    if [[ -x "$BASE_DIR/stop-all.sh" ]]; then
        "$BASE_DIR/stop-all.sh"
    fi
    [[ -x "$BASE_DIR/start-all.sh" ]] || fail "No existe $BASE_DIR/start-all.sh"
    "$BASE_DIR/start-all.sh"

    if command -v curl >/dev/null 2>&1; then
        log "Comprobando backend"
        curl --fail --silent --show-error --retry 12 --retry-delay 5 \
            http://127.0.0.1:9908/actuator/health >/dev/null
    fi

    log "Actualización completada: backend $(git -C "$SOURCE_DIR" rev-parse --short HEAD), frontend $(git -C "$FRONTEND_DIR" rev-parse --short HEAD)"
}

main "$@"
