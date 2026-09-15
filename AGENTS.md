# Repository Guidelines

## Estructura del proyecto

Sistema Finca reúne tres módulos principales. `contabilidad/` es el backend Spring Boot 3 (Java 21), con código en `src/main/java/com/kynsoft/report/`, recursos y migraciones Flyway en `src/main/resources/`, y pruebas en `src/test/`. Respete la separación CQRS: controllers, `applications/command` y `applications/query`, DTOs/servicios de dominio e infraestructura (entidades, repositorios y servicios). `mi-proyecto-vue/mi-proyecto/` contiene el frontend Vue 3 + TypeScript: páginas en `src/views/`, componentes reutilizables en `src/components/`, clientes HTTP en `src/services/` y tipos en `src/types/`. `share/` es la librería Java compartida. La infraestructura local está en `docker-compose.yml`; las especificaciones funcionales viven en `contabilidad/specs/`.

## Desarrollo, build y pruebas

Desde la raíz, ejecute `./start-all.sh` para iniciar backend y frontend. Para contenedores, use `docker compose up --build` (PostgreSQL: 5433, Redis: 6380, API: 9908, UI: 8080).

En `contabilidad/`, use `./start-dev.sh` para desarrollo, `./mvnw test` para la suite y `./mvnw clean package -DskipTests` para crear el JAR. Para un caso concreto: `./mvnw test -Dtest=FincaProductoServiceImplTest`. En `mi-proyecto-vue/mi-proyecto/`, ejecute `npm install`, `npm run serve`, `npm run build`, `npm run lint` y `npm run type-check` antes de entregar cambios de interfaz.

## Estilo y convenciones

Mantenga el estilo ya presente en cada archivo. Java usa paquetes en minúsculas, clases `PascalCase`, métodos/campos `camelCase` y sufijos explícitos como `CreateXCommand`, `XServiceImpl` y `XController`. Preserve el flujo controller → mediator/handler → servicio → repositorio y separe lectura/escritura. En Vue, nombre componentes y vistas en `PascalCase` (`FincaList.vue`), servicios como `FincaService.ts` y composables como `useNotification.ts`. Use TypeScript tipado; ESLint es la fuente de formato y reglas del frontend.

## Pruebas y datos

Agregue o adapte pruebas JUnit 5/Mockito junto a la capa modificada, con nombres `*Test` o `*IntegrationTest`. Las pruebas usan el perfil definido en `contabilidad/src/test/resources/application-test.properties`; no dependa de servicios externos. Añada migraciones ordenadas en `contabilidad/src/main/resources/db/migration/` con el patrón `V<N>__descripcion_snake_case.sql`; no edite migraciones ya aplicadas.

## Commits y pull requests

El historial usa mensajes breves en español y Conventional Commits, por ejemplo `feat: agregar reporte de stock` o `fix: validar saldo de trabajador`. Haga commits acotados por cambio. En cada PR explique el impacto, enumere migraciones/configuración necesaria, vincule el issue si existe e incluya capturas para cambios visuales. Confirme que build, pruebas, lint y verificación de tipos relevantes pasen antes de solicitar revisión.
