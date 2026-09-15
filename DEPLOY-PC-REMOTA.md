# Despliegue en PCs Remotas

## Resumen de PCs del Sistema

| PC | Alias | IP | Usuario SSH | Rol |
|----|-------|----|-------------|-----|
| **Tu PC** | central | Esta PC | — | Nodo autónomo |
| **PC RRHH** | rrhh | 192.168.1.245 | pipe | Recursos Humanos |
| **PC Contabilidad** | contabilidad | 192.168.1.143 | yuli | Contabilidad |

---

## PC RRHH (192.168.1.245)

### Datos de Conexión

| Campo | Valor |
|-------|-------|
| **IP** | 192.168.1.245 |
| **Usuario SSH** | pipe |
| **Autenticación SSH** | Llave SSH administrada fuera del repositorio |

### URLs del Sistema

| Servicio | URL |
|----------|-----|
| **Frontend** | http://192.168.1.245:8080 |
| **Backend API** | http://192.168.1.245:9908 |
| **Health Check** | http://192.168.1.245:9908/actuator/health |

### Usuarios del Sistema

| Usuario | Rol |
|---------|-----|
| Consultar el gestor de credenciales | ADMIN |

### Estructura de Archivos

```
/home/pipe/sistema-finca/
├── backend/
│   ├── contabilidad-1.0.0.jar      # JAR ejecutable del backend
│   ├── application.properties       # Configuración base
│   └── application-dev.properties   # Configuración dev (activa)
├── frontend/
│   ├── src/                         # Código fuente Vue 3
│   ├── package.json
│   └── node_modules/                # Dependencias npm
├── backups/                         # Backups automáticos (cron 23:00)
├── start-all.sh                     # Iniciar todo el sistema
├── stop-all.sh                      # Detener todo el sistema
├── backup-db.sh                     # Crear backup manual
├── backend.log                      # Logs del backend
└── frontend.log                     # Logs del frontend
```

---

## PC Contabilidad (192.168.1.143)

### Datos de Conexión

| Campo | Valor |
|-------|-------|
| **IP** | 192.168.1.143 |
| **Usuario SSH** | yuli |
| **Autenticación SSH** | Llave SSH administrada fuera del repositorio |

### URLs del Sistema

| Servicio | URL |
|----------|-----|
| **Frontend** | http://192.168.1.143:8080 |
| **Backend API** | http://192.168.1.143:9908 |
| **Health Check** | http://192.168.1.143:9908/actuator/health |

### Usuarios del Sistema

| Usuario | Rol |
|---------|-----|
| Consultar el gestor de credenciales | ADMIN |

### Estructura de Archivos

```
/home/yuli/sistema-finca/
├── backend/
│   ├── contabilidad-1.0.0.jar      # JAR ejecutable del backend
│   ├── application.properties       # Configuración base
│   └── application-dev.properties   # Configuración dev (activa)
├── frontend/
│   ├── src/                         # Código fuente Vue 3
│   ├── package.json
│   └── node_modules/                # Dependencias npm
├── backups/                         # Backups automáticos (cron 23:00)
├── start-all.sh                     # Iniciar todo el sistema
├── stop-all.sh                      # Detener todo el sistema
├── backup-db.sh                     # Crear backup manual
├── backend.log                      # Logs del backend
└── frontend.log                     # Logs del frontend
```

### Software Instalado

| Software | Versión |
|----------|---------|
| Ubuntu | 22.04 |
| PostgreSQL | 14 |
| Redis | 6.0.16 |
| Java (Temurin) | 21 |
| Node.js | 20.x |
| npm | 10.x |

### Despliegue Realizado (2026-09-11)

1. **Instalación de dependencias**:
   - PostgreSQL 14
   - Redis 6.0.16
   - Java 21 (Temurin/Adoptium)
   - Node.js 20
   - curl (para scripts)

2. **Copia de archivos desde PC RRHH**:
   - JAR del backend copiado desde 192.168.1.245
   - Archivos de configuración (application*.properties)
   - Frontend completo vía rsync
   - Base de datos restaurada desde backup de RRHH

3. **Configuración de base de datos**:
   - BD `store` creada y restaurada
   - 125 trabajadores, 3 fincas, 50+ productos
   - Tablas de datos transaccionales limpiadas para inicio fresco

4. **Usuarios creados**:
   - Usuario SSH de operación creado; la llave se administra fuera del repositorio.
   - Usuario del sistema creado con rol ADMIN; las credenciales se gestionan fuera del repositorio.

5. **Cron configurado**:
   - Backup automático diario a las 23:00
   - Retención de últimos 14 backups

