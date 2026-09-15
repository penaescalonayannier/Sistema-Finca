---
feature: deuda-trabajador
version: 1.0.0
status: implemented
priority: high
depends_on: [trabajador, salida]
---

# Feature: Gestión de Deudas de Trabajadores

## Contexto

El módulo DeudaTrabajador gestiona el control de deudas que los trabajadores acumulan al recibir productos de la finca. La deuda se genera automáticamente desde las Salidas con destino TRABAJADORES y se reduce mediante pagos.

El sistema mantiene:
- **DeudaTrabajador**: Saldo acumulado por trabajador
- **DeudaTrabajadorDetalle**: Historial completo de movimientos (auditoría)

## Actores

- Usuario autenticado (cualquier rol puede gestionar deudas y pagos)

## Dominio

### Entidad: DeudaTrabajador

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| id | UUID | auto | Identificador único |
| trabajadorId | UUID | sí | Referencia al trabajador |
| importe | Double | sí | Saldo actual de deuda (>= 0) |

### Entidad: DeudaTrabajadorDetalle

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| id | UUID | auto | Identificador único |
| trabajadorId | UUID | sí | Referencia al trabajador |
| tipoMovimiento | Enum | sí | Tipo de movimiento |
| salidaId | UUID | no | Referencia a salida (solo COMPRA) |
| salidaNumero | String | no | Número de salida (solo COMPRA) |
| salidaTipo | Enum | no | VALE/FACTURA (solo COMPRA) |
| productoId | UUID | no | Producto (solo COMPRA) |
| productoCodigo | String | no | Código del producto |
| productoNombre | String | no | Nombre del producto |
| cantidad | Integer | no | Cantidad (solo COMPRA) |
| precioUnitario | Double | no | Precio unitario (solo COMPRA) |
| importe | Double | sí | Monto del movimiento |
| fecha | LocalDateTime | auto | Fecha del movimiento |
| formaPago | Enum | no | Forma de pago (solo PAGO) |
| referenciaBancaria | String | no | Referencia (solo TRANSFERENCIA) |
| observaciones | String(500) | no | Observaciones/comentarios |
| activo | Boolean | auto | Estado del registro (default: true) |
| pagado | Boolean | no | Si pagó al momento (solo COMPRA) |

### Enum: TipoMovimiento

| Valor | Descripción | Efecto en saldo |
|-------|-------------|-----------------|
| COMPRA | Salida de productos a trabajador | + (incrementa) |
| PAGO | Pago de deuda | - (reduce) |
| AJUSTE | Ajuste contable (+ o -) | +/- según signo |
| CARGA_INICIAL | Migración de deuda inicial | + (incrementa) |

### Enum: FormaPago

```
EFECTIVO      - Pago en efectivo
TRANSFERENCIA - Transferencia bancaria (requiere referencia)
```

---

## Reglas de Negocio

### RN-01: Deuda automática desde Salidas
Cuando se crea una Salida con destino TRABAJADORES, se incrementa automáticamente la deuda de cada trabajador según el importe de sus items (cantidad × precio). Solo se incrementa si `pagado = false`.

### RN-02: Deuda no negativa
El saldo de deuda no puede ser negativo. Un pago no puede exceder el saldo actual del trabajador.

### RN-03: Referencia obligatoria para transferencia
Cuando `formaPago = TRANSFERENCIA`, el campo `referenciaBancaria` es obligatorio.

### RN-04: Pagos parciales permitidos
Se permite registrar pagos por cualquier monto, siempre que no exceda la deuda actual.

### RN-05: Sin eliminación de movimientos
Los pagos, ajustes y cargas iniciales no se pueden eliminar. Para corregir, se debe registrar un ajuste contrario.

### RN-06: Auditoría completa
Todos los movimientos (COMPRA, PAGO, AJUSTE, CARGA_INICIAL) se registran en DeudaTrabajadorDetalle con fecha, usuario y observaciones.

### RN-07: Ajustes con signo
Los ajustes pueden ser positivos (aumentar deuda) o negativos (reducir deuda), pero nunca dejar el saldo negativo.

