# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Sistema Finca** (Farm System) is a full-stack application with two main components:

1. **Backend (contabilidad/)**: Spring Boot 3.3 microservice for report generation and accounting operations
2. **Frontend (mi-proyecto-vue/mi-proyecto/)**: Vue 3 + TypeScript web application for account management

The system generates PDF reports, manages accounting data, and provides a web interface for viewing/editing account statements. It integrates with Keycloak for authentication and uses PostgreSQL for data persistence.

## Project Structure

```
Sistema Finca/
├── contabilidad/                 # Spring Boot backend (Java 21, Maven)
│   ├── src/main/java/           # Main source code
│   │   └── com/kynsoft/report/  # Application packages
│   ├── src/main/resources/      # Configuration and templates
│   ├── pom.xml                  # Maven configuration
│   ├── CLAUDE.md                # Backend-specific guidance
│   └── Dockerfile               # Container configuration
│
└── mi-proyecto-vue/mi-proyecto/  # Vue 3 frontend (TypeScript)
    ├── src/                     # Vue source code
    │   ├── components/          # Vue components
    │   ├── views/              # Page-level components
    │   ├── services/           # HTTP services
    │   ├── types/              # TypeScript interfaces
    │   └── router/             # Route configuration
    ├── package.json            # NPM dependencies
    ├── CLAUDE.md               # Frontend-specific guidance
    └── vue.config.js           # Build configuration
```

## Architecture Overview

### CQRS Pattern
Both projects leverage Command Query Responsibility Segregation:
- **Commands**: Write operations (Create, Update, Delete)
- **Queries**: Read operations
- **Handlers**: `ICommandHandler` and `IQueryHandler` interfaces dispatch operations
- **Mediator**: `IMediator` routes commands/queries to appropriate handlers

### Data Flow
1. Frontend (Vue) sends HTTP requests to backend REST endpoints
2. Backend controller receives request and dispatches via `IMediator`
3. Command/Query handlers process business logic
4. Data persisted to PostgreSQL (separate read/write schemas for CQRS)
5. Frontend receives JSON response and updates UI

### Key Technologies
- **Backend**: Spring Boot 3.3, Spring Data JPA, Apache PDFBox, JasperReports, Keycloak
- **Frontend**: Vue 3, Composition API, TypeScript, Axios, Vue Router
- **Database**: PostgreSQL with separate read/write datasources
- **Cache**: Redis
- **Container**: Docker
- **Auth**: Keycloak OAuth2/JWT (kynsoft realm)

## Backend Commands

Navigate to `contabilidad/` directory before running:

```bash
# Build (skip tests)
./mvnw clean package -DskipTests

# Run locally (requires PostgreSQL on localhost:5432)
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run single test
./mvnw test -Dtest=TestClassName

# Docker build (requires PACKAGE_TOKEN for private Maven repository)
docker build --build-arg PACKAGE_TOKEN=<token> -t report:1.0.0 .
```

**Ports**: Dev profile (9908) | Docker (9909)

## Frontend Commands

Navigate to `mi-proyecto-vue/mi-proyecto/` directory before running:

```bash
# Install dependencies
npm install

# Development server with hot-reload (runs on http://localhost:8080)
npm run serve

# Production build
npm run build

# Lint and auto-fix
npm run lint
```

## Connecting Frontend and Backend

The frontend is configured to proxy API requests to the backend:

- **Development**: Frontend runs on `http://localhost:8080`, proxies to `http://localhost:9908/api/*`
- **Production**: Build targets backend URL (configured in environment)

Configuration in `vue.config.js` handles automatic request routing for `/api/*` paths.

## Key Endpoints

### Account Statement Management
- `POST /api/estado-cuenta` - Create account statement
- `GET /api/estado-cuenta/{id}` - Retrieve by ID
- `POST /api/estado-cuenta/search` - Search with pagination and filters
- `GET /api/estado-cuenta/export` - Export to Excel
- `POST /api/estado-cuenta/upload-xml` - Import from XML

### Report Generation
- `POST /api/report/receta-medica` - Generate medical prescription PDF

## Frontend Component Architecture

### Core Services
- `EstadoCuentaService.ts` - Central API client for account operations
- Handles CRUD, search, pagination, filtering, and file uploads

### Main Views/Components
- `EstadoCuentaList.vue` - List, search, and filter accounts
- `CrearEstadoCuenta.vue` - Create new account statement
- `EditarEstadoCuenta.vue` - Edit existing account
- `UploadXmlView.vue` - Import accounts from XML

### Type System
- `src/types/EstadoCuenta.ts` - TypeScript interfaces for:
  - `EstadoCuenta` - Main entity
  - `SearchFilter` - Filter criteria
  - `SearchRequest` - Paginated search query
  - `PagedResponse<T>` - Generic paginated response

### Search API Contract
```typescript
{
  filter: SearchFilter[],     // Array of filter conditions
  query: string,              // Text search
  pageSize: number,           // Results per page
  page: number,               // Current page (0-indexed)
  sortBy: string,             // Sort field
  sortType: 'ASC' | 'DESC'   // Sort direction
}
```

## Backend Package Structure

### Applications Layer
- `applications/command/` - Command handlers (Create, Update, Delete operations)
- `applications/query/` - Query handlers (Read operations)

### Domain Layer
- `domain/dto/` - Data transfer objects
- `domain/services/` - Service interfaces
- `domain/entities/` - JPA entities

### Infrastructure Layer
- `infrastructure/repository/command/` - Write repository implementations
- `infrastructure/repository/query/` - Read repository implementations
- `infrastructure/services/` - Service implementations
- `infrastructure/services/reporte/` - PDF components (HeaderDrawer, FooterDrawer, TableSectionDrawer)

### Controller Layer
- `controller/` - REST endpoints handling HTTP requests

### Configuration
- `PostgresDBWriteConfiguration` - Write datasource (CQRS write path)
- `PostgresDBReadConfiguration` - Read datasource (CQRS read path)

## External Integrations

- **Keycloak**: OAuth2/JWT auth for `kynsoft` realm
- **AWS S3/CloudFront**: Image and document storage
- **Eureka**: Service discovery registration
- **Redis**: Caching layer for performance
- **Spring Cloud Config**: External configuration management (optional)

## User Preferences

**From existing CLAUDE.md files:**
- Execute tasks directly without asking for confirmation
- Make autonomous decisions based on context and best practices
- Act proactively when requirements are unclear

## Development Notes

- Java 21 required for backend
- Node.js 14+ required for frontend
- Both projects use git for version control (separate repos)
- Maven wrapper (`./mvnw`) included for Java builds
- NPM for Node.js dependency management
- TypeScript for frontend type safety (Composition API with `<script setup lang="ts">`)
- ESLint configured for code quality enforcement
