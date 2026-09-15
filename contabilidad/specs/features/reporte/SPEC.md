---
feature: reporte
version: 1.0.0
status: implemented
priority: high
depends_on: [finca, trabajador]
---

# Feature: Gestión de Reportes de Jornada Laboral

## Contexto

Un Reporte registra las jornadas de trabajo de los trabajadores en una ubicación específica (bloque/campo/área) durante un mes. Cada reporte tiene un trabajador responsable y contiene múltiples días de trabajo, donde cada día registra las horas trabajadas por cada trabajador.

El sistema permite:
- **Crear reportes** mensuales por bloque/campo/área
- **Registrar días de trabajo** con trabajadores y horas
- **Generar PDFs** individuales y consolidados
- **Calcular métricas** de productividad, ausentismo y rankings
- **Exportar a prenómina** Excel

## Actores

- Usuario autenticado (cualquier rol puede gestionar reportes)

## Dominio

### Entidad: Reporte

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| id | UUID | auto | Identificador único |
| codigo | String(50) | auto | Código único (año_mes_consecutivo) |
| bloque | String(50) | sí | Número/nombre del bloque (texto libre) |
| campo | String(50) | sí | Número/nombre del campo (texto libre) |
| area | String(50) | sí | Área en hectáreas (texto libre) |
| norma | Double | sí | Norma de producción por defecto |
| year | String(50) | sí | Año del reporte |
| mes | String(50) | sí | Mes del reporte (nombre) |
| fecha | String | no | Fecha opcional del reporte |
| trabajadorResponsableId | UUID | sí | Trabajador responsable |
| activo | Boolean | auto | Estado (true = activo, false = eliminado) |
| creadoPor | String | auto | Usuario que creó el reporte |
| fechaCreacion | LocalDateTime | auto | Fecha y hora de creación |
| modificadoPor | String | no | Último usuario que modificó |
| fechaModificacion | LocalDateTime | no | Fecha y hora de última modificación |

> **Nota**: La finca del reporte se deriva del trabajador responsable (trabajadorResponsable.finca).

### Entidad: DiaTrabajo

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| id | UUID | auto | Identificador único |
| reporteId | UUID | sí | Referencia al reporte |
| fecha | LocalDate | sí | Fecha del día de trabajo |

### Entidad: TrabajadorDia

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| id | UUID | auto | Identificador único |
| diaTrabajoId | UUID | sí | Referencia al día de trabajo |
| trabajadorId | UUID | sí | Referencia al trabajador |
| horas | Double | sí | Horas trabajadas (≥0) |
| norma | Double | no | Norma específica (si null, usa Reporte.norma) |

> **Nota**: Si `norma` es null, se usa el valor de `Reporte.norma` como referencia por defecto.

### Campos derivados (response)

| Campo | Descripción |
|-------|-------------|
| trabajadorResponsableNombre | Nombre del trabajador responsable |
| totalDias | Cantidad de días en el reporte |
| totalHoras | Suma de horas de todos los trabajadores |

---

## Reglas de Negocio

### RN-01: Código único auto-generado
El código se genera automáticamente con formato `año_mes_consecutivo` (ej: 2026_08_01). El consecutivo se basa en el máximo código existente para ese año/mes (activos e inactivos).

### RN-02: Trabajador responsable válido
El trabajador responsable debe existir y estar activo en el sistema.

### RN-03: Bloque, campo y área requeridos
Los campos bloque, campo y área son obligatorios y no pueden estar vacíos.

### RN-04: Mes válido
El mes debe ser uno de los 12 meses del año en español (Enero, Febrero, ..., Diciembre).

### RN-05: Día único por fecha
No puede haber dos DiaTrabajo con la misma fecha dentro del mismo reporte.

### RN-06: Trabajador único por día
Un trabajador solo puede aparecer una vez en cada DiaTrabajo.

