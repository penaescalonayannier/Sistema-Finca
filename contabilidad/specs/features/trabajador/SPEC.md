---
feature: trabajador
version: 1.0.0
status: implemented
priority: high
depends_on: [finca, grupo, cargo]
---

# Feature: Gestión de Trabajadores

## Contexto

Trabajador representa a las personas que laboran en las fincas. Cada trabajador pertenece a una finca, un grupo de trabajo y tiene un cargo asignado. Los trabajadores participan en procesos como producción, recepción de productos y registro de deudas.

## Actores

- Usuario autenticado (cualquier rol puede gestionar trabajadores)

## Dominio

### Entidad: Trabajador

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| id | UUID | auto | Identificador único |
| ruc | String(11) | sí | Carnet de identidad (único, 11 dígitos) |
| nombre | String(100) | sí | Nombre completo |
| cuenta | String(50) | no | Cuenta bancaria (opcional) |
| fincaId | UUID | sí | Finca a la que pertenece |
| grupoId | UUID | sí | Grupo de trabajo |
| cargoId | UUID | sí | Cargo/posición |
| activo | Boolean | auto | Estado (true = activo, false = inactivo) |

### Campos derivados (response)

| Campo | Descripción |
|-------|-------------|
| fincaCode | Código de la finca |
| fincaName | Nombre de la finca |
| grupoNombre | Nombre del grupo |
| cargoName | Nombre del cargo |
| tieneDeuda | Boolean: tiene deuda pendiente > 0 |
| montoDeuda | Monto de deuda actual |

---

## Reglas de Negocio

### RN-01: RUC único
El RUC (Carnet de Identidad) debe ser único en todo el sistema. No pueden existir dos trabajadores con el mismo RUC.

### RN-02: Formato de RUC
El RUC debe tener exactamente 11 dígitos numéricos.

### RN-03: Finca obligatoria
Todo trabajador debe pertenecer a una finca.

### RN-04: Grupo obligatorio
Todo trabajador debe pertenecer a un grupo de trabajo.

### RN-05: Cargo obligatorio
Todo trabajador debe tener un cargo asignado.

### RN-06: Transferencia entre fincas
Un trabajador puede ser transferido a otra finca actualizando su fincaId.

### RN-07: Soft delete con advertencia
Al eliminar (desactivar) un trabajador, se debe verificar si tiene:
- Deuda pendiente
- Producciones registradas
- Salidas sin pagar

Si existe alguna referencia, se muestra advertencia pero se permite continuar.

### RN-08: Reactivación permitida
Un trabajador inactivo puede ser reactivado cambiando activo = true.

### RN-09: RUC no editable
Una vez creado el trabajador, el RUC no puede ser modificado.

---

## API Contract

### Base URL
```
/api/trabajador
```

### Autenticación
Todos los endpoints requieren usuario autenticado (Bearer Token JWT).

---

### POST /api/trabajador
**Crear trabajador**

#### Request
```json
{
  "ruc": "12345678901",
  "nombre": "Juan Pérez García",
  "cuenta": "1234567890123456",
  "fincaId": "550e8400-e29b-41d4-a716-446655440000",
  "grupoId": "660e8400-e29b-41d4-a716-446655440001",
  "cargoId": "770e8400-e29b-41d4-a716-446655440002"
}
```

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | RUC vacío o inválido (RN-02) |
| 400 | RUC ya existe (RN-01) |
| 400 | Nombre vacío |
| 400 | Finca no encontrada |
| 400 | Grupo no encontrado |
| 400 | Cargo no encontrado |
| 401 | No autenticado |

---

### GET /api/trabajador/{id}
**Obtener trabajador por ID**

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000",
  "ruc": "12345678901",
  "nombre": "Juan Pérez García",
  "cuenta": "1234567890123456",
  "fincaId": "550e8400-e29b-41d4-a716-446655440000",
  "fincaCode": "FINCA01",
  "fincaName": "Finca La Esperanza",
  "grupoId": "660e8400-e29b-41d4-a716-446655440001",
  "grupoNombre": "Grupo A",
  "cargoId": "770e8400-e29b-41d4-a716-446655440002",
  "cargoName": "Jornalero",
  "activo": true,
  "tieneDeuda": true,
  "montoDeuda": 250.00
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Trabajador no encontrado |
| 401 | No autenticado |

---

### PUT /api/trabajador/{id}
**Actualizar trabajador**

#### Request
```json
{
  "nombre": "Juan Pérez García López",
  "cuenta": "9876543210123456",
  "fincaId": "550e8400-e29b-41d4-a716-446655440000",
  "grupoId": "660e8400-e29b-41d4-a716-446655440001",
  "cargoId": "770e8400-e29b-41d4-a716-446655440002"
}
```

