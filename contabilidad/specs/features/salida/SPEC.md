---
feature: salida
version: 1.0.0
status: approved
priority: high
depends_on: [producto, finca-producto, trabajador]
---

# Feature: Gestión de Salidas

## Contexto

Una Salida representa la entrega de productos desde el inventario de la finca. Puede ser:
- **Venta**: Se genera una FACTURA (destinos: VENTA_ESTADO, POBLACION)
- **Uso interno**: Se genera un VALE (destinos: TRABAJADORES, COMEDOR, INSUMO, OTROS)

Cada salida puede tener múltiples items cuando el destino es TRABAJADORES, donde cada item representa la entrega a un trabajador específico.

## Actores

- Usuario autenticado (cualquier rol puede gestionar salidas)

## Dominio

### Entidad: Salida

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| id | UUID | auto | Identificador único |
| tipo | Enum | sí | VALE o FACTURA |
| destino | Enum | sí | Destino de la salida |
| numero | String | auto | Número autogenerado (FAC-0001, VALE-0001) |
| fincaProductoId | UUID | sí | Referencia a FincaProducto |
| fecha | LocalDateTime | auto | Fecha de la salida |
| observaciones | String(500) | no | Observaciones opcionales |
| activo | Boolean | auto | Estado (true = activo, false = anulado) |
| items | List<ItemSalida> | no | Items de la salida (solo para TRABAJADORES) |

### Entidad: ItemSalida

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| id | UUID | auto | Identificador único |
| salidaId | UUID | sí | Referencia a la salida |
| trabajadorId | UUID | sí | Trabajador que recibe |
| cantidad | Integer | sí | Cantidad entregada |
| precio | Double | auto | Precio unitario (según destino) |
| pagado | Boolean | auto | Si el trabajador ya pagó (default: false) |

### Enum: TipoSalida

```
VALE     - Salida para uso interno (genera Vale)
FACTURA  - Venta de productos (genera Factura)
```

### Enum: DestinoSalida

| Valor | Descripción | Tipo | Precio usado | Requiere Items |
|-------|-------------|------|--------------|----------------|
| TRABAJADORES | Entrega a trabajadores | VALE | priceTrabajador | Sí |
| COMEDOR | Salida al comedor | VALE | priceComedor | No |
| VENTA_ESTADO | Venta al estado | FACTURA | price | No |
| POBLACION | Venta a la población | FACTURA | price | No |
| INSUMO | Uso como insumo | VALE | price | No |
| OTROS | Otros destinos | VALE | price | No |

---

## Reglas de Negocio

### RN-01: Número autogenerado
El número de salida se genera automáticamente según el tipo:
- FACTURA: `FAC-0001`, `FAC-0002`, ...
- VALE: `VALE-0001`, `VALE-0002`, ...

### RN-02: Precio automático según destino
El precio de cada item se calcula automáticamente según el destino:
- TRABAJADORES → `priceTrabajador` del producto
- COMEDOR → `priceComedor` del producto
- VENTA_ESTADO, POBLACION, INSUMO, OTROS → `price` del producto

### RN-03: Descuento automático de stock
Al crear una salida, el stock del producto (en FincaProducto) se descuenta automáticamente por la cantidad total de la salida.

### RN-04: Validación de stock
No se permite crear una salida si no hay stock suficiente. La operación se bloquea.

### RN-05: Devolución de stock al eliminar
Al eliminar/anular una salida, el stock se devuelve automáticamente al inventario.

### RN-06: No eliminar si hay items pagados
No se puede eliminar una salida si tiene al menos un item con `pagado = true`.

### RN-07: Items solo para TRABAJADORES
Solo el destino TRABAJADORES requiere items con trabajadorId. Los demás destinos no tienen items.

### RN-08: Pago desde DeudaTrabajador
El estado `pagado` de los items se actualiza desde el módulo DeudaTrabajador, no desde Salida.

### RN-09: Tipo según destino
- TRABAJADORES, COMEDOR, INSUMO, OTROS → tipo = VALE
- VENTA_ESTADO, POBLACION → tipo = FACTURA

---

## API Contract

### Base URL
```
/api/salida
```

### Autenticación
Todos los endpoints requieren usuario autenticado (Bearer Token JWT).

---

### POST /api/salida
**Crear salida**

