---
feature: finca-producto
version: 1.0.0
status: implemented
priority: high
depends_on: [finca, producto]
---

# Feature: Gestión de Finca-Producto (Inventario por Finca)

## Contexto

FincaProducto representa la relación muchos-a-muchos entre Finca y Producto. Cada combinación tiene su propio stock independiente, permitiendo controlar el inventario de productos por finca.

El sistema gestiona:
- **Asignación** de productos a fincas
- **Control de stock** por cada relación
- **Entradas** de stock por diferentes fuentes
- **Salidas** de stock (consumidas por el módulo Salida)
- **Alertas** de stock bajo

## Actores

- Usuario autenticado (cualquier rol puede gestionar inventario)

## Dominio

### Entidad: FincaProducto

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| id | UUID | auto | Identificador único |
| fincaId | UUID | sí | Referencia a la finca |
| productoId | UUID | sí | Referencia al producto |
| stock | Integer | sí | Stock actual (>= 0) |
| stockMinimo | Integer | no | Stock mínimo para alertas (default: 0) |
| activo | Boolean | auto | Estado (true = activo, false = removido) |

### Campos calculados/derivados

| Campo | Descripción |
|-------|-------------|
| fincaCode | Código de la finca |
| fincaName | Nombre de la finca |
| productoCode | Código del producto |
| productoName | Nombre del producto |
| productoPrice | Precio del producto |
| productoTipo | Tipo del producto |
| alertaStockBajo | Boolean: stock <= stockMinimo |

### Enum: TipoMovimientoStock

| Valor | Descripción | Efecto |
|-------|-------------|--------|
| STOCK_INICIAL | Asignación inicial de producto | + |
| ENTRADA_PRODUCCION | Entrada desde producción terminada | + |
| ENTRADA_FACTURA | Entrada con factura de compra | + |
| ENTRADA_CONDUCE | Entrada por conduce (sin referencia) | + |
| AJUSTE_MANUAL | Ajuste de inventario (+/-) | +/- |
| SALIDA_VENTA | Salida por venta/vale | - |
| DEVOLUCION | Devolución por anulación de salida | + |
| AJUSTE_EDICION | Ajuste por edición de salida | +/- |

---

## Reglas de Negocio

### RN-01: Relación única
Un producto solo puede estar asignado una vez a cada finca. Si ya existe la relación (activa o inactiva), se debe reactivar en lugar de crear duplicado.

### RN-02: Stock no negativo
El stock no puede ser negativo. Cualquier operación que resulte en stock < 0 debe ser rechazada.

### RN-03: Stock inicial puede ser cero
Al asignar un producto a una finca, el stock inicial puede ser 0.

### RN-04: Remover solo con stock cero
Solo se puede remover (desactivar) un producto de una finca si el stock actual es 0.

### RN-05: Reactivación permitida
Si un producto fue removido de una finca, se puede reactivar asignándolo nuevamente.

### RN-06: Factura obligatoria para entrada por factura
Las entradas tipo ENTRADA_FACTURA requieren número de factura obligatorio.

### RN-07: Alerta de stock bajo
Se muestra indicador visual cuando `stock <= stockMinimo` y `stockMinimo > 0`.

### RN-08: Auditoría de movimientos
Todos los movimientos de stock se registran en MovimientoStock con fecha, cantidad, stock anterior/nuevo y referencia.

### RN-09: Observaciones en entradas
Las entradas manuales (factura, conduce, ajuste) deben permitir observaciones para documentar el movimiento.

---

## API Contract

### Base URL
```
/api/finca-producto
```

### Autenticación
Todos los endpoints requieren usuario autenticado (Bearer Token JWT).

---

### POST /api/finca-producto
**Asignar producto a finca**

#### Request
```json
{
  "fincaId": "550e8400-e29b-41d4-a716-446655440000",
  "productoId": "660e8400-e29b-41d4-a716-446655440001",
  "stock": 100,
  "stockMinimo": 10
}
```

#### Response 200
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "mensaje": "Producto asignado correctamente"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Finca no encontrada |
| 400 | Producto no encontrado |
| 400 | Producto ya asignado a esta finca (RN-01) |
| 400 | Stock < 0 |
| 401 | No autenticado |

---

