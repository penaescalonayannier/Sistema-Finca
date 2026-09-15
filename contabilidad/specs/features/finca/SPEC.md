---
feature: finca
version: 1.0.0
status: implemented
priority: high
depends_on: []
---

# Feature: Gestión de Fincas

## Contexto

Finca representa las unidades productivas del sistema. Cada finca tiene trabajadores asignados, productos en inventario y registros de producción. Es la entidad central del sistema.

## Actores

- Usuario autenticado (cualquier rol puede gestionar fincas)

## Dominio

### Entidad: Finca

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| id | UUID | auto | Identificador único |
| code | String(11) | sí | Código único (5-11 dígitos numéricos) |
| name | String(100) | sí | Nombre de la finca |
| description | String(500) | no | Descripción |
| direccion | String(200) | no | Dirección/ubicación |
| telefono | String(20) | no | Teléfono de contacto |
| responsableId | UUID | condicional | Trabajador responsable (ver RN-04) |
| area | Double | sí | Área en hectáreas (> 0) |
| activo | Boolean | auto | Estado (true = activa, false = inactiva) |

### Campos derivados (response)

| Campo | Descripción |
|-------|-------------|
| responsableNombre | Nombre del trabajador responsable |
| cantidadTrabajadores | Número de trabajadores asignados |
| cantidadProductos | Número de productos asignados |

---

## Reglas de Negocio

### RN-01: Código único
El código de finca debe ser único en todo el sistema.

### RN-02: Formato de código
El código debe tener entre 5 y 11 dígitos numéricos.

### RN-03: Área obligatoria y positiva
El área debe ser mayor a 0.

### RN-04: Responsable condicional
- Al **crear** una finca: responsable es opcional (no hay trabajadores asignados aún)
- Al **actualizar** una finca con trabajadores asignados: responsable es obligatorio

### RN-05: Responsable de la misma finca
El trabajador responsable debe pertenecer a la misma finca.

### RN-06: Soft delete con advertencia
Al eliminar (desactivar) una finca, se debe verificar si tiene:
- Trabajadores asignados
- Productos asignados (FincaProducto)
- Producciones registradas

Si existe alguna referencia, se muestra advertencia pero se permite continuar.

### RN-07: Reactivación permitida
Una finca inactiva puede ser reactivada cambiando activo = true.

### RN-08: Código no editable
Una vez creada la finca, el código no puede ser modificado.

---

## API Contract

### Base URL
```
/api/finca
```

### Autenticación
Todos los endpoints requieren usuario autenticado (Bearer Token JWT).

---

### POST /api/finca
**Crear finca**

#### Request
```json
{
  "code": "12345",
  "name": "Finca La Esperanza",
  "description": "Finca productora de café y cacao",
  "direccion": "Km 5 Carretera Norte",
  "telefono": "555-1234",
  "area": 25.5
}
```

**Nota:** `responsableId` es opcional al crear (RN-04).

#### Response 200
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Código vacío o inválido (RN-02) |
| 400 | Código ya existe (RN-01) |
| 400 | Nombre vacío |
| 400 | Área <= 0 (RN-03) |
| 401 | No autenticado |

---

### GET /api/finca/{id}
**Obtener finca por ID**

#### Response 200
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "12345",
  "name": "Finca La Esperanza",
  "description": "Finca productora de café y cacao",
  "direccion": "Km 5 Carretera Norte",
  "telefono": "555-1234",
  "responsableId": "660e8400-e29b-41d4-a716-446655440001",
  "responsableNombre": "Juan Pérez",
  "area": 25.5,
  "activo": true,
  "cantidadTrabajadores": 15,
  "cantidadProductos": 8
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Finca no encontrada |
| 401 | No autenticado |

---

### GET /api/finca/code/{code}
**Obtener finca por código**

#### Response 200
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "12345",
  "name": "Finca La Esperanza",
  ...
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Finca no encontrada |
| 401 | No autenticado |

---

### PUT /api/finca/{id}
**Actualizar finca**

#### Request
```json
{
  "name": "Finca La Esperanza - Actualizada",
  "description": "Finca productora de café, cacao y frutas",
  "direccion": "Km 5 Carretera Norte, Sector B",
  "telefono": "555-5678",
  "responsableId": "660e8400-e29b-41d4-a716-446655440001",
  "area": 30.0
}
```