#### Request (Destino TRABAJADORES - con items)
```json
{
  "destino": "TRABAJADORES",
  "fincaProductoId": "550e8400-e29b-41d4-a716-446655440000",
  "observaciones": "Entrega semanal",
  "items": [
    {
      "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
      "cantidad": 2
    },
    {
      "trabajadorId": "660e8400-e29b-41d4-a716-446655440002",
      "cantidad": 3
    }
  ]
}
```

#### Request (Otros destinos - sin items)
```json
{
  "destino": "COMEDOR",
  "fincaProductoId": "550e8400-e29b-41d4-a716-446655440000",
  "cantidad": 10,
  "observaciones": "Suministro mensual"
}
```

#### Response 200
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "numero": "VALE-0001"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Datos inválidos |
| 400 | Stock insuficiente (RN-04) |
| 400 | FincaProducto no encontrado |
| 400 | Trabajador no encontrado |
| 400 | Cantidad <= 0 |
| 401 | No autenticado |

---

### GET /api/salida/{id}
**Obtener salida por ID**

#### Response 200
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "tipo": "VALE",
  "destino": "TRABAJADORES",
  "numero": "VALE-0001",
  "fincaProductoId": "550e8400-e29b-41d4-a716-446655440000",
  "fincaCode": "FINCA01",
  "fincaName": "Finca La Esperanza",
  "productoCode": "CAFE001",
  "productoName": "Café Arábica",
  "fecha": "2026-08-20T10:30:00",
  "observaciones": "Entrega semanal",
  "cantidadTotal": 5,
  "activo": true,
  "items": [
    {
      "id": "880e8400-e29b-41d4-a716-446655440001",
      "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
      "trabajadorNombre": "Juan Pérez",
      "cantidad": 2,
      "precio": 12.00,
      "pagado": false
    },
    {
      "id": "880e8400-e29b-41d4-a716-446655440002",
      "trabajadorId": "660e8400-e29b-41d4-a716-446655440002",
      "trabajadorNombre": "María García",
      "cantidad": 3,
      "precio": 12.00,
      "pagado": true
    }
  ]
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Salida no encontrada |
| 401 | No autenticado |

---

### PUT /api/salida/{id}
**Actualizar salida**

Solo se pueden actualizar las observaciones. No se puede cambiar el destino, producto ni items una vez creada.

#### Request
```json
{
  "observaciones": "Entrega semanal - actualizado"
}
```

#### Response 200
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Salida no encontrada |
| 400 | Salida ya anulada |
| 401 | No autenticado |

---

### DELETE /api/salida/{id}
**Anular salida (soft delete)**

Anula la salida y devuelve el stock al inventario.

#### Response 200
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Salida no encontrada |
| 400 | Tiene items pagados (RN-06) |
| 400 | Ya está anulada |
| 401 | No autenticado |

---

### POST /api/salida/search
**Búsqueda paginada**

#### Request
```json
{
  "page": 0,
  "pageSize": 20,
  "filter": [
    {
      "key": "destino",
      "operator": "EQUAL",
      "value": "TRABAJADORES"
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
  "content": [...],
  "totalElements": 50,
  "totalPages": 3,
  "page": 0,
  "pageSize": 20
}
```

---

### GET /api/salida/{id}/factura
**Descargar PDF (Factura o Vale)**

Genera y descarga el documento PDF de la salida.

#### Response 200
- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename="Vale_VALE-0001.pdf"` o `"Factura_FAC-0001.pdf"`

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Salida no encontrada |
| 500 | Error al generar PDF |

---

## Criterios de Aceptación

### Escenario: Crear salida a trabajadores
```gherkin
Given un usuario autenticado
And existe FincaProducto con stock = 100
When envía POST /api/salida con:
  | destino | TRABAJADORES |
  | items   | [{trabajadorId: "...", cantidad: 5}, {trabajadorId: "...", cantidad: 3}] |
Then responde 200
And se genera número "VALE-XXXX"
And el stock del FincaProducto se reduce en 8
And cada item tiene precio = priceTrabajador del producto
```

### Escenario: Crear salida al comedor
```gherkin
Given un usuario autenticado
And existe FincaProducto con stock = 50
When envía POST /api/salida con:
  | destino  | COMEDOR |
  | cantidad | 10      |