### GET /api/finca-producto/{id}
**Obtener relación por ID**

#### Response 200
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "fincaId": "550e8400-e29b-41d4-a716-446655440000",
  "fincaCode": "FINCA01",
  "fincaName": "Finca La Esperanza",
  "productoId": "660e8400-e29b-41d4-a716-446655440001",
  "productoCode": "CAFE001",
  "productoName": "Café Arábica",
  "productoPrice": 15.00,
  "productoTipo": "VENTA",
  "stock": 100,
  "stockMinimo": 10,
  "alertaStockBajo": false,
  "activo": true
}
```

---

### POST /api/finca-producto/search
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
      "fincaId": "550e8400-e29b-41d4-a716-446655440000",
      "fincaCode": "FINCA01",
      "fincaName": "Finca La Esperanza",
      "productoId": "660e8400-e29b-41d4-a716-446655440001",
      "productoCode": "CAFE001",
      "productoName": "Café Arábica",
      "productoPrice": 15.00,
      "productoTipo": "VENTA",
      "stock": 100,
      "stockMinimo": 10,
      "alertaStockBajo": false
    }
  ],
  "totalElements": 15,
  "totalPages": 1,
  "page": 0,
  "pageSize": 20
}
```

---

### PUT /api/finca-producto/{id}
**Actualizar configuración (stockMinimo)**

#### Request
```json
{
  "stockMinimo": 20
}
```

