---
feature: producto
version: 1.0.0
status: approved
priority: high
depends_on: []
---

# Feature: Gestión de Productos

## Contexto

El sistema necesita gestionar los productos de la finca, incluyendo insumos y productos para venta. Cada producto tiene diferentes precios según el destino de venta (externos, trabajadores, comedor).

## Actores

- Usuario autenticado (cualquier rol puede gestionar productos)

## Dominio

### Entidad: Producto

| Campo | Tipo | Requerido | Único | Descripción |
|-------|------|-----------|-------|-------------|
| id | UUID | auto | sí | Identificador único |
| code | String(50) | sí | sí | Código alfanumérico (sin espacios ni caracteres especiales) |
| name | String(100) | sí | no | Nombre del producto |
| unidadMedida | Enum | sí | no | Unidad de medida |
| description | String(500) | no | no | Descripción opcional |
| price | Double | sí | no | Precio para ventas externas/otros |
| priceTrabajador | Double | sí | no | Precio para trabajadores |
| priceComedor | Double | sí | no | Precio para comedor |
| stock | Integer | sí | no | Cantidad en inventario |
| active | Boolean | sí | no | Estado activo/inactivo |
| tipoProducto | Enum | sí | no | Tipo de producto |

### Enum: TipoProducto

```
INSUMO    - Productos de consumo interno
VENTA     - Productos para venta
OTROS     - Otros tipos
```

### Enum: UnidadMedida

| Código | Nombre | Categoría |
|--------|--------|-----------|
| KG | Kilogramo | Peso |
| G | Gramo | Peso |
| LB | Libra | Peso |
| QQ | Quintal | Peso |
| L | Litro | Volumen |
| ML | Mililitro | Volumen |
| GAL | Galón | Volumen |
| UND | Unidad | Cantidad |
| DOC | Docena | Cantidad |
| SACO | Saco | Cantidad |
| CAJA | Caja | Cantidad |
| M | Metro | Longitud |
| CM | Centímetro | Longitud |

---

## Reglas de Negocio

### RN-01: Precios mayores a cero
Todos los precios (price, priceTrabajador, priceComedor) deben ser mayores a 0.

### RN-02: Desactivación automática por precio cero
Si al actualizar un producto algún precio queda en 0, el producto se desactiva automáticamente (`active = false`).

### RN-03: Stock no negativo
El stock nunca puede ser menor a 0. Las operaciones que intenten dejar el stock negativo deben ser rechazadas.

### RN-04: Código alfanumérico
El código del producto solo puede contener letras (a-z, A-Z) y números (0-9). No se permiten espacios ni caracteres especiales.

### RN-05: Código único
No pueden existir dos productos con el mismo código (case-insensitive).

### RN-06: Soft Delete
Al eliminar un producto, solo se desactiva (`active = false`). No se borra físicamente de la base de datos.

### RN-07: Reactivación
Un producto inactivo puede reactivarse mediante el endpoint de actualización enviando `active = true`.

---

## API Contract

### Base URL
```
/api/producto
```

### Autenticación
Todos los endpoints requieren usuario autenticado (Bearer Token JWT).

---

### POST /api/producto
**Crear producto**

#### Request
```json
{
  "code": "CAFE001",
  "name": "Café Arábica",
  "unidadMedida": "KG",
  "description": "Café de primera calidad",
  "price": 15.50,
  "priceTrabajador": 12.00,
  "priceComedor": 10.00,
  "stock": 100,
  "tipoProducto": "VENTA"
}
```

#### Response 200
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Validación fallida (campos requeridos, formato inválido) |
| 400 | Código ya existe |
| 400 | Precio <= 0 |
| 400 | Stock < 0 |
| 401 | No autenticado |

---

### GET /api/producto/{id}
**Obtener producto por ID**