6. **Tablas limpiadas para inicio fresco**:
   - finca_producto (Stock Fincas)
   - campo, tipo_cultivo, tipo_animal, activo_animal
   - deuda_trabajador, pago_deuda
   - estadocuenta
   - prestamo, tomaprestamo
   - cliente
   - produccion_terminada
   - salida, item_salida
   - movimiento_stock

---

## Comandos de Gestión (Ambas PCs)

### Conectarse por SSH

```bash
# PC RRHH
ssh pipe@192.168.1.245

# PC Contabilidad
ssh yuli@192.168.1.143
```

### Iniciar el Sistema

```bash
cd ~/sistema-finca
./start-all.sh
```

### Detener el Sistema

```bash
cd ~/sistema-finca
./stop-all.sh
```

### Iniciar Servicios Individualmente

**Backend:**
```bash
cd ~/sistema-finca/backend
nohup java -Xms512m -Xmx1024m -jar contabilidad-1.0.0.jar --spring.profiles.active=dev --server.port=9908 > ~/sistema-finca/backend.log 2>&1 &
```

**Frontend:**
```bash
cd ~/sistema-finca/frontend
nohup npm run serve -- --port 8080 > ~/sistema-finca/frontend.log 2>&1 &
```

### Ver Logs

```bash
# Logs del backend
tail -f ~/sistema-finca/backend.log

# Logs del frontend
tail -f ~/sistema-finca/frontend.log
```

### Verificar Procesos

```bash
# Ver si el backend está corriendo
ps aux | grep contabilidad

# Ver si el frontend está corriendo
ps aux | grep vue-cli-service
```

### Reiniciar Servicios del Sistema

```bash
# PostgreSQL
sudo systemctl restart postgresql

# Redis
sudo systemctl restart redis-server

# Verificar estado
sudo systemctl status postgresql redis-server
```

---

## Base de Datos

### Conexión

```bash
# Con sudo (PC RRHH)
sudo -u postgres psql -d store

# Con .pgpass o certificado (ambas PCs)
psql -h localhost -U postgres -d store
```

### Credenciales

- **Host:** localhost
- **Puerto:** 5432
- **Base de datos:** store
- **Usuario:** postgres
- **Autenticación:** `.pgpass` o certificado local; no almacenar contraseñas en scripts ni documentación.

### Backup de la BD

```bash
# PC RRHH
sudo -u postgres pg_dump -Fc store > ~/backup_$(date +%Y%m%d).backup

# PC Contabilidad
pg_dump -h localhost -U postgres -Fc store > ~/backup_$(date +%Y%m%d).backup
```

### Restaurar BD

```bash
pg_restore -h localhost -U postgres -d store ~/backup.backup
```

---

## Arquitectura de sincronización autónoma

Cada PC mantiene su propia base `store` y puede funcionar sin red. El directorio [`sync/`](sync/README.md) implementa replicación eventual entre pares por SSH: toda alta, edición o baja de una fila con `id UUID` se registra localmente y se reintenta contra los otros dos nodos cuando están disponibles. Central no es fuente obligatoria de datos.

```
      cambios pendientes por SSH (bidireccional)
RRHH  <──────────────────────────────────────>  Contabilidad
  ↕                                                   ↕
  └───────────────── Central ────────────────────────┘
```

No se transfieren ni restauran bases completas. Un conflicto sobre la misma fila se conserva en `finca_sync.conflict` para revisión. La instalación y el uso están documentados en [`sync/README.md`](sync/README.md). Los scripts antiguos `push-db.sh` y `sync-db.sh` quedan deshabilitados para evitar pérdida de información.

---

## Procedimiento de Despliegue (Referencia)

### 1. Preparación en PC Local

```bash
# Exportar base de datos
pg_dump -h localhost -U postgres -Fc store > db-backup/store.backup

# Construir JAR del backend (requiere dependencia privada de GitHub)
cd contabilidad
./mvnw clean package -DskipTests
```

### 2. Instalación de Dependencias en PC Remota

```bash
# PostgreSQL
sudo apt install -y postgresql postgresql-contrib

# Redis
sudo apt install -y redis-server

# Java 21
sudo apt install -y wget apt-transport-https curl
wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | sudo apt-key add -
echo "deb https://packages.adoptium.net/artifactory/deb $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/adoptium.list
sudo apt update
sudo apt install -y temurin-21-jdk

# Node.js 20
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs
```

### 3. Copiar Archivos

```bash
# Crear estructura
ssh usuario@IP "mkdir -p ~/sistema-finca/backend ~/sistema-finca/frontend ~/sistema-finca/backups"

# Copiar backend
scp contabilidad/target/contabilidad-1.0.0.jar usuario@IP:~/sistema-finca/backend/
scp contabilidad/src/main/resources/application*.properties usuario@IP:~/sistema-finca/backend/

# Copiar frontend
rsync -avz --exclude 'node_modules' --exclude '.git' mi-proyecto-vue/mi-proyecto/ usuario@IP:~/sistema-finca/frontend/
```

