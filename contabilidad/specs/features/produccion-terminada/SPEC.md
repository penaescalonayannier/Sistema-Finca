---
feature: produccion-terminada
version: 1.0.0
status: implemented
priority: high
depends_on: [finca, producto, finca-producto, trabajador]
---

# Feature: Gestión de Producción Terminada

## Contexto

ProduccionTerminada registra la entrega de producción en la finca. Documenta qué producto se produjo, en qué cantidad, quién lo produjo (trabajadorEntrega) y quién lo recibió en almacén (trabajadorRecibe).

Al registrar una producción, el stock del producto en la finca se incrementa automáticamente.

## Actores

- Usuario autenticado (cualquier rol puede registrar producción)

## Dominio

### Entidad: ProduccionTerminada

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| id | UUID | auto | Identificador único |
| fincaId | UUID | sí | Referencia a la finca |
| productoId | UUID | sí | Referencia al producto |
| fecha | LocalDateTime | sí | Fecha de la producción (default: hoy) |
| cantidadTerminada | Integer | sí | Cantidad producida (> 0) |
| trabajadorEntregaId | UUID | sí | Trabajador que produjo/entrega |
| trabajadorRecibeId | UUID | sí | Trabajador que recibe en almacén |
| observaciones | String(500) | no | Observaciones opcionales |
| activo | Boolean | auto | Estado (true = activo, false = anulado) |

### Campos derivados (response)

| Campo | Descripción |
|-------|-------------|
| fincaCode | Código de la finca |
| fincaName | Nombre de la finca |
| productoCode | Código del producto |
| productoName | Nombre del producto |
| trabajadorEntregaNombre | Nombre del trabajador que entrega |
| trabajadorRecibeNombre | Nombre del trabajador que recibe |

---

## Reglas de Negocio

### RN-01: Producto asignado a finca
El producto debe estar previamente asignado a la finca (existir en FincaProducto) antes de registrar producción.

### RN-02: Incremento automático de stock
Al crear una producción, el stock del producto en FincaProducto se incrementa automáticamente por la cantidad terminada.

### RN-03: Cantidad mayor a cero
La cantidad terminada debe ser mayor a 0.

### RN-04: Trabajadores de la misma finca
Ambos trabajadores (entrega y recibe) deben pertenecer a la misma finca del registro.

### RN-05: Trabajadores diferentes
El trabajador que entrega y el que recibe deben ser personas diferentes.

### RN-06: Ajuste al editar
Al modificar la cantidad de una producción, se debe ajustar el stock automáticamente y registrar el movimiento en auditoría.

### RN-07: Reversión al anular
Al eliminar/anular una producción, el stock se revierte (se descuenta la cantidad) y se registra en auditoría.

### RN-08: Fecha seleccionable
El usuario puede seleccionar la fecha de producción. Por defecto es la fecha actual.

### RN-09: Soft delete
La eliminación es lógica (activo = false), manteniendo el registro para auditoría.

---

## API Contract

### Base URL
```
/api/produccion-terminada
```

### Autenticación
Todos los endpoints requieren usuario autenticado (Bearer Token JWT).

---

### POST /api/produccion-terminada
**Crear producción terminada**

#### Request
```json
{
  "fincaId": "550e8400-e29b-41d4-a716-446655440000",
  "productoId": "660e8400-e29b-41d4-a716-446655440001",
  "fecha": "2026-08-20T10:00:00",
  "cantidadTerminada": 100,
  "trabajadorEntregaId": "770e8400-e29b-41d4-a716-446655440001",
  "trabajadorRecibeId": "770e8400-e29b-41d4-a716-446655440002",
  "observaciones": "Producción matutina"
}
```

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000",
  "stockAnterior": 50,
  "stockNuevo": 150
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Finca no encontrada |
| 400 | Producto no encontrado |
| 400 | Producto no asignado a la finca (RN-01) |
| 400 | Cantidad <= 0 (RN-03) |
| 400 | Trabajador entrega no encontrado |
| 400 | Trabajador recibe no encontrado |
| 400 | Trabajadores no pertenecen a la finca (RN-04) |
| 400 | Trabajadores son la misma persona (RN-05) |
| 401 | No autenticado |