**Nota:** El código no se puede modificar (RN-08).

#### Response 200
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Finca no encontrada |
| 400 | Nombre vacío |
| 400 | Área <= 0 (RN-03) |
| 400 | Responsable no encontrado |
| 400 | Responsable no pertenece a la finca (RN-05) |
| 400 | Responsable requerido (finca tiene trabajadores) (RN-04) |
| 401 | No autenticado |

---

### DELETE /api/finca/{id}
**Desactivar finca (soft delete)**

#### Response 200
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "advertencias": [
    "La finca tiene 15 trabajadores asignados",
    "La finca tiene 8 productos asignados",
    "La finca tiene 120 producciones registradas"
  ]
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Finca no encontrada |
| 400 | Finca ya inactiva |
| 401 | No autenticado |

---

### POST /api/finca/{id}/reactivar
**Reactivar finca**

#### Response 200
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Finca no encontrada |
| 400 | Finca ya está activa |
| 401 | No autenticado |

---

### POST /api/finca/{id}/asignar-responsable
**Asignar responsable a la finca**

#### Request
```json
{
  "responsableId": "660e8400-e29b-41d4-a716-446655440001"
}
```

#### Response 200
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "responsableId": "660e8400-e29b-41d4-a716-446655440001",
  "responsableNombre": "Juan Pérez"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Finca no encontrada |
| 400 | Trabajador no encontrado |
| 400 | Trabajador no pertenece a la finca (RN-05) |
| 401 | No autenticado |

---

### POST /api/finca/search
**Búsqueda paginada**

#### Request
```json
{
  "page": 0,
  "pageSize": 20,
  "filter": [
    {
      "key": "activo",
      "operator": "EQUAL",
      "value": true
    }
  ],
  "query": "Esperanza"
}
```

#### Response 200
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "code": "12345",
      "name": "Finca La Esperanza",
      "responsableNombre": "Juan Pérez",
      "area": 25.5,
      "activo": true,
      "cantidadTrabajadores": 15,
      "cantidadProductos": 8
    }
  ],
  "totalElements": 10,
  "totalPages": 1,
  "page": 0,
  "pageSize": 20
}
```

---

### GET /api/finca/{id}/resumen
**Obtener resumen de la finca**

#### Response 200
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "12345",
  "name": "Finca La Esperanza",
  "area": 25.5,
  "responsableNombre": "Juan Pérez",
  "estadisticas": {
    "totalTrabajadores": 15,
    "totalProductos": 8,
    "totalProduccionMes": 1500,
    "totalSalidasMes": 45,
    "stockTotalValorizado": 25000.00
  }
}
```

---

## Criterios de Aceptación

### Escenario: Crear finca exitosa
```gherkin
Given un usuario autenticado
And no existe finca con código "12345"
When crea finca con código "12345", nombre "Finca Nueva", área = 20.0
Then responde 200
And se crea la finca con activo = true
And responsableId = null (permitido al crear)
```

### Escenario: Rechazar código duplicado
```gherkin
Given un usuario autenticado
And existe finca con código "12345"
When intenta crear otra finca con código "12345"
Then responde 400
And mensaje indica "Código ya existe"
```

### Escenario: Rechazar código inválido
```gherkin
Given un usuario autenticado
When intenta crear finca con código "ABC" (no numérico)
Then responde 400
And mensaje indica "Código debe tener 5-11 dígitos numéricos"
```

### Escenario: Rechazar código muy corto
```gherkin
Given un usuario autenticado
When intenta crear finca con código "1234" (4 dígitos)
Then responde 400
And mensaje indica "Código debe tener 5-11 dígitos numéricos"
```

### Escenario: Actualizar sin responsable cuando hay trabajadores
```gherkin
Given un usuario autenticado
And existe finca con 5 trabajadores asignados
And responsableId = null
When intenta actualizar la finca sin asignar responsable
Then responde 400
And mensaje indica "Responsable requerido: la finca tiene trabajadores asignados"
```

