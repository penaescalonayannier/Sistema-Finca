---
feature: almacen
version: 1.0.0
status: partial
priority: high
depends_on: [finca, producto, finca-producto]
note: Implemented essential fields and endpoints. AlmacenProducto migration pending.
---

# Feature: Gestión de Almacenes

## Contexto

Almacén permite organizar los productos dentro de una finca en ubicaciones físicas o lógicas. Cada almacén mantiene su propio inventario de productos, y el stock total de FincaProducto es la suma automática de todos los almacenes.

Las entradas y salidas de productos deben especificar el almacén de origen/destino, permitiendo un control granular del inventario.

## Actores

- Usuario autenticado (cualquier rol puede gestionar almacenes)

## Dominio

### Entidad: Almacen

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| id | UUID | auto | Identificador único |
| inventario | String(20) | auto | Código de inventario (INV-XXXXXX) |
| nombre | String(100) | sí | Nombre del almacén |
| descripcion | String(500) | no | Descripción opcional |
| fincaId | UUID | sí | Finca a la que pertenece |
| esPrincipal | Boolean | auto | Si es el almacén principal de la finca |
| activo | Boolean | auto | Estado (true = activo, false = inactivo) |

### Entidad: AlmacenProducto (Stock por Almacén)

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| id | UUID | auto | Identificador único |
| almacenId | UUID | sí | Referencia al almacén |
| fincaProductoId | UUID | sí | Referencia a FincaProducto |
| stock | Integer | auto | Cantidad en este almacén (>= 0) |

### Campos derivados (response)

| Campo | Descripción |
|-------|-------------|
| fincaCode | Código de la finca |
| fincaName | Nombre de la finca |
| cantidadProductos | Número de productos con stock > 0 |
| stockTotal | Suma del stock de todos los productos |

---

## Reglas de Negocio

### RN-01: Inventario único y auto-generado
El código de inventario se genera automáticamente con formato INV-XXXXXX (la cantidad de X es variable, secuencial por finca).

### RN-02: Nombre único por finca
El nombre del almacén debe ser único dentro de la misma finca.

### RN-03: Mínimo un almacén por finca
Cada finca debe tener al menos un almacén. Al crear una finca, se crea automáticamente un almacén principal.

### RN-04: Almacén principal único
Solo puede existir un almacén marcado como principal (esPrincipal = true) por finca.

### RN-05: Stock por almacén
Cada almacén mantiene su propio stock de productos. Un mismo producto puede estar en múltiples almacenes con cantidades diferentes.

### RN-06: Stock total automático
El stock en FincaProducto es la suma automática del stock de todos los almacenes para ese producto.

### RN-07: Entradas/salidas especifican almacén
Todas las operaciones de entrada (producción, ajuste) y salida (ventas, transferencias) deben especificar el almacén afectado.

### RN-08: Transferencias entre almacenes
Se permite transferir stock de un producto entre almacenes de la misma finca.

### RN-09: No eliminar con stock
Un almacén no puede eliminarse si tiene productos con stock > 0. Debe transferir todo el stock a otro almacén primero.

### RN-10: No eliminar almacén principal
El almacén principal no puede eliminarse. Primero debe designarse otro almacén como principal.

### RN-11: Soft delete
La eliminación es lógica (activo = false), manteniendo el registro para auditoría.

### RN-12: Reactivación permitida
Un almacén inactivo puede ser reactivado cambiando activo = true.

---

## API Contract

### Base URL
```
/api/almacen
```

### Autenticación
Todos los endpoints requieren usuario autenticado (Bearer Token JWT).

---

### POST /api/almacen
**Crear almacén**