### RN-07: Horas válidas por tipo de día
Las horas se validan según el día de la semana:
- **Lunes a Viernes**: máximo 8 horas
- **Sábado**: máximo 4 horas
- **Domingo**: 0 horas (no se permite trabajo)

### RN-08: Soft delete
La eliminación es lógica (activo = false), manteniendo el registro para auditoría.

### RN-09: No eliminar con días registrados
Un reporte no puede eliminarse si tiene días de trabajo registrados. Primero deben eliminarse los días.

### RN-10: Fecha del día dentro del mes
La fecha de un DiaTrabajo debe corresponder al mes y año del reporte.

### RN-11: Finca derivada del responsable
La finca del reporte se obtiene automáticamente del trabajador responsable (`trabajadorResponsable.finca`). No se almacena un `fincaId` explícito.

### RN-12: Norma por defecto heredable
`TrabajadorDia.norma` es opcional. Si no se especifica (null), se usa `Reporte.norma` como valor de referencia en los cálculos.

### RN-13: Días de trabajo editables
Un día de trabajo (DiaTrabajo) puede ser editado después de creado:
- Modificar horas y norma de trabajadores existentes
- Agregar nuevos trabajadores al día
- Eliminar trabajadores del día

### RN-14: Bloque, campo y área sin validación
Los campos bloque, campo y área son texto libre. No se validan contra ningún catálogo o entidad.

### RN-15: Campos de auditoría
Los reportes registran automáticamente:
- `creadoPor`: usuario que crea el reporte
- `fechaCreacion`: timestamp de creación
- `modificadoPor`: último usuario que modifica
- `fechaModificacion`: timestamp de última modificación

---

## API Contract

### Base URL
```
/api/reporte
```

### Autenticación
Todos los endpoints requieren usuario autenticado (Bearer Token JWT).

---

### GET /api/reporte/next-codigo
**Obtener próximo código disponible**

#### Request
```
?year=2026&mes=Agosto
```

#### Response 200
```json
{
  "codigo": "2026_08_35"
}
```

---

### POST /api/reporte
**Crear reporte**