**Nota:** El RUC no se puede modificar (RN-09).

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Trabajador no encontrado |
| 400 | Nombre vacío |
| 400 | Finca no encontrada |
| 400 | Grupo no encontrado |
| 400 | Cargo no encontrado |
| 401 | No autenticado |

---

### DELETE /api/trabajador/{id}
**Desactivar trabajador (soft delete)**

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000",
  "advertencias": [
    "El trabajador tiene deuda pendiente de $250.00",
    "El trabajador tiene 5 producciones registradas"
  ]
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Trabajador no encontrado |
| 400 | Trabajador ya inactivo |
| 401 | No autenticado |

---

### POST /api/trabajador/{id}/reactivar
**Reactivar trabajador**

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Trabajador no encontrado |
| 400 | Trabajador ya está activo |
| 401 | No autenticado |

---

### POST /api/trabajador/{id}/transferir
**Transferir trabajador a otra finca**

#### Request
```json
{
  "nuevaFincaId": "550e8400-e29b-41d4-a716-446655440099"
}
```

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000",
  "fincaAnterior": "Finca La Esperanza",
  "fincaNueva": "Finca El Progreso"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Trabajador no encontrado |
| 400 | Nueva finca no encontrada |
| 400 | Trabajador ya pertenece a esa finca |
| 401 | No autenticado |

---

### POST /api/trabajador/search
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
  "query": "Juan"
}
```

#### Response 200
```json
{
  "content": [
    {
      "id": "880e8400-e29b-41d4-a716-446655440000",
      "ruc": "12345678901",
      "nombre": "Juan Pérez García",
      "fincaCode": "FINCA01",
      "fincaName": "Finca La Esperanza",
      "grupoNombre": "Grupo A",
      "cargoName": "Jornalero",
      "activo": true,
      "tieneDeuda": true,
      "montoDeuda": 250.00
    }
  ],
  "totalElements": 45,
  "totalPages": 3,
  "page": 0,
  "pageSize": 20
}
```

---

### GET /api/trabajador/por-finca/{fincaId}
**Obtener trabajadores de una finca**

#### Response 200
```json
{
  "content": [...],
  "totalElements": 25
}
```

---

### GET /api/trabajador/por-grupo/{grupoId}
**Obtener trabajadores de un grupo**

#### Response 200
```json
{
  "content": [...],
  "totalElements": 8
}
```

---

### GET /api/trabajador/con-deuda
**Obtener trabajadores con deuda pendiente**

#### Request
```
?fincaId=550e8400-e29b-41d4-a716-446655440000
```

#### Response 200
```json
{
  "content": [
    {
      "id": "880e8400-e29b-41d4-a716-446655440000",
      "ruc": "12345678901",
      "nombre": "Juan Pérez García",
      "montoDeuda": 250.00
    }
  ],
  "totalElements": 10,
  "totalDeuda": 3500.00
}
```

---

### POST /api/trabajador/import
**Importar trabajadores desde Excel**

#### Request
- Content-Type: `multipart/form-data`
- Body: archivo Excel (.xlsx)

#### Formato Excel esperado
| RUC | Nombre | Cuenta | FincaCode | GrupoNombre | CargoName |
|-----|--------|--------|-----------|-------------|-----------|
| 12345678901 | Juan Pérez | 123456789 | FINCA01 | Grupo A | Jornalero |

#### Response 200
```json
{
  "totalProcesados": 50,
  "totalImportados": 48,
  "totalErrores": 2,
  "errores": [
    "Fila 15: RUC 12345678901 ya existe",
    "Fila 23: Finca FINCA99 no encontrada"
  ]
}
```

---

## Criterios de Aceptación

### Escenario: Crear trabajador exitoso
```gherkin
Given un usuario autenticado
And existe Finca "FINCA01"
And existe Grupo "Grupo A"
And existe Cargo "Jornalero"
And no existe trabajador con RUC "12345678901"
When crea trabajador con los datos válidos
Then responde 200
And se crea el trabajador con activo = true
```

### Escenario: Rechazar RUC duplicado
```gherkin
Given un usuario autenticado
And existe trabajador con RUC "12345678901"
When intenta crear otro trabajador con el mismo RUC
Then responde 400
And mensaje indica "RUC ya existe"
```

### Escenario: Rechazar RUC inválido
```gherkin
Given un usuario autenticado
When intenta crear trabajador con RUC "1234ABC"
Then responde 400
And mensaje indica "RUC debe tener 11 dígitos numéricos"
```

### Escenario: Eliminar con advertencia
```gherkin
Given un usuario autenticado
And existe trabajador con deuda = 250.00
When elimina el trabajador
Then responde 200 con advertencias
And el trabajador queda con activo = false
And la advertencia indica "tiene deuda pendiente"
```

### Escenario: Transferir a otra finca
```gherkin
Given un usuario autenticado
And existe trabajador en Finca "FINCA01"
And existe Finca "FINCA02"
When transfiere el trabajador a "FINCA02"
Then responde 200
And el trabajador ahora pertenece a "FINCA02"
```

### Escenario: Reactivar trabajador
```gherkin
Given un usuario autenticado
And existe trabajador inactivo
When reactiva el trabajador
Then responde 200
And el trabajador queda con activo = true
```

### Escenario: Importar desde Excel
```gherkin
Given un usuario autenticado
And existe archivo Excel con 50 trabajadores
And 48 tienen datos válidos
And 2 tienen errores (RUC duplicado, finca inexistente)
When importa el archivo
Then responde 200
And totalImportados = 48
And totalErrores = 2
And se listan los errores específicos
```

---

## Arquitectura

### Capas a modificar

```
controller/
  └── TrabajadorController.java