### 4. Restaurar Base de Datos

```bash
# Crear BD
sudo -u postgres createdb store

# Restaurar
pg_restore -h localhost -U postgres -d store /ruta/al/backup.backup
```

### 5. Instalar Dependencias Frontend

```bash
cd ~/sistema-finca/frontend
npm install --legacy-peer-deps
```

### 6. Crear Usuario del Sistema

Use el endpoint o la interfaz administrativa del sistema para crear usuarios. No inserte contraseñas ni hashes directamente con SQL.

### 7. Configurar Backup Automático

```bash
# Crear script backup-db.sh
cat > ~/sistema-finca/backup-db.sh << 'EOF'
#!/bin/bash
BACKUP_DIR=~/sistema-finca/backups
BACKUP_FILE="$BACKUP_DIR/backup_$(date +%Y%m%d_%H%M%S).backup"
mkdir -p "$BACKUP_DIR"
pg_dump -h localhost -U postgres -Fc store > "$BACKUP_FILE"
cd "$BACKUP_DIR"
ls -t backup_*.backup | tail -n +15 | xargs -r rm
echo "Backup creado: $BACKUP_FILE"
EOF
chmod +x ~/sistema-finca/backup-db.sh

# Agregar cron
(crontab -l 2>/dev/null; echo "0 23 * * * ~/sistema-finca/backup-db.sh >> ~/sistema-finca/backup.log 2>&1") | crontab -
```

---

## Troubleshooting

### El backend no inicia

```bash
# Verificar logs
tail -50 ~/sistema-finca/backend.log

# Verificar que PostgreSQL esté corriendo
sudo systemctl status postgresql

# Verificar que Redis esté corriendo
sudo systemctl status redis-server
```

### El frontend no compila

```bash
# Limpiar y reinstalar dependencias
cd ~/sistema-finca/frontend
rm -rf node_modules
npm install --legacy-peer-deps
```

### Error de conexión a la BD

```bash
# Verificar que la BD existe
psql -h localhost -U postgres -l | grep store

# Verificar conexión
psql -h localhost -U postgres -d store -c "SELECT 1;"
```

### Puerto ya en uso

```bash
# Ver qué proceso usa el puerto
sudo lsof -i :9908  # Backend
sudo lsof -i :8080  # Frontend

# Matar proceso
kill -9 <PID>

# O matar todos los procesos Java/Node del sistema
pkill -f 'contabilidad.*jar'
pkill -f 'vue-cli-service'
```

---

## Protección contra Pérdida de Datos

### Resumen de Protecciones

| Protección | Ubicación | Frecuencia | Retención |
|------------|-----------|------------|-----------|
| **Backup automático RRHH** | `~/sistema-finca/backups/` (192.168.1.245) | Diario 23:00 | 14 días |
| **Backup automático Conta** | `~/sistema-finca/backups/` (192.168.1.143) | Diario 23:00 | 14 días |
| **Sincronización incremental** | Cola local `finca_sync.change_log` | Cada 5 min | Hasta confirmar entrega |

### Flujo de Recuperación

```
┌─────────────────────────────────────────────────────────┐
│                 SI HAY PÉRDIDA DE DATOS                 │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  1. Verificar backups en PC Remota:                     │
│     ls ~/sistema-finca/backups/                         │
│                                                         │
│  2. Si hay backups → restaurar:                         │
│     pg_restore -h localhost \                           │
│       -U postgres -d store ~/sistema-finca/backups/X    │
│                                                         │
│  3. Si NO hay backups → detener sincronización y         │
│     recuperar una copia verificada antes de reanudar.    │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## Notas Importantes

1. **Dependencia Privada**: El backend usa `com.kynsoft:share` que es una dependencia privada de GitHub. Por eso el JAR debe construirse en una máquina que tenga acceso a esa dependencia.

2. **Perfil Activo**: El backend usa el perfil `dev` que se conecta a `localhost:5432/store`.

3. **CORS**: El backend tiene CORS habilitado para todas las origins (`*`).

4. **Servicios al Reiniciar**: PostgreSQL y Redis inician automáticamente. Backend y frontend deben iniciarse manualmente con `./start-all.sh`.

5. **Arquitectura de Sincronización**: las tres PCs son nodos autónomos. Consulte [`sync/README.md`](sync/README.md); no use restauraciones completas para intercambiar cambios.

---

*Documentación actualizada el 2026-09-11 — Sistema desplegado en 2 PCs (RRHH + Contabilidad)*