### RN-08: Carga inicial documentada
Las cargas iniciales deben incluir observaciones obligatorias explicando el origen de la deuda.

---

## API Contract

### Base URL
```
/api/deuda-trabajador
```

### Autenticación
Todos los endpoints requieren usuario autenticado (Bearer Token JWT).

---

### GET /api/deuda-trabajador/{trabajadorId}
**Obtener deuda de un trabajador**

#### Response 200
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
  "trabajadorNombre": "Juan Pérez",
  "trabajadorRuc": "12345678901",
  "importe": 250.00
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Trabajador no encontrado |
| 401 | No autenticado |

---

### POST /api/deuda-trabajador/search
**Búsqueda paginada de trabajadores con deuda**

#### Request
```json
{
  "page": 0,
  "pageSize": 20,
  "filter": [
    {
      "key": "importe",
      "operator": "GREATER_THAN",
      "value": 0
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
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
      "trabajadorNombre": "Juan Pérez",
      "trabajadorRuc": "12345678901",
      "importe": 250.00
    }
  ],
  "totalElements": 15,
  "totalPages": 1,
  "page": 0,
  "pageSize": 20
}
```

---

### GET /api/deuda-trabajador/{trabajadorId}/detalle
**Obtener historial de movimientos de un trabajador**

#### Response 200
```json
{
  "content": [
    {
      "id": "770e8400-e29b-41d4-a716-446655440001",
      "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
      "tipoMovimiento": "COMPRA",
      "salidaId": "880e8400-e29b-41d4-a716-446655440000",
      "salidaNumero": "VALE-0001",
      "salidaTipo": "VALE",
      "productoId": "990e8400-e29b-41d4-a716-446655440000",
      "productoCodigo": "CAFE001",
      "productoNombre": "Café Arábica",
      "cantidad": 5,
      "precioUnitario": 10.00,
      "importe": 50.00,
      "fecha": "2026-08-20T10:30:00",
      "pagado": false,
      "activo": true
    },
    {
      "id": "770e8400-e29b-41d4-a716-446655440002",
      "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
      "tipoMovimiento": "PAGO",
      "importe": -30.00,
      "fecha": "2026-08-21T14:00:00",
      "formaPago": "EFECTIVO",
      "observaciones": "Pago parcial quincenal",
      "activo": true
    }
  ],
  "totalElements": 2,
  "totalPages": 1,
  "page": 0,
  "pageSize": 20
}
```

---

### POST /api/deuda-trabajador/pago
**Registrar pago de deuda**

#### Request
```json
{
  "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
  "monto": 100.00,
  "formaPago": "TRANSFERENCIA",
  "referenciaBancaria": "TRF-2026082100123",
  "observaciones": "Pago quincenal agosto"
}
```

#### Response 200
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440003",
  "saldoAnterior": 250.00,
  "saldoNuevo": 150.00
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Monto <= 0 |
| 400 | Monto excede deuda actual (RN-02) |
| 400 | Falta referencia bancaria para transferencia (RN-03) |
| 404 | Trabajador no encontrado |
| 401 | No autenticado |

---

### POST /api/deuda-trabajador/ajuste
**Registrar ajuste contable**

#### Request
```json
{
  "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
  "monto": -25.00,
  "observaciones": "Corrección por error en cantidad - Vale VALE-0001"
}
```

#### Response 200
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440004",
  "saldoAnterior": 150.00,
  "saldoNuevo": 125.00
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Monto = 0 |
| 400 | Ajuste dejaría saldo negativo (RN-07) |
| 400 | Observaciones vacías |
| 404 | Trabajador no encontrado |
| 401 | No autenticado |

---

### POST /api/deuda-trabajador/carga-inicial
**Registrar carga inicial de deuda**

#### Request
```json
{
  "trabajadorId": "660e8400-e29b-41d4-a716-446655440001",
  "monto": 500.00,
  "observaciones": "Migración de deuda del sistema anterior - Corte agosto 2026"
}
```