#### Request
```json
{
  "nombre": "Almacén Norte",
  "descripcion": "Almacén de productos terminados zona norte",
  "fincaId": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000",
  "inventario": "INV-000001"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Nombre vacío |
| 400 | Nombre ya existe en la finca (RN-02) |
| 400 | Finca no encontrada |
| 401 | No autenticado |

---

### GET /api/almacen/{id}
**Obtener almacén por ID**

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000",
  "inventario": "INV-000001",
  "nombre": "Almacén Norte",
  "descripcion": "Almacén de productos terminados zona norte",
  "fincaId": "550e8400-e29b-41d4-a716-446655440000",
  "fincaCode": "FINCA01",
  "fincaName": "Finca La Esperanza",
  "esPrincipal": false,
  "activo": true,
  "cantidadProductos": 15,
  "stockTotal": 5000
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Almacén no encontrado |
| 401 | No autenticado |

---

### PUT /api/almacen/{id}
**Actualizar almacén**

#### Request
```json
{
  "nombre": "Almacén Norte - Actualizado",
  "descripcion": "Nueva descripción"
}
```

**Nota:** El código de inventario y fincaId no se pueden modificar.

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Almacén no encontrado |
| 400 | Nombre vacío |
| 400 | Nombre ya existe en la finca (RN-02) |
| 401 | No autenticado |

---

### DELETE /api/almacen/{id}
**Desactivar almacén (soft delete)**

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Almacén no encontrado |
| 400 | Almacén ya inactivo |
| 400 | Almacén tiene productos con stock > 0 (RN-09) |
| 400 | Es almacén principal (RN-10) |
| 401 | No autenticado |

---

### POST /api/almacen/{id}/reactivar
**Reactivar almacén**

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Almacén no encontrado |
| 400 | Almacén ya está activo |
| 401 | No autenticado |

---

### POST /api/almacen/{id}/establecer-principal
**Establecer como almacén principal**

#### Response 200
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000",
  "almacenAnterior": "Almacén Central"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Almacén no encontrado |
| 400 | Almacén ya es principal |
| 400 | Almacén inactivo |
| 401 | No autenticado |

---

### POST /api/almacen/transferir
**Transferir stock entre almacenes**

#### Request
```json
{
  "almacenOrigenId": "880e8400-e29b-41d4-a716-446655440001",
  "almacenDestinoId": "880e8400-e29b-41d4-a716-446655440002",
  "fincaProductoId": "990e8400-e29b-41d4-a716-446655440003",
  "cantidad": 50,
  "observaciones": "Reubicación de inventario"
}
```

#### Response 200
```json
{
  "id": "aa0e8400-e29b-41d4-a716-446655440000",
  "stockOrigenAnterior": 100,
  "stockOrigenNuevo": 50,
  "stockDestinoAnterior": 30,
  "stockDestinoNuevo": 80
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Almacén origen no encontrado |
| 400 | Almacén destino no encontrado |
| 400 | Almacenes son el mismo |
| 400 | Almacenes no son de la misma finca |
| 400 | Producto no encontrado en almacén origen |
| 400 | Stock insuficiente en origen |
| 400 | Cantidad <= 0 |
| 401 | No autenticado |

---

### POST /api/almacen/search
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
  "query": "Norte"
}
```

#### Response 200
```json
{
  "content": [
    {
      "id": "880e8400-e29b-41d4-a716-446655440000",
      "inventario": "INV-000001",
      "nombre": "Almacén Norte",
      "fincaCode": "FINCA01",
      "fincaName": "Finca La Esperanza",
      "esPrincipal": false,
      "activo": true,
      "cantidadProductos": 15,
      "stockTotal": 5000
    }
  ],
  "totalElements": 5,
  "totalPages": 1,
  "page": 0,
  "pageSize": 20
}
```

---

### GET /api/almacen/por-finca/{fincaId}
**Obtener almacenes de una finca**

#### Response 200
```json
{
  "content": [
    {
      "id": "880e8400-e29b-41d4-a716-446655440000",
      "inventario": "INV-000001",
      "nombre": "Almacén Central",
      "esPrincipal": true,
      "cantidadProductos": 20,
      "stockTotal": 8000
    },
    {
      "id": "880e8400-e29b-41d4-a716-446655440001",
      "inventario": "INV-000002",
      "nombre": "Almacén Norte",
      "esPrincipal": false,
      "cantidadProductos": 15,
      "stockTotal": 5000
    }
  ],
  "totalElements": 2
}
```

---

### GET /api/almacen/{id}/productos
**Obtener productos de un almacén**

#### Response 200
```json
{
  "content": [
    {
      "fincaProductoId": "990e8400-e29b-41d4-a716-446655440000",
      "productoId": "660e8400-e29b-41d4-a716-446655440001",
      "productoCode": "CAFE001",
      "productoName": "Café Arábica",
      "stock": 500,
      "stockMinimo": 100,
      "bajoStock": false
    }
  ],
  "totalElements": 15,
  "stockTotal": 5000
}
```

---

### GET /api/almacen/{id}/movimientos
**Obtener historial de movimientos de un almacén**

#### Request
```
?fechaInicio=2026-08-01&fechaFin=2026-08-31
```

#### Response 200
```json
{
  "content": [
    {
      "id": "bb0e8400-e29b-41d4-a716-446655440000",
      "fecha": "2026-08-20T10:00:00",
      "tipoMovimiento": "ENTRADA_PRODUCCION",
      "productoName": "Café Arábica",
      "cantidad": 100,
      "stockAnterior": 400,
      "stockNuevo": 500,
      "referenciaId": "cc0e8400-e29b-41d4-a716-446655440000",
      "observaciones": "Producción matutina"
    }
  ],
  "totalElements": 50
}
```

---

## Criterios de Aceptación

### Escenario: Crear almacén exitoso
```gherkin
Given un usuario autenticado
And existe Finca "FINCA01"
When crea almacén con nombre "Almacén Norte"
Then responde 200
And se genera código INV-XXXXXX automáticamente
And se crea el almacén con activo = true
And esPrincipal = false
```

### Escenario: Crear almacén principal automático
```gherkin
Given un usuario autenticado
When crea una nueva Finca "FINCA01"
Then se crea automáticamente un almacén "Almacén Principal"
And el almacén tiene esPrincipal = true
```

### Escenario: Rechazar nombre duplicado
```gherkin
Given un usuario autenticado
And existe almacén "Almacén Norte" en Finca "FINCA01"
When intenta crear otro almacén con nombre "Almacén Norte" en la misma finca
Then responde 400
And mensaje indica "Nombre ya existe en esta finca"
```

### Escenario: Transferir stock entre almacenes
```gherkin
Given un usuario autenticado
And existe Almacén "Norte" con 100 unidades de "Café"
And existe Almacén "Sur" con 30 unidades de "Café"
When transfiere 50 unidades de "Norte" a "Sur"
Then responde 200
And Almacén "Norte" tiene 50 unidades
And Almacén "Sur" tiene 80 unidades
And FincaProducto.stock = 130 (sin cambio)
And se registra movimiento TRANSFERENCIA_SALIDA en "Norte"
And se registra movimiento TRANSFERENCIA_ENTRADA en "Sur"
```

### Escenario: No eliminar con stock
```gherkin
Given un usuario autenticado
And existe almacén con stockTotal = 500
When intenta eliminar el almacén
Then responde 400
And mensaje indica "Almacén tiene productos con stock. Transfiera a otro almacén primero"
```

### Escenario: No eliminar almacén principal
```gherkin
Given un usuario autenticado
And existe almacén principal sin stock
When intenta eliminar el almacén
Then responde 400
And mensaje indica "No se puede eliminar el almacén principal. Designe otro almacén como principal primero"
```

### Escenario: Cambiar almacén principal
```gherkin
Given un usuario autenticado
And existe almacén "Central" como principal
And existe almacén "Norte" activo
When establece "Norte" como principal
Then responde 200
And "Norte" tiene esPrincipal = true
And "Central" tiene esPrincipal = false
```

### Escenario: Stock automático en FincaProducto
```gherkin
Given producto "Café" en Finca "FINCA01"
And Almacén "Norte" tiene 100 unidades
And Almacén "Sur" tiene 50 unidades
When consulta FincaProducto
Then FincaProducto.stock = 150
```

---

## Arquitectura

### Capas a crear/modificar

```
controller/
  └── AlmacenController.java

