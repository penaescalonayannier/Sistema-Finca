---
feature: movimiento-stock
version: 1.0.0
status: implemented
priority: high
depends_on: [finca, producto, finca-producto, almacen]
---

# Feature: Gestión de Movimientos de Stock

## Contexto

MovimientoStock es el registro de auditoría de todas las operaciones que afectan el inventario. Cada entrada, salida, transferencia o ajuste genera automáticamente uno o más registros de movimiento.

Los movimientos son inmutables - no se pueden editar ni eliminar. Cualquier corrección requiere un nuevo movimiento contrario (ajuste).

## Actores

- Usuario autenticado (cualquier rol puede consultar y crear ajustes manuales)

## Dominio

### Entidad: MovimientoStock

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| id | UUID | auto | Identificador único |
| fecha | LocalDateTime | auto | Fecha/hora del movimiento |
| tipoMovimiento | Enum | sí | Tipo de movimiento |
| almacenId | UUID | sí | Almacén afectado |
| fincaProductoId | UUID | sí | Producto afectado |
| cantidad | Integer | sí | Cantidad movida (siempre > 0) |
| stockAnterior | Integer | auto | Stock antes del movimiento |
| stockNuevo | Integer | auto | Stock después del movimiento |
| referenciaId | UUID | condicional | UUID de la operación origen |
| referenciaTipo | Enum | condicional | Tipo de referencia |
| observaciones | String(500) | condicional | Notas (obligatorio para ajustes) |
| usuarioId | UUID | auto | Usuario que realizó la operación |

### Enum: TipoMovimiento

**Entradas:**
| Valor | Descripción |
|-------|-------------|
| ENTRADA_PRODUCCION | Ingreso desde producción terminada |
| ENTRADA_AJUSTE | Ajuste manual positivo |
| TRANSFERENCIA_ENTRADA | Recepción desde otro almacén |

**Salidas:**
| Valor | Descripción |
|-------|-------------|
| SALIDA_VENTA | Salida por venta |
| SALIDA_AUTOCONSUMO | Salida por autoconsumo |
| SALIDA_AJUSTE | Ajuste manual negativo |
| TRANSFERENCIA_SALIDA | Envío hacia otro almacén |

**Reversiones:**
| Valor | Descripción |
|-------|-------------|
| REVERSION_PRODUCCION | Anulación de producción |
| REVERSION_SALIDA | Anulación de salida |

### Enum: ReferenciaTipo

| Valor | Descripción |
|-------|-------------|
| PRODUCCION | Referencia a ProduccionTerminada |
| SALIDA | Referencia a Salida/ItemSalida |
| TRANSFERENCIA | Referencia a TransferenciaAlmacen |
| AJUSTE | Sin referencia externa (ajuste manual) |

### Campos derivados (response)

| Campo | Descripción |
|-------|-------------|
| almacenNombre | Nombre del almacén |
| almacenInventario | Código de inventario del almacén |
| fincaCode | Código de la finca |
| fincaName | Nombre de la finca |
| productoCode | Código del producto |
| productoName | Nombre del producto |
| usuarioNombre | Nombre del usuario |

---

## Reglas de Negocio

### RN-01: Generación automática
Los movimientos se generan automáticamente al ejecutar operaciones de:
- ProduccionTerminada (crear, editar, anular)
- Salida (crear, anular)
- Transferencia entre almacenes

### RN-02: Inmutabilidad
Los movimientos son inmutables. No se pueden editar ni eliminar. Cualquier corrección requiere un ajuste contrario.

### RN-03: Ajustes manuales
Se permiten ajustes manuales (ENTRADA_AJUSTE, SALIDA_AJUSTE) para correcciones de inventario.

### RN-04: Observaciones obligatorias en ajustes
Para movimientos tipo ENTRADA_AJUSTE y SALIDA_AJUSTE, el campo observaciones es obligatorio.

### RN-05: Cantidad positiva
La cantidad siempre es un valor positivo. El tipo de movimiento indica si es entrada o salida.

### RN-06: No stock negativo
Un ajuste de salida (SALIDA_AJUSTE) no puede dejar el stock en negativo.