### Escenario: Asignar responsable de otra finca
```gherkin
Given un usuario autenticado
And existe finca "FINCA01"
And existe trabajador "Juan" asignado a "FINCA02"
When intenta asignar "Juan" como responsable de "FINCA01"
Then responde 400
And mensaje indica "Responsable debe pertenecer a la finca"
```

### Escenario: Eliminar con advertencia
```gherkin
Given un usuario autenticado
And existe finca con trabajadores y productos asignados
When elimina la finca
Then responde 200 con advertencias
And la finca queda con activo = false
```

### Escenario: Reactivar finca
```gherkin
Given un usuario autenticado
And existe finca inactiva
When reactiva la finca
Then responde 200
And la finca queda con activo = true
```

---

## Arquitectura

### Capas a modificar

```
controller/
  └── FincaController.java

applications/
  ├── command/finca/
  │   ├── create/
  │   │   ├── CreateFincaCommand.java
  │   │   ├── CreateFincaCommandHandler.java
  │   │   └── CreateFincaRequest.java
  │   ├── update/
  │   │   ├── UpdateFincaCommand.java
  │   │   ├── UpdateFincaCommandHandler.java
  │   │   └── UpdateFincaRequest.java
  │   ├── delete/
  │   │   ├── DeleteFincaCommand.java
  │   │   └── DeleteFincaCommandHandler.java
  │   ├── reactivar/
  │   │   ├── ReactivarFincaCommand.java
  │   │   └── ReactivarFincaCommandHandler.java
  │   └── asignarResponsable/
  │       ├── AsignarResponsableCommand.java
  │       ├── AsignarResponsableCommandHandler.java
  │       └── AsignarResponsableRequest.java
  └── query/finca/
      ├── getById/
      ├── getByCode/
      ├── search/
      └── resumen/

domain/
  ├── dto/
  │   └── FincaDto.java (agregar nuevos campos)
  └── services/
      └── IFincaService.java

infrastructure/
  ├── entity/
  │   └── Finca.java (agregar nuevos campos)
  ├── repository/
  │   ├── command/
  │   │   └── FincaWriteDataJPARepository.java
  │   └── query/
  │       └── FincaReadDataJPARepository.java
  └── services/
      └── FincaServiceImpl.java
```

---

## Tests Requeridos

### Unit Tests
- [ ] CreateFincaCommandHandler valida formato código (5-11 dígitos)
- [ ] CreateFincaCommandHandler valida código único
- [ ] CreateFincaCommandHandler valida área > 0
- [ ] CreateFincaCommandHandler permite responsable null al crear
- [ ] UpdateFincaCommandHandler no permite cambiar código
- [ ] UpdateFincaCommandHandler valida responsable obligatorio si hay trabajadores
- [ ] UpdateFincaCommandHandler valida responsable pertenece a finca
- [ ] DeleteFincaCommandHandler genera advertencias

### Integration Tests
- [ ] POST /api/finca crea correctamente
- [ ] POST /api/finca rechaza código duplicado
- [ ] POST /api/finca rechaza código inválido
- [ ] PUT /api/finca no permite cambiar código
- [ ] PUT /api/finca valida responsable
- [ ] DELETE /api/finca retorna advertencias
- [ ] POST /api/finca/{id}/reactivar funciona
- [ ] POST /api/finca/{id}/asignar-responsable valida pertenencia
- [ ] GET /api/finca/{id}/resumen retorna estadísticas

---

## Cambios Pendientes

Para alinear el código actual con esta spec:

1. **Agregar campos** - direccion, telefono, responsableId, area
2. **Validar formato código** - 5-11 dígitos numéricos
3. **Bloquear edición de código** - En update
4. **Validar responsable condicional** - Obligatorio si hay trabajadores
5. **Validar responsable pertenece a finca** - RN-05
6. **Implementar endpoint /reactivar**
7. **Implementar endpoint /asignar-responsable**
8. **Implementar endpoint /resumen** - Estadísticas
9. **Agregar advertencias al eliminar**
10. **Agregar campos derivados** - cantidadTrabajadores, cantidadProductos
11. **Actualizar frontend**

---

## Out of Scope

- Geolocalización (coordenadas GPS)
- Mapa interactivo
- Fotos/imágenes de la finca
- Documentos legales adjuntos
- Certificaciones
- Historial de responsables
- División en lotes/parcelas