#### Request
```json
{
  "bloque": "3",
  "campo": "2",
  "area": "0.6",
  "norma": "1.5",
  "year": "2026",
  "mes": "Agosto",
  "fecha": "2026-08-21",
  "trabajadorResponsableId": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Response 200
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "codigo": "2026_08_35"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Datos inválidos (bloque/campo/area vacíos) |
| 400 | Trabajador responsable no encontrado (RN-02) |
| 400 | Mes inválido (RN-04) |
| 401 | No autenticado |

---

### GET /api/reporte/{id}
**Obtener reporte por ID**

#### Response 200
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "codigo": "2026_08_35",
  "bloque": "3",
  "campo": "2",
  "area": "0.6",
  "norma": "1.5",
  "year": "2026",
  "mes": "Agosto",
  "fecha": "2026-08-21",
  "trabajadorResponsableId": "550e8400-e29b-41d4-a716-446655440000",
  "trabajadorResponsableNombre": "Juan Pérez",
  "activo": true
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Reporte no encontrado |
| 401 | No autenticado |

---

### PUT /api/reporte/{id}
**Actualizar reporte**

#### Request
```json
{
  "bloque": "4",
  "campo": "3",
  "area": "0.8",
  "norma": "1.8",
  "trabajadorResponsableId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Nota:** El código, año y mes no se pueden modificar.

#### Response 200
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Reporte no encontrado |
| 400 | Reporte ya eliminado |
| 400 | Trabajador responsable no encontrado |
| 401 | No autenticado |

---

### DELETE /api/reporte/{id}
**Eliminar reporte (soft delete)**

#### Response 200
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Reporte no encontrado |
| 400 | Reporte ya eliminado |
| 400 | Tiene días registrados (RN-09) |
| 401 | No autenticado |

---

### POST /api/reporte/search
**Búsqueda paginada**

#### Request
```json
{
  "page": 0,
  "pageSize": 20,
  "filter": [
    {
      "key": "year",
      "operator": "EQUAL",
      "value": "2026"
    },
    {
      "key": "mes",
      "operator": "EQUAL",
      "value": "Agosto"
    },
    {
      "key": "activo",
      "operator": "EQUAL",
      "value": true
    }
  ],
  "query": ""
}
```

#### Response 200
```json
{
  "content": [
    {
      "id": "770e8400-e29b-41d4-a716-446655440000",
      "codigo": "2026_08_35",
      "bloque": "3",
      "campo": "2",
      "area": "0.6",
      "norma": "1.5",
      "year": "2026",
      "mes": "Agosto",
      "trabajadorResponsableNombre": "Juan Pérez"
    }
  ],
  "totalElements": 35,
  "totalPages": 2,
  "page": 0,
  "pageSize": 20
}
```

---

### POST /api/reporte/{reporteId}/dias
**Agregar día de trabajo**

#### Request
```json
{
  "fecha": "2026-08-20",
  "trabajadores": [
    {
      "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
      "horas": "8",
      "norma": "1.5"
    },
    {
      "trabajadorId": "660e8400-e29b-41d4-a716-446655440002",
      "horas": "6",
      "norma": "1.5"
    }
  ]
}
```

#### Response 201
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000",
  "fecha": "2026-08-20",
  "cantidadTrabajadores": 2
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Reporte no encontrado |
| 400 | Fecha ya existe en el reporte (RN-05) |
| 400 | Fecha fuera del mes del reporte (RN-10) |
| 400 | Trabajador duplicado en el día (RN-06) |
| 400 | Trabajador no encontrado |
| 401 | No autenticado |

---

### GET /api/reporte/{reporteId}/dias
**Obtener días de un reporte**

#### Response 200
```json
{
  "content": [
    {
      "id": "880e8400-e29b-41d4-a716-446655440000",
      "fecha": "2026-08-20",
      "trabajadores": [
        {
          "id": "990e8400-e29b-41d4-a716-446655440001",
          "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
          "trabajadorNombre": "María García",
          "trabajadorRuc": "12345678901",
          "horas": "8",
          "norma": "1.5"
        }
      ]
    }
  ],
  "totalDias": 15,
  "totalHoras": 240
}
```

---

### DELETE /api/reporte/dias/{diaId}
**Eliminar día de trabajo**

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Día no encontrado |
| 401 | No autenticado |

---

### GET /api/reporte/{id}/pdf
**Generar PDF del reporte**

#### Response 200
- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename="reporte_{id}.pdf"`

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Reporte no encontrado |
| 500 | Error al generar PDF |

---

### GET /api/reporte/consolidado
**Reporte consolidado mensual**

#### Request
```
?year=2026&mes=Agosto
```

#### Response 200
```json
{
  "year": "2026",
  "mes": "Agosto",
  "totalReportes": 35,
  "totalHoras": 5600,
  "trabajadores": [
    {
      "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
      "nombre": "María García",
      "ruc": "12345678901",
      "totalHoras": 160,
      "diasTrabajados": 20
    }
  ]
}
```

---

### GET /api/reporte/consolidado/pdf
**Exportar consolidado a PDF**

#### Request
```
?year=2026&mes=Agosto
```

#### Response 200
- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename="reporte_consolidado_2026_Agosto.pdf"`

---

### GET /api/reporte/consolidado-por-responsable
**Consolidado agrupado por responsable**

#### Request
```
?year=2026&mes=Agosto
```

#### Response 200
```json
{
  "year": "2026",
  "mes": "Agosto",
  "responsables": [
    {
      "responsableId": "550e8400-e29b-41d4-a716-446655440000",
      "responsableNombre": "Juan Pérez",
      "cantidadReportes": 5,
      "totalHoras": 800
    }
  ]
}
```

---

### GET /api/reporte/consolidado/trabajadores-excedidos
**Trabajadores con horas excedidas**

#### Request
```
?year=2026&mes=Agosto
```

#### Response 200
```json
{
  "content": [
    {
      "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
      "nombre": "María García",
      "ruc": "12345678901",
      "horasTrabajadas": 200,
      "horasPermitidas": 176,
      "horasExcedidas": 24
    }
  ],
  "totalExcedidos": 5
}
```

---

### GET /api/reporte/consolidado/trabajadores-faltantes
**Trabajadores sin reportar en el mes**

#### Request
```
?year=2026&mes=Agosto
```

#### Response 200
```json
{
  "content": [
    {
      "trabajadorId": "660e8400-e29b-41d4-a716-446655440003",
      "nombre": "Pedro López",
      "ruc": "98765432101",
      "fincaNombre": "Finca La Esperanza"
    }
  ],
  "totalFaltantes": 3
}
```

---

### GET /api/reporte/metricas/ausentismo
**Métricas de ausentismo**

#### Request
```
?year=2026&mes=Agosto&trabajadorId={opcional}
```

#### Response 200
```json
{
  "content": [
    {
      "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
      "nombre": "María García",
      "diasLaborables": 22,
      "diasTrabajados": 20,
      "diasAusente": 2,
      "porcentajeAsistencia": 90.9
    }
  ]
}
```

---

### GET /api/reporte/metricas/productividad
**Métricas de productividad**

#### Request
```
?year=2026&mes=Agosto&trabajadorId={opcional}
```

#### Response 200
```json
{
  "content": [
    {
      "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
      "nombre": "María García",
      "horasTrabajadas": 160,
      "normaCumplida": 120,
      "porcentajeCumplimiento": 75.0
    }
  ]
}
```

---

### GET /api/reporte/metricas/rankings
**Rankings de trabajadores**

#### Request
```
?year=2026&mes=Agosto&cargo={opcional}
```

#### Response 200
```json
{
  "content": [
    {
      "posicion": 1,
      "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
      "nombre": "María García",
      "cargo": "Operario",
      "horasTrabajadas": 180,
      "normaPromedio": 1.8
    }
  ]
}
```

---

### POST /api/reporte/consolidado/escribir-prenomina
**Exportar horas a Excel de prenómina**

#### Request
```json
{
  "12345678901": 160.5,
  "98765432101": 144.0
}
```

#### Response 200
- Content-Type: `application/vnd.ms-excel`
- Headers: `X-Actualizados`, `X-No-Encontrados`, `X-Total`

---

## Criterios de Aceptación

### Escenario: Crear reporte exitoso
```gherkin
Given un usuario autenticado
And existe trabajador "Juan Pérez" activo
When crea reporte con bloque=3, campo=2, area=0.6, year=2026, mes=Agosto
Then responde 200
And se genera código "2026_08_XX" automáticamente
And se crea el reporte con activo = true
```

### Escenario: Código consecutivo correcto
```gherkin
Given existen reportes con códigos 2026_08_33, 2026_08_34
When crea nuevo reporte para año=2026, mes=Agosto
Then el código asignado es 2026_08_35
```

### Escenario: Código considera inactivos
```gherkin
Given existe reporte con código 2026_08_34 (activo=false, eliminado)
And existen 33 reportes activos
When crea nuevo reporte para año=2026, mes=Agosto
Then el código asignado es 2026_08_35 (no 34)
```

### Escenario: Rechazar fecha duplicada
```gherkin
Given existe reporte con día 2026-08-20
When intenta agregar otro día con fecha 2026-08-20
Then responde 400
And mensaje indica "Ya existe un día con esta fecha"
```

### Escenario: Rechazar trabajador duplicado en día
```gherkin
Given usuario autenticado
When agrega día con el mismo trabajador dos veces
Then responde 400
And mensaje indica "Trabajador duplicado en el día"
```

### Escenario: Generar PDF individual
```gherkin
Given existe reporte con días y trabajadores
When solicita GET /api/reporte/{id}/pdf
Then responde 200 con Content-Type application/pdf
And el PDF contiene los datos del reporte
```

### Escenario: Consolidado mensual
```gherkin
Given existen 35 reportes para agosto 2026
And cada reporte tiene múltiples días y trabajadores
When solicita GET /api/reporte/consolidado?year=2026&mes=Agosto
Then responde con suma de horas por trabajador
And lista todos los trabajadores que trabajaron en el mes
```

### Escenario: Rechazar más de 8 horas en día laboral
```gherkin
Given existe reporte para agosto 2026
When agrega día miércoles 20/08/2026 con trabajador y horas=9
Then responde 400
And mensaje indica "Las horas no pueden exceder 8 en días laborables"
```

### Escenario: Rechazar más de 4 horas en sábado
```gherkin
Given existe reporte para agosto 2026
When agrega día sábado 24/08/2026 con trabajador y horas=5
Then responde 400
And mensaje indica "Las horas no pueden exceder 4 en sábado"
```

### Escenario: Rechazar trabajo en domingo
```gherkin
Given existe reporte para agosto 2026
When agrega día domingo 25/08/2026 con trabajador y horas=1
Then responde 400
And mensaje indica "No se permite registrar trabajo en domingo"
```

### Escenario: Norma heredada del reporte
```gherkin
Given existe reporte con norma=1.5
When agrega trabajadorDia sin especificar norma
Then el cálculo de productividad usa norma=1.5 del reporte
```

### Escenario: Editar día existente
```gherkin
Given existe reporte con día 20/08/2026 y trabajador María con horas=6
When actualiza horas de María a 8
Then responde 200
And las horas de María son 8
```

### Escenario: Agregar trabajador a día existente
```gherkin
Given existe reporte con día 20/08/2026 y 2 trabajadores
When agrega trabajador Pedro al día 20/08/2026
Then responde 200
And el día tiene 3 trabajadores
```

### Escenario: Campos de auditoría en creación
```gherkin
Given usuario "admin" autenticado
When crea reporte
Then creadoPor = "admin"
And fechaCreacion = timestamp actual
```

### Escenario: Campos de auditoría en modificación
```gherkin
Given existe reporte creado por "admin"
And usuario "supervisor" autenticado
When modifica el reporte
Then modificadoPor = "supervisor"
And fechaModificacion = timestamp actual
```

---

## Arquitectura

### Capas

```
controller/
  └── ReporteController.java

applications/
  ├── command/reporte/
  │   ├── create/
  │   │   ├── CreateReporteCommand.java
  │   │   ├── CreateReporteCommandHandler.java
  │   │   └── CreateReporteRequest.java
  │   ├── update/
  │   │   ├── UpdateReporteCommand.java
  │   │   ├── UpdateReporteCommandHandler.java
  │   │   └── UpdateReporteRequest.java
  │   ├── delete/
  │   │   ├── DeleteReporteCommand.java
  │   │   ├── DeleteReporteCommandHandler.java
  │   │   └── DeleteReporteMessage.java
  │   ├── generatePdf/
  │   │   ├── GenerateReportePdfCommand.java
  │   │   └── GenerateReportePdfCommandHandler.java
  │   └── generateConsolidadoPdf/
  │       ├── GenerateConsolidadoPdfCommand.java
  │       └── GenerateConsolidadoPdfCommandHandler.java
  ├── command/diatrabajo/
  │   ├── create/
  │   └── delete/
  └── query/reporte/
      ├── getById/
      ├── search/
      ├── consolidado/
      ├── consolidadoPorResponsable/
      ├── trabajadoresExcedidos/
      └── trabajadoresFaltantes/

domain/
  ├── dto/
  │   ├── ReporteDto.java
  │   ├── DiaTrabajoDto.java
  │   └── TrabajadorDiaDto.java
  └── services/
      ├── IReporteService.java
      ├── IDiaTrabajoService.java
      └── ITrabajadorDiaService.java

infrastructure/
  ├── entity/
  │   ├── Reporte.java
  │   ├── DiaTrabajo.java
  │   └── TrabajadorDia.java
  ├── repository/
  │   ├── command/
  │   │   ├── ReporteWriteDataJPARepository.java
  │   │   ├── DiaTrabajoWriteDataJPARepository.java
  │   │   └── TrabajadorDiaWriteDataJPARepository.java
  │   └── query/
  │       ├── ReporteReadDataJPARepository.java
  │       ├── DiaTrabajoReadDataJPARepository.java
  │       └── TrabajadorDiaReadDataJPARepository.java
  └── services/
      ├── ReporteServiceImpl.java
      ├── DiaTrabajoServiceImpl.java
      ├── TrabajadorDiaServiceImpl.java
      └── PrenominaExcelService.java
```

---

## Tests Requeridos

### Unit Tests
- [ ] CreateReporteCommandHandler genera código correctamente (RN-01)
- [ ] CreateReporteCommandHandler valida trabajador responsable existe (RN-02)
- [ ] CreateReporteCommandHandler valida campos obligatorios (RN-03)
- [ ] CreateReporteCommandHandler valida mes válido (RN-04)
- [ ] CreateDiaTrabajoCommandHandler valida fecha única (RN-05)
- [ ] CreateDiaTrabajoCommandHandler valida trabajador único por día (RN-06)
- [ ] CreateDiaTrabajoCommandHandler valida horas ≤8h lunes-viernes (RN-07)
- [ ] CreateDiaTrabajoCommandHandler valida horas ≤4h sábado (RN-07)
- [ ] CreateDiaTrabajoCommandHandler rechaza trabajo en domingo (RN-07)
- [ ] CreateDiaTrabajoCommandHandler valida fecha dentro del mes (RN-10)
- [ ] DeleteReporteCommandHandler valida no tiene días (RN-09)
- [ ] generateCodigo busca máximo incluyendo inactivos (RN-01)
- [ ] TrabajadorDia usa norma del reporte cuando norma es null (RN-12)
- [ ] UpdateDiaTrabajoCommandHandler permite editar horas/norma (RN-13)
- [ ] Reporte obtiene finca del trabajador responsable (RN-11)
- [ ] CreateReporteCommandHandler registra creadoPor y fechaCreacion (RN-15)
- [ ] UpdateReporteCommandHandler registra modificadoPor y fechaModificacion (RN-15)

### Integration Tests
- [ ] POST /api/reporte crea con código auto-generado
- [ ] POST /api/reporte rechaza trabajador inexistente
- [ ] POST /api/reporte/{id}/dias agrega día correctamente
- [ ] POST /api/reporte/{id}/dias rechaza fecha duplicada
- [ ] DELETE /api/reporte/{id} rechaza si tiene días
- [ ] GET /api/reporte/{id}/pdf genera PDF válido
- [ ] GET /api/reporte/consolidado calcula horas correctamente
- [ ] GET /api/reporte/consolidado/trabajadores-excedidos identifica excedidos
- [ ] GET /api/reporte/next-codigo retorna siguiente disponible

---

## Cambios Implementados

1. ✅ Generación automática de código (año_mes_consecutivo)
2. ✅ CRUD completo de reportes
3. ✅ Gestión de días de trabajo
4. ✅ Generación de PDF individual
5. ✅ Consolidado mensual
6. ✅ Consolidado por responsable
7. ✅ Trabajadores con horas excedidas
8. ✅ Trabajadores faltantes
9. ✅ Métricas de ausentismo
10. ✅ Métricas de productividad
11. ✅ Rankings de trabajadores
12. ✅ Exportación a prenómina Excel
13. ✅ Fix: generateCodigo busca máximo (no cuenta activos)

---

## Out of Scope

- Aprobación/workflow de reportes
- Firma digital de reportes
- Notificaciones de reportes pendientes
- Integración con sistema de nómina externo
- Múltiples fincas por reporte
- Geolocalización de trabajo
- Fotos/evidencias del trabajo