---

### GET /api/produccion-terminada/{id}
**Obtener producción por ID**

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000",
  "fincaId": "550e8400-e29b-41d4-a716-446655440000",
  "fincaCode": "FINCA01",
  "fincaName": "Finca La Esperanza",
  "productoId": "660e8400-e29b-41d4-a716-446655440001",
  "productoCode": "CAFE001",
  "productoName": "Café Arábica",
  "fecha": "2026-08-20T10:00:00",
  "cantidadTerminada": 100,
  "trabajadorEntregaId": "770e8400-e29b-41d4-a716-446655440001",
  "trabajadorEntregaNombre": "Juan Pérez",
  "trabajadorRecibeId": "770e8400-e29b-41d4-a716-446655440002",
  "trabajadorRecibeNombre": "María García",
  "observaciones": "Producción matutina",
  "activo": true
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Producción no encontrada |
| 401 | No autenticado |

---

### PUT /api/produccion-terminada/{id}
**Actualizar producción**

#### Request
```json
{
  "cantidadTerminada": 120,
  "trabajadorEntregaId": "770e8400-e29b-41d4-a716-446655440001",
  "trabajadorRecibeId": "770e8400-e29b-41d4-a716-446655440002",
  "observaciones": "Producción matutina - corregida"
}
```

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000",
  "stockAnterior": 150,
  "stockNuevo": 170,
  "ajuste": 20
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Producción no encontrada |
| 400 | Producción ya anulada |
| 400 | Cantidad <= 0 (RN-03) |
| 400 | Trabajadores no pertenecen a la finca (RN-04) |
| 400 | Trabajadores son la misma persona (RN-05) |
| 400 | Ajuste dejaría stock negativo |
| 401 | No autenticado |

---

### DELETE /api/produccion-terminada/{id}
**Anular producción (soft delete)**

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000",
  "stockAnterior": 170,
  "stockNuevo": 50,
  "cantidadRevertida": 120
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Producción no encontrada |
| 400 | Producción ya anulada |
| 400 | Reversión dejaría stock negativo |
| 401 | No autenticado |

---

### POST /api/produccion-terminada/search
**Búsqueda paginada**

#### Request
```json
{
  "page": 0,
  "pageSize": 20,
  "filter": [
    {
      "key": "fincaId",
      "operator": "EQUAL",
      "value": "550e8400-e29b-41d4-a716-446655440000"
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
      "id": "880e8400-e29b-41d4-a716-446655440000",
      "fincaCode": "FINCA01",
      "fincaName": "Finca La Esperanza",
      "productoCode": "CAFE001",
      "productoName": "Café Arábica",
      "fecha": "2026-08-20T10:00:00",
      "cantidadTerminada": 120,
      "trabajadorEntregaNombre": "Juan Pérez",
      "trabajadorRecibeNombre": "María García"
    }
  ],
  "totalElements": 25,
  "totalPages": 2,
  "page": 0,
  "pageSize": 20
}
```

---

### GET /api/produccion-terminada/por-finca/{fincaId}
**Obtener producciones por finca**

#### Request
```
?fechaInicio=2026-08-01&fechaFin=2026-08-31
```

#### Response 200
```json
{
  "content": [...],
  "totalElements": 15
}
```

---

### GET /api/produccion-terminada/por-producto/{productoId}
**Obtener producciones por producto**

#### Response 200
```json
{
  "content": [...],
  "totalElements": 10
}
```

---

### GET /api/produccion-terminada/por-trabajador/{trabajadorId}
**Obtener producciones por trabajador (entrega o recibe)**