#### Response 200
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "code": "CAFE001",
  "name": "Café Arábica",
  "unidadMedida": "KG",
  "description": "Café de primera calidad",
  "price": 15.50,
  "priceTrabajador": 12.00,
  "priceComedor": 10.00,
  "stock": 100,
  "active": true,
  "tipoProducto": "VENTA"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Producto no encontrado |
| 401 | No autenticado |

---

### PUT /api/producto/{id}
**Actualizar producto**

Permite actualizar cualquier campo, incluyendo reactivar un producto inactivo.

#### Request
```json
{
  "code": "CAFE001",
  "name": "Café Arábica Premium",
  "unidadMedida": "KG",
  "description": "Café de primera calidad - edición especial",
  "price": 18.00,
  "priceTrabajador": 14.00,
  "priceComedor": 12.00,
  "stock": 150,
  "active": true,
  "tipoProducto": "VENTA"
}
```

#### Response 200
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Validación fallida |
| 400 | Código ya existe (si se cambia a uno existente) |
| 404 | Producto no encontrado |
| 401 | No autenticado |

#### Comportamiento especial
- Si algún precio es 0 → producto se desactiva automáticamente (RN-02)

---

### DELETE /api/producto/{id}
**Desactivar producto (soft delete)**

No elimina físicamente. Solo establece `active = false`.

#### Response 200
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 404 | Producto no encontrado |
| 401 | No autenticado |

---

### POST /api/producto/search
**Búsqueda paginada**

#### Request
```json
{
  "page": 0,
  "pageSize": 20,
  "filter": [
    {
      "field": "tipoProducto",
      "operator": "EQUAL",
      "value": "VENTA"
    },
    {
      "field": "active",
      "operator": "EQUAL",
      "value": true
    }
  ],
  "query": "café"
}
```

#### Response 200
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "code": "CAFE001",
      "name": "Café Arábica",
      "unidadMedida": "KG",
      "price": 15.50,
      "priceTrabajador": 12.00,
      "priceComedor": 10.00,
      "stock": 100,
      "active": true,
      "tipoProducto": "VENTA"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "page": 0,
  "pageSize": 20
}
```

---

### POST /api/producto/import-excel
**Importar productos desde Excel**

#### Request
- Content-Type: `multipart/form-data`
- Campo: `file` (archivo .xlsx o .xls)

#### Formato del Excel
| code | name | unidadMedida | description | price | priceTrabajador | priceComedor | stock | tipoProducto |
|------|------|--------------|-------------|-------|-----------------|--------------|-------|--------------|
| CAFE001 | Café | KG | Descripción | 15.50 | 12.00 | 10.00 | 100 | VENTA |

#### Response 200
```json
{
  "totalProcesados": 10,
  "totalExitosos": 8,
  "totalErrores": 2,
  "errores": [
    "Fila 3: Código CAFE001 ya existe",
    "Fila 7: Precio no puede ser 0"
  ]
}
```

#### Errores
| Code | Condición |
|------|-----------|
| 400 | Archivo vacío |
| 400 | Formato inválido (no es .xlsx o .xls) |
| 401 | No autenticado |

---

## Criterios de Aceptación

### Escenario: Crear producto válido
```gherkin
Given un usuario autenticado
When envía POST /api/producto con datos válidos
Then responde 200
And retorna el ID del producto creado
And el producto existe en la base de datos con active = true
```

### Escenario: Crear producto con código duplicado
```gherkin
Given un usuario autenticado
And existe un producto con code "CAFE001"
When envía POST /api/producto con code "CAFE001"
Then responde 400
And el mensaje indica que el código ya existe
```

### Escenario: Crear producto con precio cero
```gherkin
Given un usuario autenticado
When envía POST /api/producto con price = 0
Then responde 400
And el mensaje indica que el precio debe ser mayor a 0
```

### Escenario: Crear producto con código inválido
```gherkin
Given un usuario autenticado
When envía POST /api/producto con code "CAFE-001" (contiene guión)
Then responde 400
And el mensaje indica que el código solo permite letras y números
```

### Escenario: Actualizar producto con precio cero desactiva
```gherkin
Given un usuario autenticado
And existe un producto activo con id "123"
When envía PUT /api/producto/123 con price = 0
Then responde 200
And el producto queda con active = false
```

### Escenario: Reactivar producto
```gherkin
Given un usuario autenticado
And existe un producto inactivo con id "123"
When envía PUT /api/producto/123 con active = true y precios válidos
Then responde 200
And el producto queda con active = true
```

### Escenario: Soft delete
```gherkin
Given un usuario autenticado
And existe un producto activo con id "123"
When envía DELETE /api/producto/123
Then responde 200
And el producto sigue en la base de datos
And el producto tiene active = false
```

### Escenario: Stock no puede ser negativo
```gherkin
Given un usuario autenticado
When envía POST /api/producto con stock = -5
Then responde 400
And el mensaje indica que el stock no puede ser negativo
```

---

## Arquitectura

### Capas a modificar

```
controller/
  └── ProductoController.java