applications/
  ├── command/almacen/
  │   ├── create/
  │   │   ├── CreateAlmacenCommand.java
  │   │   ├── CreateAlmacenCommandHandler.java
  │   │   └── CreateAlmacenRequest.java
  │   ├── update/
  │   │   ├── UpdateAlmacenCommand.java
  │   │   ├── UpdateAlmacenCommandHandler.java
  │   │   └── UpdateAlmacenRequest.java
  │   ├── delete/
  │   │   ├── DeleteAlmacenCommand.java
  │   │   └── DeleteAlmacenCommandHandler.java
  │   ├── reactivar/
  │   │   ├── ReactivarAlmacenCommand.java
  │   │   └── ReactivarAlmacenCommandHandler.java
  │   ├── establecerPrincipal/
  │   │   ├── EstablecerPrincipalCommand.java
  │   │   └── EstablecerPrincipalCommandHandler.java
  │   └── transferir/
  │       ├── TransferirStockCommand.java
  │       ├── TransferirStockCommandHandler.java
  │       └── TransferirStockRequest.java
  └── query/almacen/
      ├── getById/
      ├── search/
      ├── porFinca/
      ├── productos/
      └── movimientos/

domain/
  ├── dto/
  │   ├── AlmacenDto.java (modificar)
  │   └── AlmacenProductoDto.java (nuevo)
  └── services/
      ├── IAlmacenService.java
      └── IAlmacenProductoService.java (nuevo)

