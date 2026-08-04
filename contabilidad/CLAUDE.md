# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 3.3 microservice for generating PDF reports using JasperReports and Apache PDFBox. Part of a medical/healthcare system (Medinec) that generates documents like medical prescriptions, clinical history summaries, and account statements.

## Build and Run Commands

```bash
# Build (skip tests)
./mvnw clean package -DskipTests

# Run locally (requires PostgreSQL on localhost:5432)
./mvnw spring-boot:run

# Docker build (requires PACKAGE_TOKEN for private Maven repository)
docker build --build-arg PACKAGE_TOKEN=<token> -t report:1.0.0 .
```

The application runs on port 9908 (dev profile) or 9909 (Docker).

## Architecture

### CQRS Pattern
The project uses Command Query Responsibility Segregation with a shared library (`com.knsof:share`):
- **Commands**: `applications/command/` - Write operations handled by `ICommandHandler`
- **Queries**: `applications/query/` - Read operations handled by `IQueryHandler`
- **Mediator**: `IMediator` dispatches commands/queries to their handlers

### Database Configuration
Separate read/write datasources for CQRS:
- `PostgresDBWriteConfiguration`: Manages write operations (`infrastructure/repository/command/`)
- `PostgresDBReadConfiguration`: Manages read operations (`infrastructure/repository/query/`)

### Report Generation
Two PDF generation approaches:
1. **JasperReports**: Templates in `src/main/resources/templates/` (`.jrxml`/`.jasper` files). Service: `IReportService`
2. **PDFBox**: Programmatic PDF generation with custom drawing. Service: `IReportServicePdfBox` with implementation in `ReportServicePdfBoxImpl`

### Key Packages
- `controller/` - REST endpoints (`/api/report`, `/api/estado-cuenta`, `/api/cuenta110`)
- `domain/dto/` - Data transfer objects
- `domain/services/` - Service interfaces
- `infrastructure/services/` - Service implementations
- `infrastructure/services/reporte/` - PDF component drawers (HeaderDrawer, FooterDrawer, TableSectionDrawer, etc.)

## External Dependencies

- **Keycloak**: OAuth2/JWT authentication configured for `kynsoft` realm
- **AWS S3/CloudFront**: File storage for images and documents
- **Eureka**: Service discovery registration
- **Redis**: Caching layer
- **Spring Cloud Config**: External configuration (optional)

## API Endpoints

- `POST /api/report/receta-medica` - Generate medical prescription PDF
- `POST /api/estado-cuenta` - Create account statement
- `GET /api/estado-cuenta/{id}` - Get account statement by ID
- `POST /api/estado-cuenta/search` - Search account statements
- `GET /api/estado-cuenta/export` - Export to Excel
- `POST /api/estado-cuenta/upload-xml` - Import from XML