applications/
  ├── command/producto/
  │   ├── create/
  │   │   ├── CreateProductoCommand.java
  │   │   ├── CreateProductoCommandHandler.java
  │   │   └── CreateProductoRequest.java
  │   ├── update/
  │   │   ├── UpdateProductoCommand.java
  │   │   ├── UpdateProductoCommandHandler.java
  │   │   └── UpdateProductoRequest.java
  │   ├── delete/
  │   │   ├── DeleteProductoCommand.java
  │   │   └── DeleteProductoCommandHandler.java
  │   └── importexcel/
  │       ├── ImportProductoExcelCommand.java
  │       └── ImportProductoExcelCommandHandler.java
  └── query/producto/
      ├── getById/
      │   └── FindProductoByIdQuery.java
      └── search/
          └── GetSearchProductoQuery.java

domain/
  ├── dto/
  │   ├── ProductoDto.java
  │   ├── TipoProducto.java
  │   └── UnidadMedida.java  # NUEVO: Enum de unidades
  └── services/
      └── IProductoService.java

infrastructure/
  ├── entity/
  │   └── Producto.java
  ├── repository/
  │   ├── command/ProductoWriteDataJPARepository.java
  │   └── query/ProductoReadDataJPARepository.java
  └── services/
      └── ProductoServiceImpl.java
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
- [ ] CreateProductoCommandHandler valida campos requeridos
- [ ] CreateProductoCommandHandler rechaza código duplicado
- [ ] CreateProductoCommandHandler rechaza precio <= 0
- [ ] CreateProductoCommandHandler rechaza stock < 0
- [ ] CreateProductoCommandHandler rechaza código con caracteres especiales
- [ ] UpdateProductoCommandHandler desactiva si precio = 0
- [ ] UpdateProductoCommandHandler permite reactivar producto
- [ ] DeleteProductoCommandHandler hace soft delete

### Integration Tests
- [ ] POST /api/producto crea producto correctamente
- [ ] POST /api/producto retorna 400 con datos inválidos
- [ ] GET /api/producto/{id} retorna producto
- [ ] GET /api/producto/{id} retorna 404 si no existe
- [ ] PUT /api/producto/{id} actualiza correctamente
- [ ] DELETE /api/producto/{id} desactiva producto
- [ ] POST /api/producto/search filtra correctamente
- [ ] POST /api/producto/import-excel procesa archivo

---

## Cambios Pendientes

Para alinear el código actual con esta spec:

1. **Crear enum UnidadMedida** - Actualmente es String libre
2. **Validar código alfanumérico** - Agregar regex en validación
3. **Validar precios > 0** - En create y update
4. **Implementar RN-02** - Desactivación automática si precio = 0
5. **Validar stock >= 0** - En create y update

---

## Out of Scope

- Historial de cambios de precio
- Alertas de stock bajo
- Categorías/subcategorías de productos
- Imágenes de productos
