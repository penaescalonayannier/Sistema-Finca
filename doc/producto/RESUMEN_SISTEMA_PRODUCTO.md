# Sistema de Gestion de Productos

## Resumen General

El sistema de productos permite gestionar el catalogo de productos de la finca, incluyendo precios diferenciados por tipo de cliente, control de stock, y asociacion de productos a fincas especificas.

---

## 1. BACKEND (Spring Boot)

### 1.1 Entidad Producto

**Archivo:** `contabilidad/src/main/java/com/kynsoft/report/infrastructure/entity/Producto.java`

**Tabla:** `productos`

| Campo | Tipo | Restricciones | Descripcion |
|-------|------|---------------|-------------|
| id | UUID | PK | Identificador unico |
| code | String(50) | UNIQUE, NOT NULL | Codigo del producto |
| name | String(100) | NOT NULL | Nombre del producto |
| unidadMedida | String(100) | NOT NULL | Unidad de medida (kg, unidad, etc.) |
| description | String(500) | - | Descripcion opcional |
| price | Double | NOT NULL | Precio para "Otros" |
| priceTrabajador | Double | NOT NULL | Precio para Trabajadores |
| priceComedor | Double | NOT NULL | Precio para Comedor |
| stock | Integer | NOT NULL | Cantidad en inventario |
| active | Boolean | - | Estado activo/inactivo |

### 1.2 DTO Producto

**Archivo:** `contabilidad/src/main/java/com/kynsoft/report/domain/dto/ProductoDto.java`

```java
public class ProductoDto {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private Double price;          // Precio Otros
    private Double priceTrabajador; // Precio Trabajador
    private Double priceComedor;    // Precio Comedor
    private Integer stock;
    private Boolean active;
    private String unidadMedida;
}
```

### 1.3 Endpoints REST

**Base URL:** `/api/producto`

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| POST | `/api/producto` | Crear producto |
| GET | `/api/producto/{id}` | Obtener por ID |
| PUT | `/api/producto/{id}` | Actualizar producto |
| DELETE | `/api/producto/{id}` | Eliminar producto |
| POST | `/api/producto/search` | Busqueda paginada |

### 1.4 Estructura CQRS

#### Commands (Escritura)

| Operacion | Archivos |
|-----------|----------|
| **Create** | `CreateProductoCommand.java`, `CreateProductoRequest.java`, `CreateProductoCommandHandler.java`, `CreateProductoMessage.java` |
| **Update** | `UpdateProductoCommand.java`, `UpdateProductoRequest.java`, `UpdateProductoCommandHandler.java`, `UpdateProductoMessage.java` |
| **Delete** | `DeleteProductoCommand.java`, `DeleteProductoCommandHandler.java`, `DeleteProductoMessage.java` |

#### Queries (Lectura)

| Operacion | Archivos |
|-----------|----------|
| **GetById** | `FindProductoByIdQuery.java`, `FindProductoByIdQueryHandler.java` |
| **Search** | `GetSearchProductoQuery.java`, `GetSearchProductoQueryHandler.java` |

### 1.5 Repositorios

| Repositorio | Tipo | Descripcion |
|-------------|------|-------------|
| `ProductoWriteDataJPARepository` | Escritura | Operaciones de escritura (CQRS) |
| `ProductoReadDataJPARepository` | Lectura | Operaciones de lectura (CQRS) |

### 1.6 Servicio

**Interface:** `IProductoService.java`
**Implementacion:** `ProductoServiceImpl.java`

---

## 2. RELACION FINCA-PRODUCTO

### 2.1 Entidad FincaProducto

**Archivo:** `contabilidad/src/main/java/com/kynsoft/report/infrastructure/entity/FincaProducto.java`

**Tabla:** `finca_producto`

| Campo | Tipo | Restricciones | Descripcion |
|-------|------|---------------|-------------|
| id | UUID | PK | Identificador unico |
| finca_id | UUID | FK, NOT NULL | Referencia a Finca |
| producto_id | UUID | FK, NOT NULL | Referencia a Producto |
| stock | Integer | NOT NULL | Stock del producto en esta finca |

### 2.2 Endpoints FincaProducto

**Base URL:** `/api/finca-producto`

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| POST | `/api/finca-producto/asignar` | Asignar producto a finca |
| PUT | `/api/finca-producto/stock` | Actualizar stock |
| POST | `/api/finca-producto/search` | Busqueda paginada |
| GET | `/api/finca-producto/finca/{fincaId}/productos/activos` | Productos activos de una finca |