#### Response 200
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440005",
  "saldoAnterior": 0.00,
  "saldoNuevo": 500.00
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Monto <= 0 |
| 400 | Observaciones vacías (RN-08) |
| 404 | Trabajador no encontrado |
| 401 | No autenticado |

---

## Criterios de Aceptación

### Escenario: Deuda automática desde salida
```gherkin
Given un usuario autenticado
And existe trabajador "Juan Pérez" con deuda = 0
When se crea Salida a TRABAJADORES con item:
  | trabajadorId | cantidad | precio | pagado |
  | Juan Pérez   | 5        | 10.00  | false  |
Then la deuda de "Juan Pérez" = 50.00
And se registra detalle con tipoMovimiento = COMPRA
```

### Escenario: No incrementar si pagado al momento
```gherkin
Given un usuario autenticado
And existe trabajador "María García" con deuda = 100
When se crea Salida a TRABAJADORES con item:
  | trabajadorId  | cantidad | precio | pagado |
  | María García  | 2        | 20.00  | true   |
Then la deuda de "María García" = 100 (sin cambio)
And se registra detalle con tipoMovimiento = COMPRA y pagado = true
```

### Escenario: Pago parcial exitoso
```gherkin
Given un usuario autenticado
And existe trabajador con deuda = 200
When registra pago con monto = 80 y formaPago = EFECTIVO
Then responde 200
And la deuda = 120
And se registra detalle con tipoMovimiento = PAGO
```

### Escenario: Rechazar pago mayor a deuda
```gherkin
Given un usuario autenticado
And existe trabajador con deuda = 100
When registra pago con monto = 150
Then responde 400
And mensaje indica "El monto excede la deuda actual"
And la deuda sigue siendo 100
```

### Escenario: Pago por transferencia sin referencia
```gherkin
Given un usuario autenticado
And existe trabajador con deuda = 100
When registra pago con formaPago = TRANSFERENCIA y sin referenciaBancaria
Then responde 400
And mensaje indica "Referencia bancaria requerida para transferencia"
```

### Escenario: Ajuste negativo exitoso
```gherkin
Given un usuario autenticado
And existe trabajador con deuda = 200
When registra ajuste con monto = -50 y observaciones = "Corrección"
Then responde 200
And la deuda = 150
And se registra detalle con tipoMovimiento = AJUSTE
```

### Escenario: Rechazar ajuste que deja saldo negativo
```gherkin
Given un usuario autenticado
And existe trabajador con deuda = 100
When registra ajuste con monto = -150
Then responde 400
And mensaje indica "El ajuste dejaría saldo negativo"
```

### Escenario: Carga inicial exitosa
```gherkin
Given un usuario autenticado
And existe trabajador sin deuda registrada
When registra carga inicial con monto = 300 y observaciones = "Migración"
Then responde 200
And la deuda = 300
And se registra detalle con tipoMovimiento = CARGA_INICIAL
```

---

## Arquitectura

### Capas a modificar

