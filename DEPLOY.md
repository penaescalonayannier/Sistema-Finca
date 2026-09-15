# Despliegue con Docker

## Requisitos en la PC destino

Solo necesitas Docker instalado:

```bash
# Ubuntu 22.04/24.04
sudo apt update
sudo apt install docker.io docker-compose-v2
sudo usermod -aG docker $USER
# Cerrar sesión y volver a entrar para que tome efecto
```

## Pre-requisito: Construir el JAR del backend

El backend requiere una dependencia privada de GitHub. Antes de desplegar, construye el JAR en una máquina que tenga las dependencias:

```bash
cd contabilidad
./mvnw clean package -DskipTests
```

Esto genera `contabilidad/target/contabilidad-1.0.0.jar` que será usado por Docker.

## Desplegar

```bash
cd "Sistema Finca"
docker compose up -d --build
```

Espera ~2-3 minutos para que compile el frontend y levante todo.

## URLs

| Servicio | URL |
|----------|-----|
| Frontend | http://localhost:8080 |
| Backend API | http://localhost:9908 |
| Swagger | http://localhost:9908/swagger-ui.html |
| Health Check | http://localhost:9908/actuator/health |

## Comandos útiles

```bash
# Ver estado de contenedores
docker compose ps

# Ver logs
docker compose logs -f

# Ver logs de un servicio específico
docker compose logs -f backend

# Detener todo
docker compose down

# Detener y eliminar datos (base de datos)
docker compose down -v

# Reconstruir después de cambios en código
cd contabilidad && ./mvnw clean package -DskipTests && cd ..
docker compose up -d --build
```

## Puertos utilizados

| Servicio   | Puerto Host | Puerto Contenedor |
|------------|-------------|-------------------|
| Frontend   | 8080        | 80                |
| Backend    | 9908        | 9908              |
| PostgreSQL | 5433        | 5432              |
| Redis      | 6380        | 6379              |

**Nota:** Los puertos de PostgreSQL y Redis usan puertos alternativos (5433 y 6380) para evitar conflictos si ya tienes estos servicios corriendo localmente.

## Troubleshooting

**El backend no inicia:**
```bash
docker compose logs backend
```

**Puerto ya en uso:**
Edita `docker-compose.yml` y cambia el puerto en conflicto.

**Limpiar todo y empezar de cero:**
```bash
docker compose down -v
docker system prune -af
docker compose up -d --build
```

## Arquitectura Docker

```
┌─────────────────────────────────────────────────────────┐
│                    Docker Network                        │
│                                                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │ Frontend │  │ Backend  │  │ Postgres │  │  Redis   │ │
│  │  :8080   │──│  :9908   │──│  :5432   │  │  :6379   │ │
│  │  nginx   │  │ Spring   │  │          │  │          │ │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘ │
│       │              │              │             │      │
└───────┼──────────────┼──────────────┼─────────────┼──────┘
        │              │              │             │
   host:8080      host:9908      host:5433     host:6380
```