#### Request
```
?rol=ENTREGA  // ENTREGA | RECIBE | AMBOS (default)
```

#### Response 200
```json
{
  "content": [...],
  "totalElements": 8
}
```

---

### GET /api/produccion-terminada/por-fecha
**Obtener producciones por rango de fechas**

#### Request
```
?fechaInicio=2026-08-01&fechaFin=2026-08-31&fincaId=550e8400-e29b-41d4-a716-446655440000
```

#### Response 200
```json
{
  "content": [...],
  "totalElements": 30,
  "totalCantidad": 2500
}
```

---

## Criterios de Aceptación

### Escenario: Crear producción exitosa
```gherkin
Given un usuario autenticado
And existe Finca "FINCA01"
And existe Producto "CAFE001" asignado a la finca con stock = 50
And existen trabajadores "Juan" y "María" en la finca
When crea producción con cantidad = 100, entrega = Juan, recibe = María
Then responde 200
And se crea registro de ProduccionTerminada
And el stock del producto = 150
And se registra movimiento ENTRADA_PRODUCCION
```

### Escenario: Rechazar producto no asignado
```gherkin
Given un usuario autenticado
And existe Finca "FINCA01"
And existe Producto "CAFE001" NO asignado a la finca
When intenta crear producción
Then responde 400
And mensaje indica "Producto no asignado a la finca"
```

### Escenario: Rechazar mismo trabajador
```gherkin
Given un usuario autenticado
And existe trabajador "Juan"
When crea producción con entrega = Juan y recibe = Juan
Then responde 400
And mensaje indica "Trabajadores deben ser diferentes"
```

### Escenario: Rechazar trabajador de otra finca
```gherkin
Given un usuario autenticado
And existe Finca "FINCA01" y "FINCA02"
And trabajador "Juan" pertenece a FINCA01
And trabajador "Pedro" pertenece a FINCA02
When crea producción en FINCA01 con entrega = Juan y recibe = Pedro
Then responde 400
And mensaje indica "Trabajadores deben pertenecer a la finca"
```

### Escenario: Editar cantidad ajusta stock
```gherkin
Given existe producción con cantidad = 100 y stock actual = 150
When edita producción cambiando cantidad a 120
Then responde 200
And el stock = 170 (150 + 20)
And se registra movimiento de ajuste
```

### Escenario: Anular producción revierte stock
```gherkin
Given existe producción con cantidad = 100 y stock actual = 150
When anula la producción
Then responde 200
And la producción queda con activo = false
And el stock = 50 (150 - 100)
And se registra movimiento de reversión
```

### Escenario: No anular si deja stock negativo
```gherkin
Given existe producción con cantidad = 100 y stock actual = 80
When intenta anular la producción
Then responde 400
And mensaje indica "Reversión dejaría stock negativo"
And la producción sigue activa
```

---

## Arquitectura

### Capas a modificar

```
controller/
  └── ProduccionTerminadaController.java

applications/
  ├── command/produccionTerminada/
  │   ├── create/
  │   │   ├── CreateProduccionTerminadaCommand.java
  │   │   ├── CreateProduccionTerminadaCommandHandler.java
  │   │   └── CreateProduccionTerminadaRequest.java
  │   ├── update/
  │   │   ├── UpdateProduccionTerminadaCommand.java
  │   │   ├── UpdateProduccionTerminadaCommandHandler.java
  │   │   └── UpdateProduccionTerminadaRequest.java
  │   └── delete/
  │       ├── DeleteProduccionTerminadaCommand.java
  │       └── DeleteProduccionTerminadaCommandHandler.java
  └── query/produccionTerminada/
      ├── getById/
      ├── search/
      ├── porFinca/
      ├── porProducto/
      ├── porTrabajador/
      └── porFecha/

domain/
  ├── dto/
  │   └── ProduccionTerminadaDto.java
  └── services/
      └── IProduccionTerminadaService.java

infrastructure/
  ├── entity/
  │   └── ProduccionTerminada.java
  ├── repository/
  │   ├── command/
  │   │   └── ProduccionTerminadaWriteDataJPARepository.java
  │   └── query/
  │       └── ProduccionTerminadaReadDataJPARepository.java
  └── services/
      └── ProduccionTerminadaServiceImpl.java
```