### RN-07: Referencia obligatoria
Para movimientos automáticos, referenciaId y referenciaTipo son obligatorios. Para ajustes manuales, son null.

### RN-08: Stock calculado
stockAnterior y stockNuevo se calculan automáticamente al momento de crear el movimiento.

### RN-09: Usuario automático
usuarioId se captura automáticamente del contexto de seguridad (JWT).

### RN-10: Fecha automática
La fecha se asigna automáticamente al momento de crear el movimiento.

---

## API Contract

### Base URL
```
/api/movimiento-stock
```

### Autenticación
Todos los endpoints requieren usuario autenticado (Bearer Token JWT).

---

### POST /api/movimiento-stock/ajuste
**Crear ajuste manual**

#### Request
```json
{
  "almacenId": "880e8400-e29b-41d4-a716-446655440000",
  "fincaProductoId": "990e8400-e29b-41d4-a716-446655440000",
  "tipoMovimiento": "ENTRADA_AJUSTE",
  "cantidad": 50,
  "observaciones": "Ajuste por conteo físico - diferencia encontrada"
}
```

#### Response 200
```json
{
  "id": "aa0e8400-e29b-41d4-a716-446655440000",
  "stockAnterior": 100,
  "stockNuevo": 150
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Almacén no encontrado |
| 400 | Producto no encontrado |
| 400 | Tipo de movimiento inválido (solo ENTRADA_AJUSTE o SALIDA_AJUSTE) |
| 400 | Cantidad <= 0 |
| 400 | Observaciones vacías (RN-04) |
| 400 | Stock resultante negativo (RN-06) |
| 401 | No autenticado |

---

### GET /api/movimiento-stock/{id}
**Obtener movimiento por ID**

#### Response 200
```json
{
  "id": "aa0e8400-e29b-41d4-a716-446655440000",
  "fecha": "2026-08-20T10:30:00",
  "tipoMovimiento": "ENTRADA_PRODUCCION",
  "almacenId": "880e8400-e29b-41d4-a716-446655440000",
  "almacenNombre": "Almacén Norte",
  "almacenInventario": "INV-000001",
  "fincaProductoId": "990e8400-e29b-41d4-a716-446655440000",
  "fincaCode": "FINCA01",
  "fincaName": "Finca La Esperanza",
  "productoCode": "CAFE001",
  "productoName": "Café Arábica",
  "cantidad": 100,
  "stockAnterior": 50,
  "stockNuevo": 150,
  "referenciaId": "bb0e8400-e29b-41d4-a716-446655440000",
  "referenciaTipo": "PRODUCCION",
  "observaciones": "Producción matutina",
  "usuarioId": "cc0e8400-e29b-41d4-a716-446655440000",
  "usuarioNombre": "Juan Pérez"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Movimiento no encontrado |
| 401 | No autenticado |

---

### POST /api/movimiento-stock/search
**Búsqueda paginada**

#### Request
```json
{
  "page": 0,
  "pageSize": 20,
  "filter": [
    {
      "key": "almacenId",
      "operator": "EQUAL",
      "value": "880e8400-e29b-41d4-a716-446655440000"
    },
    {
      "key": "tipoMovimiento",
      "operator": "EQUAL",
      "value": "ENTRADA_PRODUCCION"
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
      "id": "aa0e8400-e29b-41d4-a716-446655440000",
      "fecha": "2026-08-20T10:30:00",
      "tipoMovimiento": "ENTRADA_PRODUCCION",
      "almacenNombre": "Almacén Norte",
      "productoCode": "CAFE001",
      "productoName": "Café Arábica",
      "cantidad": 100,
      "stockAnterior": 50,
      "stockNuevo": 150
    }
  ],
  "totalElements": 150,
  "totalPages": 8,
  "page": 0,
  "pageSize": 20
}
```

---

### GET /api/movimiento-stock/por-almacen/{almacenId}
**Obtener movimientos por almacén**

#### Request
```
?fechaInicio=2026-08-01&fechaFin=2026-08-31
```

#### Response 200
```json
{
  "content": [...],
  "totalElements": 50,
  "totalEntradas": 35,
  "totalSalidas": 15
}
```

---

### GET /api/movimiento-stock/por-producto/{fincaProductoId}
**Obtener movimientos por producto**

#### Request
```
?fechaInicio=2026-08-01&fechaFin=2026-08-31
```

#### Response 200
```json
{
  "content": [...],
  "totalElements": 30
}
```

---

### GET /api/movimiento-stock/por-tipo/{tipoMovimiento}
**Obtener movimientos por tipo**

#### Request
```
?fincaId=550e8400-e29b-41d4-a716-446655440000&fechaInicio=2026-08-01&fechaFin=2026-08-31
```

#### Response 200
```json
{
  "content": [...],
  "totalElements": 20,
  "totalCantidad": 5000
}
```

---

### GET /api/movimiento-stock/por-referencia/{referenciaId}
**Obtener movimientos por referencia**

#### Response 200
```json
{
  "content": [
    {
      "id": "aa0e8400-e29b-41d4-a716-446655440000",
      "tipoMovimiento": "ENTRADA_PRODUCCION",
      "cantidad": 100,
      "stockAnterior": 50,
      "stockNuevo": 150
    }
  ],
  "totalElements": 1
}
```

---

### GET /api/movimiento-stock/por-finca/{fincaId}
**Obtener movimientos de todos los almacenes de una finca**

#### Request
```
?fechaInicio=2026-08-01&fechaFin=2026-08-31
```

#### Response 200
```json
{
  "content": [...],
  "totalElements": 200
}
```

---

### GET /api/movimiento-stock/resumen
**Resumen de entradas/salidas por período**

#### Request
```
?fincaId=550e8400-e29b-41d4-a716-446655440000&fechaInicio=2026-08-01&fechaFin=2026-08-31
```

#### Response 200
```json
{
  "periodo": {
    "fechaInicio": "2026-08-01",
    "fechaFin": "2026-08-31"
  },
  "entradas": {
    "ENTRADA_PRODUCCION": 5000,
    "ENTRADA_AJUSTE": 200,
    "TRANSFERENCIA_ENTRADA": 300,
    "total": 5500
  },
  "salidas": {
    "SALIDA_VENTA": 3000,
    "SALIDA_AUTOCONSUMO": 500,
    "SALIDA_AJUSTE": 100,
    "TRANSFERENCIA_SALIDA": 300,
    "REVERSION_PRODUCCION": 50,
    "REVERSION_SALIDA": 0,
    "total": 3950
  },
  "balance": 1550,
  "totalMovimientos": 250
}
```

---

### GET /api/movimiento-stock/balance-producto
**Balance de movimientos por producto**

#### Request
```
?fincaId=550e8400-e29b-41d4-a716-446655440000&fechaInicio=2026-08-01&fechaFin=2026-08-31
```

#### Response 200
```json
{
  "content": [
    {
      "fincaProductoId": "990e8400-e29b-41d4-a716-446655440000",
      "productoCode": "CAFE001",
      "productoName": "Café Arábica",
      "stockInicial": 100,
      "totalEntradas": 500,
      "totalSalidas": 350,
      "stockFinal": 250,
      "cantidadMovimientos": 25
    }
  ],
  "totalElements": 15
}
```

---

### GET /api/movimiento-stock/kardex/{fincaProductoId}
**Kardex - Historial con saldo acumulado**

#### Request
```
?almacenId=880e8400-e29b-41d4-a716-446655440000&fechaInicio=2026-08-01&fechaFin=2026-08-31
```

#### Response 200
```json
{
  "producto": {
    "fincaProductoId": "990e8400-e29b-41d4-a716-446655440000",
    "productoCode": "CAFE001",
    "productoName": "Café Arábica"
  },
  "almacen": {
    "almacenId": "880e8400-e29b-41d4-a716-446655440000",
    "almacenNombre": "Almacén Norte"
  },
  "stockInicial": 100,
  "movimientos": [
    {
      "fecha": "2026-08-01T08:00:00",
      "tipoMovimiento": "ENTRADA_PRODUCCION",
      "entrada": 50,
      "salida": 0,
      "saldo": 150,
      "observaciones": "Producción matutina"
    },
    {
      "fecha": "2026-08-01T14:00:00",
      "tipoMovimiento": "SALIDA_VENTA",
      "entrada": 0,
      "salida": 30,
      "saldo": 120,
      "observaciones": "Venta cliente X"
    }
  ],
  "stockFinal": 250,
  "totalEntradas": 200,
  "totalSalidas": 50
}
```

---

## Criterios de Aceptación

### Escenario: Movimiento automático por producción
```gherkin
Given un usuario autenticado
And existe ProduccionTerminada con cantidad = 100
When se crea la producción
Then se genera MovimientoStock automáticamente
And tipoMovimiento = ENTRADA_PRODUCCION
And referenciaId = id de la producción
And referenciaTipo = PRODUCCION
And stockNuevo = stockAnterior + 100
```

### Escenario: Movimiento automático por salida
```gherkin
Given un usuario autenticado
And existe Salida tipo VENTA con cantidad = 50
When se crea la salida
Then se genera MovimientoStock automáticamente
And tipoMovimiento = SALIDA_VENTA
And stockNuevo = stockAnterior - 50
```

### Escenario: Crear ajuste manual de entrada
```gherkin
Given un usuario autenticado
And existe almacén con producto stock = 100
When crea ajuste ENTRADA_AJUSTE cantidad = 20 con observaciones
Then responde 200
And se crea movimiento con stockNuevo = 120
And el stock del producto en el almacén = 120
```

### Escenario: Rechazar ajuste sin observaciones
```gherkin
Given un usuario autenticado
When intenta crear ajuste SALIDA_AJUSTE sin observaciones
Then responde 400
And mensaje indica "Observaciones obligatorias para ajustes"
```

### Escenario: Rechazar ajuste que deja stock negativo
```gherkin
Given un usuario autenticado
And existe almacén con producto stock = 30
When intenta crear ajuste SALIDA_AJUSTE cantidad = 50
Then responde 400
And mensaje indica "Stock resultante no puede ser negativo"
```

### Escenario: Movimientos inmutables
```gherkin
Given un usuario autenticado
And existe movimiento registrado
When intenta editar o eliminar el movimiento
Then responde 400 o 405
And mensaje indica "Movimientos son inmutables"
```

### Escenario: Transferencia genera dos movimientos
```gherkin
Given un usuario autenticado
When transfiere 50 unidades de Almacén A a Almacén B
Then se generan 2 movimientos:
And movimiento 1: TRANSFERENCIA_SALIDA en Almacén A
And movimiento 2: TRANSFERENCIA_ENTRADA en Almacén B
And ambos con mismo referenciaId
```

### Escenario: Kardex muestra historial
```gherkin
Given un usuario autenticado
And existen 10 movimientos para producto "CAFE001" en agosto
When consulta kardex del producto
Then responde con lista ordenada por fecha
And cada movimiento muestra entrada, salida y saldo acumulado
And stockInicial + totalEntradas - totalSalidas = stockFinal
```

### Escenario: Resumen de período
```gherkin
Given un usuario autenticado
And existen movimientos en agosto 2026
When consulta resumen del período
Then responde con totales agrupados por tipo
And balance = totalEntradas - totalSalidas
```

---

## Arquitectura

### Capas a crear

```
controller/
  └── MovimientoStockController.java

applications/
  ├── command/movimientoStock/
  │   └── ajuste/
  │       ├── CreateAjusteCommand.java
  │       ├── CreateAjusteCommandHandler.java
  │       └── CreateAjusteRequest.java
  └── query/movimientoStock/
      ├── getById/
      ├── search/
      ├── porAlmacen/
      ├── porProducto/
      ├── porTipo/
      ├── porReferencia/
      ├── porFinca/
      ├── resumen/
      ├── balanceProducto/
      └── kardex/

domain/
  ├── dto/
  │   ├── MovimientoStockDto.java
  │   ├── ResumenMovimientosDto.java
  │   ├── BalanceProductoDto.java
  │   └── KardexDto.java
  ├── enums/
  │   ├── TipoMovimiento.java
  │   └── ReferenciaTipo.java
  └── services/
      └── IMovimientoStockService.java

infrastructure/
  ├── entity/
  │   └── MovimientoStock.java
  ├── repository/
  │   ├── command/
  │   │   └── MovimientoStockWriteDataJPARepository.java
  │   └── query/
  │       └── MovimientoStockReadDataJPARepository.java
  └── services/
      └── MovimientoStockServiceImpl.java
```

### Integración con otras features

**ProduccionTerminadaServiceImpl:**
- Al crear: llamar a `movimientoStockService.registrarEntrada(ENTRADA_PRODUCCION, ...)`
- Al anular: llamar a `movimientoStockService.registrarSalida(REVERSION_PRODUCCION, ...)`

**SalidaServiceImpl:**
- Al crear: llamar a `movimientoStockService.registrarSalida(SALIDA_VENTA|SALIDA_AUTOCONSUMO, ...)`
- Al anular: llamar a `movimientoStockService.registrarEntrada(REVERSION_SALIDA, ...)`

**AlmacenServiceImpl:**
- Al transferir: llamar a `movimientoStockService.registrarTransferencia(...)`

---

## Tests Requeridos

### Unit Tests
- [x] CreateAjusteCommandHandler valida observaciones obligatorias (RN-04)
- [x] CreateAjusteCommandHandler valida cantidad > 0 (RN-05)
- [x] CreateAjusteCommandHandler valida no stock negativo (RN-06)
- [x] CreateAjusteCommandHandler calcula stockAnterior y stockNuevo
- [x] MovimientoStockService genera movimiento por producción
- [x] MovimientoStockService genera movimiento por salida
- [ ] MovimientoStockService genera par de movimientos por transferencia
- [ ] Kardex calcula saldos acumulados correctamente
- [ ] Resumen agrupa por tipo correctamente

### Integration Tests
- [x] POST /api/movimiento-stock/ajuste crea correctamente
- [x] POST /api/movimiento-stock/ajuste rechaza sin observaciones
- [x] POST /api/movimiento-stock/ajuste rechaza stock negativo
- [ ] GET /api/movimiento-stock/kardex retorna historial correcto
- [ ] GET /api/movimiento-stock/resumen retorna totales correctos
- [ ] GET /api/movimiento-stock/balance-producto retorna balance correcto
- [ ] Al crear ProduccionTerminada se genera movimiento
- [ ] Al crear Salida se genera movimiento
- [ ] Al transferir se generan dos movimientos
- [ ] PUT/DELETE /api/movimiento-stock/{id} retorna error (inmutable)

### Test Files Created

**Unit Tests:**
- `src/test/java/com/kynsoft/report/infrastructure/services/MovimientoStockServiceImplTest.java`
  - Tests para crearAjuste(): validaciones de observaciones, cantidad, y stock negativo
  - Tests para findById(): encontrar movimiento y manejar no encontrado
  - Tests para TipoMovimientoStock enum: isEntrada(), isSalida(), isAjuste()

**Test Configuration:**
- `src/test/resources/application-test.properties` - Configuracion H2 para tests

---

## Cambios Pendientes

Para implementar esta spec:

1. **Crear enums TipoMovimiento y ReferenciaTipo**
2. **Crear entidad MovimientoStock**
3. **Crear repositorios read/write**
4. **Crear IMovimientoStockService con métodos:**
   - `registrarEntrada(tipo, almacenId, fincaProductoId, cantidad, referenciaId, referenciaTipo, observaciones)`
   - `registrarSalida(tipo, almacenId, fincaProductoId, cantidad, referenciaId, referenciaTipo, observaciones)`
   - `registrarTransferencia(almacenOrigenId, almacenDestinoId, fincaProductoId, cantidad, observaciones)`
   - `crearAjuste(tipo, almacenId, fincaProductoId, cantidad, observaciones)`
5. **Integrar en ProduccionTerminadaServiceImpl**
6. **Integrar en SalidaServiceImpl**
7. **Integrar en AlmacenServiceImpl (transferencias)**
8. **Crear endpoint /ajuste para ajustes manuales**
9. **Crear endpoints de consulta**
10. **Implementar Kardex con saldos acumulados**
11. **Implementar resumen y balance**
12. **Actualizar frontend**

---

## Out of Scope

- Aprobación/workflow para ajustes
- Límites de ajuste por usuario
- Notificaciones de ajustes
- Cierre de períodos contables
- Exportación a sistemas contables
- Firma digital de movimientos
- Blockchain/inmutabilidad criptográfica