```
controller/
  └── DeudaTrabajadorController.java

applications/
  ├── command/deudaTrabajador/
  │   ├── pago/
  │   │   ├── RegistrarPagoCommand.java
  │   │   ├── RegistrarPagoCommandHandler.java
  │   │   └── RegistrarPagoRequest.java
  │   ├── ajuste/
  │   │   ├── RegistrarAjusteCommand.java
  │   │   ├── RegistrarAjusteCommandHandler.java
  │   │   └── RegistrarAjusteRequest.java
  │   └── cargaInicial/
  │       ├── RegistrarCargaInicialCommand.java
  │       ├── RegistrarCargaInicialCommandHandler.java
  │       └── RegistrarCargaInicialRequest.java
  └── query/deudaTrabajador/
      ├── getById/
      │   └── FindDeudaByTrabajadorIdQueryHandler.java
      ├── getDetalle/
      │   └── GetDetalleByTrabajadorIdQueryHandler.java
      └── search/
          └── GetSearchDeudaTrabajadorQueryHandler.java

domain/
  ├── dto/
  │   ├── DeudaTrabajadorDto.java
  │   ├── DeudaTrabajadorDetalleDto.java
  │   ├── TipoMovimiento.java (actualizar)
  │   └── FormaPago.java
  └── services/
      ├── IDeudaTrabajadorService.java
      └── IDeudaTrabajadorDetalleService.java

infrastructure/
  ├── entity/
  │   ├── DeudaTrabajador.java
  │   └── DeudaTrabajadorDetalle.java
  ├── repository/
  │   ├── command/
  │   │   ├── DeudaTrabajadorWriteDataJPARepository.java
  │   │   └── DeudaTrabajadorDetalleWriteDataJPARepository.java
  │   └── query/
  │       ├── DeudaTrabajadorReadDataJPARepository.java
  │       └── DeudaTrabajadorDetalleReadDataJPARepository.java
  └── services/
      ├── DeudaTrabajadorServiceImpl.java
      └── DeudaTrabajadorDetalleServiceImpl.java
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
- [x] RegistrarPagoCommandHandler valida monto > 0
- [x] RegistrarPagoCommandHandler valida monto <= deuda actual (RN-02)
- [x] RegistrarPagoCommandHandler valida referencia para transferencia (RN-03)
- [x] RegistrarAjusteCommandHandler valida no dejar saldo negativo (RN-02)
- [ ] RegistrarAjusteCommandHandler valida observaciones no vacías
- [x] RegistrarCargaInicialCommandHandler valida monto > 0 (RN-08)
- [x] RegistrarCargaInicialCommandHandler valida observaciones no vacías

### Integration Tests
- [x] POST /api/deuda-trabajador/pago reduce saldo correctamente
- [x] POST /api/deuda-trabajador/pago rechaza monto > deuda
- [x] POST /api/deuda-trabajador/pago rechaza transferencia sin referencia
- [x] POST /api/deuda-trabajador/ajuste permite ajuste positivo
- [x] POST /api/deuda-trabajador/ajuste permite ajuste negativo
- [x] POST /api/deuda-trabajador/ajuste rechaza saldo negativo
- [x] POST /api/deuda-trabajador/carga-inicial crea deuda correctamente
- [x] GET /api/deuda-trabajador/{id}/detalle retorna historial ordenado

### Test Files Created

**Unit Tests:**
- `src/test/java/com/kynsoft/report/infrastructure/services/DeudaTrabajadorServiceImplTest.java`
  - Tests para registrarPago(): validación de monto > deuda (RN-02), referencia bancaria (RN-03)
  - Tests para registrarAjuste(): ajustes positivos, negativos, y validación de saldo negativo (RN-07)
  - Tests para registrarCargaInicial(): creación inicial, monto negativo, deuda existente (RN-08)
  - Tests para getSaldoActual(): obtener saldo y retornar 0 si no existe

**Integration Tests:**
- `src/test/java/com/kynsoft/report/controller/DeudaTrabajadorControllerIntegrationTest.java`
  - Tests para GET /{trabajadorId}: retornar deuda y deuda en cero
  - Tests para POST /pago: pago efectivo, pago transferencia con referencia
  - Tests para POST /ajuste: ajuste positivo y negativo
  - Tests para POST /carga-inicial: carga inicial
  - Tests para GET /{trabajadorId}/detalle: lista de detalles

---

## Cambios Pendientes

Para alinear el código actual con esta spec:

1. **Actualizar TipoMovimiento** - Agregar AJUSTE y CARGA_INICIAL
2. **Agregar campo observaciones** - En DeudaTrabajadorDetalle
3. **Implementar endpoint /pago** - Con validaciones RN-02, RN-03
4. **Implementar endpoint /ajuste** - Con validaciones RN-05, RN-07
5. **Implementar endpoint /carga-inicial** - Con validación RN-08
6. **Implementar endpoint /detalle** - Historial por trabajador
7. **Actualizar frontend** - Formularios de pago, ajuste y carga inicial

---

## Out of Scope

- Notificaciones de deuda alta
- Reportes consolidados de deudas
- Intereses por mora
- Descuento automático de nómina
- Límite de crédito por trabajador