### 2.3 Estructura CQRS FincaProducto

#### Commands

| Operacion | Descripcion |
|-----------|-------------|
| **AsignarProductoAFinca** | Asigna un producto a una finca con stock inicial |
| **ActualizarStockFincaProducto** | Modifica el stock de un producto en una finca |
| **RemoverProductoDeFinca** | Elimina la asociacion producto-finca |

#### Queries

| Operacion | Descripcion |
|-----------|-------------|
| **GetProductosDeFinca** | Obtiene productos de una finca especifica |
| **GetAllFincaProducto** | Busqueda paginada de todas las asociaciones |

---

## 3. FRONTEND (Vue 3 + TypeScript)

### 3.1 Tipos TypeScript

**Archivo:** `mi-proyecto-vue/mi-proyecto/src/types/Producto.ts`

```typescript
export interface Producto {
  id?: string
  code: string
  name: string
  description?: string
  price: number
  stock: number
  active: boolean
}

export interface ProductoRequest {
  code: string
  name: string
  description?: string
  price: number
  stock: number
  active?: boolean
}

export interface ProductoResponse {
  id: string
  code: string
  name: string
  description: string
  price: number
  stock: number
  active: boolean
}
```

### 3.2 Tipos FincaProducto

**Archivo:** `mi-proyecto-vue/mi-proyecto/src/types/FincaProducto.ts`

```typescript
export interface FincaProducto {
  id: string
  fincaId: string
  fincaCode: string
  fincaName: string
  productoId: string
  productoCode: string
  productoName: string
  productoPrice: number
  stock: number
}

export interface AsignarProductoRequest {
  fincaId: string
  productoId: string
  stock: number
}

export interface ActualizarStockRequest {
  fincaId: string
  productoId: string
  stock: number
}
```

### 3.3 Servicio ProductoService

**Archivo:** `mi-proyecto-vue/mi-proyecto/src/services/ProductoService.ts`

| Metodo | Descripcion |
|--------|-------------|
| `crearProducto(producto)` | Crea un nuevo producto |
| `obtenerProductoPorId(id)` | Obtiene producto por ID |
| `actualizarProducto(id, producto)` | Actualiza un producto |
| `eliminarProducto(id)` | Elimina un producto |
| `buscarProductos(filtros)` | Busqueda con paginacion y filtros |
| `importarCsv(file)` | Importa productos desde CSV/Excel |
| `exportarProductos(ids)` | Exporta productos seleccionados |
| `obtenerProductosActivos()` | Lista productos activos |
| `obtenerProductosStockBajo(threshold)` | Productos con stock bajo |
| `obtenerProductoPorCodigo(code)` | Busca por codigo |
| `actualizarStock(id, cantidad)` | Actualiza solo el stock |

### 3.4 Componentes Vue

| Componente | Descripcion |
|------------|-------------|
| `ProductoList.vue` | Lista principal con busqueda, paginacion, seleccion multiple, importar/exportar |
| `CrearProducto.vue` | Formulario para crear y editar productos |
| `DetalleProducto.vue` | Vista detallada de un producto |
| `FincaProductoList.vue` | Gestion de productos por finca |

### 3.5 Funcionalidades del ProductoList.vue

1. **Busqueda:** Por codigo, nombre o descripcion
2. **Paginacion:** Configurable (10, 25, 50, 150 registros)
3. **Seleccion multiple:** Checkbox para seleccionar productos
4. **Acciones por producto:**
   - Ver detalle
   - Editar
   - Eliminar
5. **Importar CSV/Excel:** Modal con drag & drop
6. **Exportar:** Descarga Excel de productos seleccionados
7. **Indicadores visuales:**
   - Estado activo/inactivo con badges
   - Stock con colores (cero, bajo, medio, alto)

---

## 4. FORMATO DE BUSQUEDA (API)

### Request de Busqueda

```json
{
  "filter": [
    {
      "property": "active",
      "operator": "EQUALS",
      "value": "true"
    }
  ],
  "query": "texto de busqueda",
  "pageSize": 10,
  "page": 0,
  "sortBy": "name",
  "sortType": "ASC"
}
```

### Response Paginada

```json
{
  "data": [...],
  "totalElements": 100,
  "totalPages": 10,
  "currentPage": 0,
  "pageSize": 10
}
```

---

## 5. FORMATO DE IMPORTACION CSV/EXCEL

