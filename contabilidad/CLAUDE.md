# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Descripción

Backend Spring Boot 3.3 para Sistema Finca — sistema de gestión agrícola que maneja fincas, trabajadores, productos, inventarios, distribuciones y reportes.

## Comandos

```bash
./start-dev.sh                      # Inicio optimizado para dev (recomendado)
./mvnw spring-boot:run              # Inicio estándar
./mvnw clean package -DskipTests    # Construir JAR
./mvnw test                         # Ejecutar todos los tests
./mvnw test -Dtest=NombreClaseTest  # Ejecutar un test específico
```

Puerto: 9908 (dev) | 9909 (Docker)

## Base de Datos

PostgreSQL en `localhost:5432/store` (postgres/postgres). Patrón CQRS con datasources separados:
- `PostgresDBWriteConfiguration` → `infrastructure/repository/command/`
- `PostgresDBReadConfiguration` → `infrastructure/repository/query/`

## Arquitectura

### Estructura de Paquetes

```
com.kynsoft.report/
├── controller/                    # Endpoints REST
├── applications/
│   ├── command/{entidad}/        # Handlers Create, Update, Delete
│   └── query/{entidad}/          # Handlers Search, GetById, GetAll
├── domain/
│   ├── dto/                      # Objetos de transferencia + enums
│   └── services/                 # Interfaces de servicios
└── infrastructure/
    ├── entity/                   # Entidades JPA
    ├── repository/command/       # Repositorios de escritura
    ├── repository/query/         # Repositorios de lectura
    ├── services/                 # Implementaciones de servicios
    └── config/                   # Configuración Security, CORS, Jackson
```

### Flujo CQRS

1. Controller recibe la petición
2. `IMediator.send()` despacha al handler
3. Command handler → repository de escritura | Query handler → repository de lectura
4. Capa de servicio maneja lógica de negocio
5. Se retorna DTO de respuesta

### Entidades Principales

| Entidad | Propósito |
|---------|-----------|
| Finca | Finca con área, trabajador responsable, estado |
| Trabajador | Trabajador con CI, cuenta, grupo, cargo, asignación a finca |
| Producto | Producto con precios diferenciados y unidad de medida |
| FincaProducto | Inventario por finca (stock, niveles mín/máx) |
| Salida | Distribución de productos a trabajadores o comedor |
| DeudaTrabajador | Seguimiento de deudas de trabajadores |
| ProduccionTerminada | Producción agrícola completada |
| Reporte | Reporte de trabajo por bloque/campo/área |
| MovimientoStock | Auditoría de movimientos de stock |

### Enums

- `TipoProducto`: PRODUCCION, INSUMO
- `TipoSalida`: TRABAJADOR, COMEDOR
- `DestinoSalida`: TRABAJADOR, COMEDOR
- `UnidadMedida`: KG, UNIDAD, LIBRA, QUINTAL, ARROBA, SACO
- `TipoMovimientoStock`: ENTRADA_PRODUCCION, ENTRADA_FACTURA, ENTRADA_CONDUCE, SALIDA, AJUSTE

### Autenticación

Autenticación dual:
1. **JWT Local**: Endpoint `/api/auth/login`, secreto en propiedad `jwt.secret`
2. **Keycloak**: OAuth2/JWT para realm `kynsoft` (opcional)

Seguridad configurada en `SecurityConfig.java` con filtro JWT.