infrastructure/
  ├── entity/
  │   ├── Almacen.java (modificar)
  │   └── AlmacenProducto.java (nuevo)
  ├── repository/
  │   ├── command/
  │   │   ├── AlmacenWriteDataJPARepository.java
  │   │   └── AlmacenProductoWriteDataJPARepository.java (nuevo)
  │   └── query/
  │       ├── AlmacenReadDataJPARepository.java
  │       └── AlmacenProductoReadDataJPARepository.java (nuevo)
  └── services/
      ├── AlmacenServiceImpl.java (modificar)
      └── AlmacenProductoServiceImpl.java (nuevo)
```

### Cambios en otras entidades

**FincaProducto:**
- El campo `stock` pasa a ser calculado automáticamente como suma de AlmacenProducto.stock
- Agregar método `recalcularStock()` que suma stocks de todos los almacenes

**ProduccionTerminada:**
- Agregar campo `almacenId` para especificar dónde se almacena la producción

**Salida/ItemSalida:**
- Agregar campo `almacenId` para especificar de dónde sale el producto

---

## Tests Requeridos

### Unit Tests
- [ ] CreateAlmacenCommandHandler genera código INV-XXXXXX
- [ ] CreateAlmacenCommandHandler valida nombre único por finca
- [ ] UpdateAlmacenCommandHandler no permite cambiar inventario/finca
- [ ] DeleteAlmacenCommandHandler valida stock = 0
- [ ] DeleteAlmacenCommandHandler no permite eliminar principal
- [ ] TransferirStockCommandHandler valida stock suficiente
- [ ] TransferirStockCommandHandler actualiza ambos almacenes
- [ ] FincaProducto.stock se recalcula automáticamente

### Integration Tests
- [ ] POST /api/almacen genera código automáticamente
- [ ] POST /api/almacen rechaza nombre duplicado
- [ ] DELETE /api/almacen rechaza si tiene stock
- [ ] DELETE /api/almacen rechaza si es principal
- [ ] POST /api/almacen/transferir funciona correctamente
- [ ] POST /api/almacen/{id}/establecer-principal funciona
- [ ] GET /api/almacen/{id}/productos retorna stock correcto
- [ ] Al crear finca se crea almacén principal automáticamente

---

## Cambios Pendientes

Para alinear el código actual con esta spec:

1. **Crear entidad AlmacenProducto** - Nueva tabla junction con stock
2. **Agregar campo esPrincipal** - En Almacen
3. **Agregar campo descripcion** - En Almacen
4. **Generar código inventario automático** - INV-XXXXXX secuencial
5. **Validar nombre único por finca**
6. **Implementar stock por almacén** - Migrar de relación directa a AlmacenProducto
7. **Hacer FincaProducto.stock calculado** - Suma de AlmacenProducto
8. **Agregar almacenId a ProduccionTerminada**
9. **Agregar almacenId a ItemSalida**
10. **Crear almacén principal automáticamente** - Al crear finca
11. **Implementar endpoint /transferir**
12. **Implementar endpoint /establecer-principal**
13. **Implementar endpoints de consulta**
14. **Actualizar frontend**

---

## Out of Scope

- Ubicación física (coordenadas, plano)
- Capacidad máxima del almacén
- Temperatura/condiciones de almacenamiento
- Zonas dentro del almacén (estantes, pasillos)
- Códigos de barras/QR para ubicaciones
- Gestión de lotes/fechas de vencimiento
- Integración con lectores de inventario