**NOTA:** El endpoint de importacion (`/api/producto/import-csv`) **NO ESTA IMPLEMENTADO** en el backend actualmente. Solo existe el metodo en el frontend.

### Formato Sugerido para Implementacion

```csv
code,name,description,price,priceTrabajador,priceComedor,unidadMedida,stock,active
PROD001,Fertilizante NPK,Fertilizante para cultivos,150.50,140.00,135.00,KG,100,true
PROD002,Semilla de Maiz,Semilla certificada,25.00,23.00,22.00,UNIDAD,500,true
```

| Columna | Tipo | Requerido | Descripcion |
|---------|------|-----------|-------------|
| code | String | Si | Codigo unico del producto |
| name | String | Si | Nombre del producto |
| description | String | No | Descripcion |
| price | Double | Si | Precio para Otros |
| priceTrabajador | Double | Si | Precio para Trabajadores |
| priceComedor | Double | Si | Precio para Comedor |
| unidadMedida | String | Si | Unidad de medida |
| stock | Integer | Si | Cantidad inicial |
| active | Boolean | No | Estado (default: true) |

---

## 6. ESTRUCTURA DE ARCHIVOS

### Backend

```
contabilidad/src/main/java/com/kynsoft/report/
├── controller/
│   ├── ProductoController.java
│   └── FincaProductoController.java
├── domain/
│   ├── dto/
│   │   ├── ProductoDto.java
│   │   └── FincaProductoDto.java
│   └── services/
│       ├── IProductoService.java
│       └── IFincaProductoService.java
├── infrastructure/
│   ├── entity/
│   │   ├── Producto.java
│   │   └── FincaProducto.java
│   ├── repository/
│   │   ├── command/
│   │   │   ├── ProductoWriteDataJPARepository.java
│   │   │   └── FincaProductoWriteDataJPARepository.java
│   │   └── query/
│   │       ├── ProductoReadDataJPARepository.java
│   │       └── FincaProductoReadDataJPARepository.java
│   └── services/
│       ├── ProductoServiceImpl.java
│       └── FincaProductoServiceImpl.java
└── applications/
    ├── command/
    │   ├── producto/
    │   │   ├── create/
    │   │   ├── update/
    │   │   └── delete/
    │   └── fincaproducto/
    │       ├── asignar/
    │       ├── actualizar/
    │       └── remover/
    └── query/
        ├── producto/
        │   ├── getById/
        │   └── search/
        ├── fincaproducto/
        │   ├── getall/
        │   └── getproductos/
        └── responseObject/
            ├── ProductoResponse.java
            ├── FincaProductoResponse.java
            └── FincaProductoListResponse.java
```

### Frontend

```
mi-proyecto-vue/mi-proyecto/src/
├── components/
│   ├── ProductoList.vue
│   ├── CrearProducto.vue
│   ├── DetalleProducto.vue
│   └── FincaProductoList.vue
├── services/
│   ├── ProductoService.ts
│   └── FincaProductoService.ts
├── types/
│   ├── Producto.ts
│   └── FincaProducto.ts
└── router/
    └── index.ts (rutas de productos)
```

---

## 7. PENDIENTES DE IMPLEMENTACION

1. **Endpoint de importacion CSV/Excel** - El frontend tiene el metodo pero el backend no tiene el endpoint
2. **Endpoint de exportacion** - Similar al anterior
3. **Endpoint de productos activos** - `/api/producto/active`
4. **Endpoint de productos con stock bajo** - `/api/producto/low-stock`
5. **Endpoint de busqueda por codigo** - `/api/producto/code/{code}`
6. **Endpoint de actualizacion de stock** - `PATCH /api/producto/{id}/stock`

---

## 8. NOTAS ADICIONALES

### Precios Diferenciados

El sistema maneja 3 tipos de precios:
- **price:** Precio general para clientes externos u otros
- **priceTrabajador:** Precio especial para trabajadores de la finca
- **priceComedor:** Precio para el comedor de la finca

### Arquitectura CQRS

El sistema implementa el patron CQRS (Command Query Responsibility Segregation):
- Repositorios separados para lectura (`*ReadDataJPARepository`) y escritura (`*WriteDataJPARepository`)
- Commands para operaciones de modificacion
- Queries para operaciones de lectura
- Mediator para despachar comandos y queries

### Relacion Finca-Producto

Permite asignar productos a fincas especificas con stock independiente. Un producto puede estar en multiples fincas con diferentes cantidades de stock.

---

*Documento generado: Agosto 2026*
*Sistema: Gestion de Finca - Modulo de Productos*
