# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Descripción del Proyecto

**Sistema Finca** es un sistema de gestión agrícola que administra:

- **Fincas** con áreas, trabajadores responsables e inventarios de productos
- **Trabajadores** organizados por grupos y cargos, asignados a fincas
- **Productos** con precios diferenciados (general, trabajador, comedor) y control de stock
- **Reportes** de trabajo agrícola por bloque, campo y área
- **Salidas** seguimiento de distribuciones de productos a trabajadores o comedor
- **Deudas** control de deudas y pagos de trabajadores
- **Producción terminada** registro de producción agrícola completada

El sistema usa arquitectura CQRS con rutas separadas de lectura/escritura.

## Inicio Rápido

```bash
# Iniciar todo (backend + frontend)
./start-all.sh

# O iniciar individualmente:
# Backend (puerto 9908)
cd contabilidad && ./start-dev.sh

# Frontend (puerto 8080)
cd mi-proyecto-vue/mi-proyecto && npm run serve
```

## Comandos Backend (contabilidad/)

```bash
./mvnw spring-boot:run              # Ejecutar con perfil dev
./mvnw clean package -DskipTests    # Construir JAR
./mvnw test                         # Ejecutar todos los tests
./mvnw test -Dtest=NombreClaseTest  # Ejecutar un test específico
```

**Base de datos**: PostgreSQL en `localhost:5432/store` (usuario: postgres, contraseña: postgres)

## Comandos Frontend (mi-proyecto-vue/mi-proyecto/)

```bash
npm run serve      # Servidor dev con hot-reload
npm run build      # Build de producción
npm run lint       # Lint y corrección automática
npm run type-check # Solo verificación TypeScript
```

## Arquitectura

### Patrón CQRS

Tanto backend como frontend siguen Command Query Responsibility Segregation:

**Backend** (`com.kynsoft.report`):
- `applications/command/{entidad}/` — Handlers de Create, Update, Delete
- `applications/query/{entidad}/` — Handlers de Search, GetById, GetAll
- `IMediator` enruta comandos/queries a los handlers

**Base de datos**: Datasources separados para lectura/escritura:
- Ruta de escritura: `infrastructure/repository/command/`
- Ruta de lectura: `infrastructure/repository/query/`

### Capas del Backend

| Capa | Ruta | Propósito |
|------|------|-----------|
| Controller | `controller/` | Endpoints REST |
| Application | `applications/command/`, `applications/query/` | Handlers CQRS |
| Domain | `domain/dto/`, `domain/services/` | Interfaces de lógica de negocio |
| Infrastructure | `infrastructure/entity/`, `infrastructure/services/` | Entidades JPA, implementaciones |

### Estructura Frontend

| Ruta | Propósito |
|------|-----------|
| `src/services/` | Clientes HTTP por entidad (FincaService, TrabajadorService, etc.) |
| `src/views/` | Componentes de página (LoginView, HomeView, GerencialDashboard) |
| `src/components/` | Componentes UI reutilizables |
| `src/types/` | Interfaces TypeScript |

### Entidades Principales

```
Finca
  ├── Trabajadores — agrupados por Grupo y Cargo
  ├── FincaProducto (inventario por producto)
  └── Reportes (reportes de trabajo)

Producto
  ├── price, priceTrabajador, priceComedor
  ├── tipoProducto (PRODUCCION, INSUMO)
  └── unidadMedida (KG, UNIDAD, LIBRA, etc.)

Salida (Distribución)
  ├── tipo (TRABAJADOR, COMEDOR)
  ├── items → trabajadores con cantidades
  └── crea registros DeudaTrabajador
```

### Endpoints Principales

| Entidad | Ruta Base |
|---------|-----------|
| Fincas | `/api/finca` |
| Trabajadores | `/api/trabajadores` |
| Productos | `/api/producto` |
| FincaProducto | `/api/finca-producto` |
| Salidas | `/api/salida` |
| Reportes | `/api/reporte` |
| Deudas | `/api/deuda-trabajador` |
| Auth | `/api/auth/login` |

### Patrón de Búsqueda

Todos los endpoints de búsqueda siguen el mismo contrato:

```typescript
POST /api/{entidad}/search
{
  filter: [{ property: string, operator: string, value: string }],
  query: string,
  pageSize: number,
  page: number,
  sortBy: string,
  sortType: 'ASC' | 'DESC'
}
```

## Autenticación

- **JWT Local**: `/api/auth/login` retorna token JWT
- **Keycloak** (opcional): OAuth2/JWT para realm `kynsoft`
- El frontend guarda el token y lo incluye en header `Authorization: Bearer`

## Dependencias Externas

- **PostgreSQL**: Base de datos principal
- **Redis**: Cache (opcional en desarrollo)
- **Keycloak**: Proveedor OAuth2 (opcional, fallback a JWT local)
- **AWS S3/CloudFront**: Almacenamiento de archivos (imágenes, documentos)

## Migraciones de Base de Datos

Scripts SQL en `contabilidad/src/main/resources/db/migration/`:
- V1: movimiento_stock, configuracion_numeracion
- V2: campos de recibo en pago_deuda
- V3: stock_maximo para finca_producto
- V4: tablas usuario y auditoria

## Preferencias de Usuario

- Ejecutar tareas directamente sin pedir confirmación
- Tomar decisiones autónomas basadas en contexto y mejores prácticas
- Actuar proactivamente cuando los requisitos no estén claros