applications/
  ├── command/trabajador/
  │   ├── create/
  │   │   ├── CreateTrabajadorCommand.java
  │   │   ├── CreateTrabajadorCommandHandler.java
  │   │   └── CreateTrabajadorRequest.java
  │   ├── update/
  │   │   ├── UpdateTrabajadorCommand.java
  │   │   ├── UpdateTrabajadorCommandHandler.java
  │   │   └── UpdateTrabajadorRequest.java
  │   ├── delete/
  │   │   ├── DeleteTrabajadorCommand.java
  │   │   └── DeleteTrabajadorCommandHandler.java
  │   ├── reactivar/
  │   │   ├── ReactivarTrabajadorCommand.java
  │   │   └── ReactivarTrabajadorCommandHandler.java
  │   ├── transferir/
  │   │   ├── TransferirTrabajadorCommand.java
  │   │   ├── TransferirTrabajadorCommandHandler.java
  │   │   └── TransferirTrabajadorRequest.java
  │   └── import/
  │       ├── ImportTrabajadorCommand.java
  │       └── ImportTrabajadorCommandHandler.java
  └── query/trabajador/
      ├── getById/
      ├── search/
      ├── porFinca/
      ├── porGrupo/
      └── conDeuda/

domain/
  ├── dto/
  │   └── TrabajadorDto.java (agregar fincaId)
  └── services/
      └── ITrabajadorService.java

infrastructure/
  ├── entity/
  │   └── Trabajador.java (agregar fincaId)
  ├── repository/
  │   ├── command/
  │   │   └── TrabajadorWriteDataJPARepository.java
  │   └── query/
  │       └── TrabajadorReadDataJPARepository.java
  └── services/
      └── TrabajadorServiceImpl.java
```

---

## Tests Requeridos

### Unit Tests
- [ ] CreateTrabajadorCommandHandler valida formato RUC (11 dígitos)
- [ ] CreateTrabajadorCommandHandler valida RUC único
- [ ] CreateTrabajadorCommandHandler valida finca existe
- [ ] CreateTrabajadorCommandHandler valida grupo existe
- [ ] CreateTrabajadorCommandHandler valida cargo existe
- [ ] UpdateTrabajadorCommandHandler no permite cambiar RUC
- [ ] DeleteTrabajadorCommandHandler genera advertencias
- [ ] TransferirTrabajadorCommandHandler valida nueva finca
- [ ] ImportTrabajadorCommandHandler procesa Excel correctamente

### Integration Tests
- [ ] POST /api/trabajador crea correctamente
- [ ] POST /api/trabajador rechaza RUC duplicado
- [ ] POST /api/trabajador rechaza RUC inválido
- [ ] PUT /api/trabajador no permite cambiar RUC
- [ ] DELETE /api/trabajador retorna advertencias
- [ ] POST /api/trabajador/{id}/reactivar funciona
- [ ] POST /api/trabajador/{id}/transferir funciona
- [ ] POST /api/trabajador/import procesa Excel
- [ ] GET /api/trabajador/con-deuda retorna correctamente

---

## Cambios Pendientes

Para alinear el código actual con esta spec:

1. **Agregar campo fincaId** - En entidad y DTO
2. **Validar formato RUC** - 11 dígitos numéricos
3. **Validar RUC único** - Al crear
4. **Bloquear edición de RUC** - En update
5. **Implementar endpoint /reactivar**
6. **Implementar endpoint /transferir**
7. **Agregar advertencias al eliminar** - Verificar deudas y referencias
8. **Implementar endpoint /con-deuda**
9. **Implementar importación Excel**
10. **Agregar campos derivados** - tieneDeuda, montoDeuda en response
11. **Actualizar frontend**

---

## Out of Scope

- Historial de transferencias entre fincas
- Foto del trabajador
- Documentos adjuntos
- Firma digital
- Control de asistencia
- Vacaciones y permisos
- Evaluación de desempeño