Then responde 200
And se genera número "VALE-XXXX"
And el stock se reduce en 10
And el precio usado es priceComedor
```

### Escenario: Rechazar por stock insuficiente
```gherkin
Given un usuario autenticado
And existe FincaProducto con stock = 5
When envía POST /api/salida con cantidad total = 10
Then responde 400
And el mensaje indica "Stock insuficiente"
And el stock NO se modifica
```

### Escenario: Eliminar salida sin items pagados
```gherkin
Given un usuario autenticado
And existe salida VALE-0001 con items sin pagar
When envía DELETE /api/salida/{id}
Then responde 200
And la salida queda con activo = false
And el stock se devuelve al inventario
```

### Escenario: No eliminar salida con items pagados
```gherkin
Given un usuario autenticado
And existe salida VALE-0001 con al menos un item pagado
When envía DELETE /api/salida/{id}
Then responde 400
And el mensaje indica "No se puede eliminar: tiene items pagados"
And la salida sigue activa
```

### Escenario: Generar número automático
```gherkin
Given existen salidas con números VALE-0001, VALE-0002
When se crea una nueva salida tipo VALE
Then el número asignado es VALE-0003
```

---

## Arquitectura

### Capas a modificar

```
controller/
  └── SalidaController.java

applications/
  ├── command/salida/
  │   ├── create/
  │   │   ├── CreateSalidaCommand.java
  │   │   ├── CreateSalidaCommandHandler.java
  │   │   └── CreateSalidaRequest.java
  │   ├── update/
  │   │   ├── UpdateSalidaCommand.java
  │   │   ├── UpdateSalidaCommandHandler.java
  │   │   └── UpdateSalidaRequest.java
  │   └── delete/
  │       ├── DeleteSalidaCommand.java
  │       └── DeleteSalidaCommandHandler.java
  └── query/salida/
      ├── getbyid/
      │   └── FindSalidaByIdQueryHandler.java
      └── getall/
          └── GetAllSalidaQueryHandler.java

domain/
  ├── dto/
  │   ├── SalidaDto.java
  │   ├── ItemSalidaDto.java
  │   ├── TipoSalida.java
  │   └── DestinoSalida.java
  └── services/
      └── ISalidaService.java

infrastructure/
  ├── entity/
  │   ├── Salida.java
  │   └── ItemSalida.java
  ├── repository/
  │   ├── command/
  │   │   ├── SalidaWriteDataJPARepository.java
  │   │   └── ItemSalidaWriteDataJPARepository.java
  │   └── query/
  │       ├── SalidaReadDataJPARepository.java
  │       └── ItemSalidaReadDataJPARepository.java
  └── services/
      ├── SalidaServiceImpl.java
      └── FacturaPdfService.java
```

### Patrón: CQRS con Mediator

```
Controller
    → Command/Query
    → IMediator
    → Handler
    → Service
    → Repository
```

---

## Tests Requeridos

### Unit Tests
- [ ] CreateSalidaCommandHandler genera número automático correcto
- [ ] CreateSalidaCommandHandler calcula precio según destino
- [ ] CreateSalidaCommandHandler descuenta stock
- [ ] CreateSalidaCommandHandler rechaza si stock insuficiente
- [ ] CreateSalidaCommandHandler valida items para TRABAJADORES
- [ ] DeleteSalidaCommandHandler devuelve stock
- [ ] DeleteSalidaCommandHandler rechaza si hay items pagados

### Integration Tests
- [ ] POST /api/salida crea salida correctamente
- [ ] POST /api/salida descuenta stock
- [ ] POST /api/salida rechaza por stock insuficiente
- [ ] DELETE /api/salida devuelve stock
- [ ] DELETE /api/salida rechaza si items pagados
- [ ] GET /api/salida/{id}/factura genera PDF

---

## Cambios Pendientes

Para alinear el código actual con esta spec:

1. **Implementar generación automática de número** (FAC-XXXX, VALE-XXXX)
2. **Calcular precio automáticamente según destino**
3. **Descontar stock al crear salida**
4. **Devolver stock al eliminar salida**
5. **Validar stock suficiente antes de crear**
6. **Validar no eliminar si hay items pagados**
7. **Determinar tipo automáticamente según destino**

---

## Out of Scope

- Edición de items después de crear la salida
- Cambio de destino después de crear
- Reportes consolidados de salidas
- Notificaciones al crear salidas