#### Response 200
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000"
}
```

---

### DELETE /api/finca-producto/{id}
**Remover producto de finca (soft delete)**

#### Response 200
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "mensaje": "Producto removido de la finca"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Stock > 0 (RN-04) |
| 404 | Relación no encontrada |
| 401 | No autenticado |

---

### POST /api/finca-producto/{id}/entrada-produccion
**Registrar entrada de producción**

#### Request
```json
{
  "cantidad": 50,
  "descripcion": "Producción del día 20/08/2026"
}
```

#### Response 200
```json
{
  "stockAnterior": 100,
  "stockNuevo": 150,
  "cantidad": 50
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Cantidad <= 0 |
| 404 | Relación no encontrada |
| 401 | No autenticado |

---

### POST /api/finca-producto/{id}/entrada-factura
**Registrar entrada por factura**

#### Request
```json
{
  "cantidad": 100,
  "numeroFactura": "FAC-2026-00123",
  "observaciones": "Compra a proveedor XYZ"
}
```

#### Response 200
```json
{
  "stockAnterior": 150,
  "stockNuevo": 250,
  "cantidad": 100
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Cantidad <= 0 |
| 400 | Número de factura vacío (RN-06) |
| 404 | Relación no encontrada |
| 401 | No autenticado |

---

### POST /api/finca-producto/{id}/entrada-conduce
**Registrar entrada por conduce**

#### Request
```json
{
  "cantidad": 30,
  "observaciones": "Recepción sin documentación formal"
}
```

#### Response 200
```json
{
  "stockAnterior": 250,
  "stockNuevo": 280,
  "cantidad": 30
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Cantidad <= 0 |
| 404 | Relación no encontrada |
| 401 | No autenticado |

---

### POST /api/finca-producto/{id}/ajuste
**Registrar ajuste manual de stock**

#### Request
```json
{
  "cantidad": -5,
  "observaciones": "Ajuste por inventario físico - diferencia detectada"
}
```

#### Response 200
```json
{
  "stockAnterior": 280,
  "stockNuevo": 275,
  "cantidad": -5
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Cantidad = 0 |
| 400 | Ajuste dejaría stock < 0 (RN-02) |
| 400 | Observaciones vacías |
| 404 | Relación no encontrada |
| 401 | No autenticado |

---

### GET /api/finca-producto/{id}/movimientos
**Obtener historial de movimientos**

#### Request
```
?page=0&size=20
```

#### Response 200
```json
{
  "content": [
    {
      "id": "880e8400-e29b-41d4-a716-446655440001",
      "tipo": "ENTRADA_PRODUCCION",
      "cantidad": 50,
      "stockAnterior": 100,
      "stockNuevo": 150,
      "fecha": "2026-08-20T10:30:00",
      "descripcion": "Producción del día 20/08/2026",
      "referenciaId": null,
      "referenciaTabla": null
    },
    {
      "id": "880e8400-e29b-41d4-a716-446655440002",
      "tipo": "SALIDA_VENTA",
      "cantidad": -20,
      "stockAnterior": 150,
      "stockNuevo": 130,
      "fecha": "2026-08-20T14:00:00",
      "descripcion": "Salida VALE-0001",
      "referenciaId": "990e8400-e29b-41d4-a716-446655440000",
      "referenciaTabla": "salida"
    }
  ],
  "totalElements": 25,
  "totalPages": 2,
  "page": 0,
  "pageSize": 20
}
```

---

### GET /api/finca-producto/alertas
**Obtener productos con alerta de stock bajo**

#### Response 200
```json
{
  "content": [
    {
      "id": "770e8400-e29b-41d4-a716-446655440000",
      "fincaCode": "FINCA01",
      "fincaName": "Finca La Esperanza",
      "productoCode": "CAFE001",
      "productoName": "Café Arábica",
      "stock": 5,
      "stockMinimo": 10
    }
  ],
  "totalElements": 3
}
```

---

## Criterios de Aceptación

### Escenario: Asignar producto a finca
```gherkin
Given un usuario autenticado
And existe Finca "FINCA01"
And existe Producto "CAFE001"
And el producto no está asignado a la finca
When asigna el producto a la finca con stock = 100
Then responde 200
And se crea la relación FincaProducto
And se registra movimiento STOCK_INICIAL
```

### Escenario: Reactivar producto removido
```gherkin
Given un usuario autenticado
And existe relación FincaProducto inactiva
When asigna el mismo producto a la misma finca
Then la relación se reactiva (activo = true)
And se actualiza el stock
```

### Escenario: Rechazar asignación duplicada activa
```gherkin
Given un usuario autenticado
And existe relación FincaProducto activa
When intenta asignar el mismo producto a la misma finca
Then responde 400
And mensaje indica "Producto ya asignado a esta finca"
```

### Escenario: Remover producto con stock cero
```gherkin
Given un usuario autenticado
And existe relación FincaProducto con stock = 0
When remueve el producto de la finca
Then responde 200
And la relación queda con activo = false
```

### Escenario: Rechazar remover con stock positivo
```gherkin
Given un usuario autenticado
And existe relación FincaProducto con stock = 50
When intenta remover el producto de la finca
Then responde 400
And mensaje indica "No se puede remover: stock > 0"
```

### Escenario: Entrada por factura sin número
```gherkin
Given un usuario autenticado
And existe relación FincaProducto
When registra entrada factura sin numeroFactura
Then responde 400
And mensaje indica "Número de factura requerido"
```

### Escenario: Ajuste que deja stock negativo
```gherkin
Given un usuario autenticado
And existe relación FincaProducto con stock = 10
When registra ajuste con cantidad = -15
Then responde 400
And mensaje indica "El ajuste dejaría stock negativo"
```

### Escenario: Alerta de stock bajo
```gherkin
Given existe relación FincaProducto con stock = 5 y stockMinimo = 10
When se consulta la relación
Then alertaStockBajo = true
And aparece en el endpoint /alertas
```

---

## Arquitectura

### Capas a modificar

```
controller/
  └── FincaProductoController.java

applications/
  ├── command/fincaProducto/
  │   ├── asignar/
  │   │   ├── AsignarProductoCommand.java
  │   │   ├── AsignarProductoCommandHandler.java
  │   │   └── AsignarProductoRequest.java
  │   ├── remover/
  │   │   ├── RemoverProductoCommand.java
  │   │   └── RemoverProductoCommandHandler.java
  │   ├── entradaProduccion/
  │   │   └── ... (existente)
  │   ├── entradaFactura/
  │   │   ├── EntradaFacturaCommand.java
  │   │   ├── EntradaFacturaCommandHandler.java
  │   │   └── EntradaFacturaRequest.java
  │   ├── entradaConduce/
  │   │   ├── EntradaConduceCommand.java
  │   │   ├── EntradaConduceCommandHandler.java
  │   │   └── EntradaConduceRequest.java
  │   └── ajuste/
  │       ├── AjusteStockCommand.java
  │       ├── AjusteStockCommandHandler.java
  │       └── AjusteStockRequest.java
  └── query/fincaProducto/
      ├── getById/
      ├── search/
      ├── movimientos/
      │   └── GetMovimientosByFincaProductoQueryHandler.java
      └── alertas/
          └── GetAlertasStockBajoQueryHandler.java

domain/
  ├── dto/
  │   ├── FincaProductoDto.java (agregar stockMinimo)
  │   └── TipoMovimientoStock.java (agregar nuevos tipos)
  └── services/
      └── IFincaProductoService.java

infrastructure/
  ├── entity/
  │   └── FincaProducto.java (agregar stockMinimo)
  ├── repository/
  │   └── query/
  │       └── FincaProductoReadDataJPARepository.java
  └── services/
      └── FincaProductoServiceImpl.java
```

---

## Tests Requeridos

### Unit Tests
- [x] AsignarProductoCommandHandler valida finca existe
- [x] AsignarProductoCommandHandler valida producto existe
- [x] AsignarProductoCommandHandler rechaza duplicado activo (RN-01)
- [x] AsignarProductoCommandHandler reactiva relación inactiva (RN-05)
- [x] RemoverProductoCommandHandler valida stock = 0 (RN-04)
- [x] EntradaFacturaCommandHandler valida número factura (RN-06)
- [x] AjusteStockCommandHandler valida no dejar stock negativo (RN-02)

### Integration Tests
- [x] POST /api/finca-producto crea relación correctamente
- [x] POST /api/finca-producto reactiva relación inactiva
- [x] DELETE /api/finca-producto rechaza si stock > 0
- [x] POST /api/finca-producto/{id}/entrada-factura requiere número
- [x] POST /api/finca-producto/{id}/ajuste rechaza stock negativo
- [x] GET /api/finca-producto/alertas retorna productos con stock bajo
- [ ] GET /api/finca-producto/{id}/movimientos retorna historial

### Test Files Created

**Unit Tests:**
- `src/test/java/com/kynsoft/report/infrastructure/services/FincaProductoServiceImplTest.java`
  - Tests para asignarProductoAFinca(): finca no existe, producto no existe, ya asignado (RN-01), reactivar inactivo (RN-05), crear nuevo
  - Tests para removerProductoDeFinca(): no encontrada, ya removido, stock > 0 (RN-04), remover correctamente
  - Tests para entradaProduccion(): cantidad <= 0, producto no asignado, entrada correcta
  - Tests para entradaFactura(): factura vacía (RN-06), factura null, cantidad <= 0, entrada correcta
  - Tests para ajusteManual(): observaciones vacías, observaciones null, cantidad cero, stock negativo (RN-02), ajuste positivo, ajuste negativo
  - Tests para decrementarStock(): cantidad <= 0, stock insuficiente, decremento correcto
  - Tests para obtenerStock(): retornar stock y retornar 0 si no existe

**Integration Tests:**
- `src/test/java/com/kynsoft/report/controller/FincaProductoControllerIntegrationTest.java`
  - Tests para POST /asignar: asignar producto
  - Tests para GET /{id}: obtener por ID
  - Tests para POST /{id}/entrada-factura: entrada por factura
  - Tests para POST /{id}/ajuste: ajuste positivo y negativo
  - Tests para GET /alertas: alertas de stock bajo

---

## Cambios Pendientes

Para alinear el código actual con esta spec:

1. **Agregar campo stockMinimo** - En entidad y DTO
2. **Agregar campo alertaStockBajo** - Calculado en response
3. **Agregar TipoMovimientoStock** - ENTRADA_FACTURA, ENTRADA_CONDUCE
4. **Implementar endpoint /entrada-factura** - Con validación RN-06
5. **Implementar endpoint /entrada-conduce** - Sin validación de referencia
6. **Implementar endpoint /ajuste** - Con validación de observaciones
7. **Implementar endpoint /alertas** - Productos con stock bajo
8. **Validar stock = 0 al remover** - RN-04
9. **Implementar reactivación** - RN-05
10. **Actualizar frontend** - Formularios y alertas visuales

---

## Out of Scope

- Soporte para proveedores (futuro)
- Notificaciones push de alertas
- Transferencias entre fincas
- Múltiples ubicaciones/lotes por producto
- Códigos de barras / QR
- Inventario físico masivo