---

## Tests Requeridos

### Unit Tests
- [x] CreateProduccionTerminadaCommandHandler valida producto asignado a finca (RN-01)
- [x] CreateProduccionTerminadaCommandHandler valida cantidad > 0 (RN-03)
- [x] CreateProduccionTerminadaCommandHandler valida trabajadores de misma finca (RN-04)
- [x] CreateProduccionTerminadaCommandHandler valida trabajadores diferentes (RN-05)
- [x] CreateProduccionTerminadaCommandHandler incrementa stock (RN-02)
- [x] UpdateProduccionTerminadaCommandHandler ajusta stock correctamente (RN-06)
- [x] DeleteProduccionTerminadaCommandHandler revierte stock (RN-07)
- [x] DeleteProduccionTerminadaCommandHandler valida no dejar stock negativo

### Integration Tests
- [x] POST /api/produccion-terminada incrementa stock
- [x] POST /api/produccion-terminada rechaza producto no asignado
- [x] POST /api/produccion-terminada rechaza mismo trabajador
- [x] PUT /api/produccion-terminada ajusta stock
- [x] DELETE /api/produccion-terminada revierte stock
- [x] DELETE /api/produccion-terminada rechaza si stock negativo
- [x] GET /api/produccion-terminada/por-finca filtra correctamente
- [x] GET /api/produccion-terminada/por-fecha calcula total

### Test Files Created

**Unit Tests:**
- `src/test/java/com/kynsoft/report/infrastructure/services/ProduccionTerminadaServiceImplTest.java`
  - Tests para create(): validación cantidad (RN-03), producto asignado (RN-01), trabajadores iguales (RN-05), trabajadores de otra finca (RN-04), incremento stock (RN-02)
  - Tests para update(): producción no encontrada, producción anulada, ajuste stock negativo (RN-06), actualización correcta
  - Tests para delete(): producción no encontrada, ya anulada, reversión negativa (RN-07), eliminación correcta
  - Tests para findById(): encontrar y lanzar excepción

**Integration Tests:**
- `src/test/java/com/kynsoft/report/controller/ProduccionTerminadaControllerIntegrationTest.java`
  - Tests para POST /: crear producción
  - Tests para PUT /{id}: actualizar producción
  - Tests para DELETE /{id}: eliminar/anular producción
  - Tests para GET /{id}: obtener por ID
  - Tests para GET /por-finca/{fincaId}: filtrar por finca
  - Tests para GET /por-producto/{productoId}: filtrar por producto
  - Tests para GET /por-trabajador/{trabajadorId}: filtrar por trabajador (entrega/recibe)
  - Tests para GET /por-fecha: filtrar por rango de fechas

---

## Cambios Pendientes

Para alinear el código actual con esta spec:

1. **Validar producto asignado a finca** (RN-01)
2. **Incrementar stock automáticamente** (RN-02) - Llamar a FincaProductoService
3. **Validar trabajadores de misma finca** (RN-04)
4. **Validar trabajadores diferentes** (RN-05)
5. **Ajustar stock al editar** (RN-06)
6. **Revertir stock al anular** (RN-07)
7. **Validar no dejar stock negativo** al editar/anular
8. **Agregar endpoints de consulta** - por finca, producto, trabajador, fecha
9. **Registrar movimientos en auditoría** - Integrar con MovimientoStock
10. **Actualizar frontend** - Formularios con validaciones

---

## Out of Scope

- Producción parcial (en progreso)
- Control de calidad
- Mermas/pérdidas
- Costos de producción
- Lotes de producción
- Integración con nómina
