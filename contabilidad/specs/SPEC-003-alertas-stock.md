# SPEC-003: Alertas de Stock Mínimo

## Metadata
- **ID**: SPEC-003
- **Módulo**: Inventario/Stock
- **Prioridad**: MEDIA
- **Estado**: Propuesto
- **Fecha**: 2026-08-22

---

## 1. Problema

El sistema no alerta cuando el stock de un producto está por debajo de un umbral:
- No existe campo de stock mínimo en FincaProducto
- No hay endpoint para consultar productos con stock bajo
- No hay indicadores visuales de alerta en la UI
- El usuario debe revisar manualmente cada producto

### Impacto
- Desabastecimiento no detectado a tiempo
- Pérdida de producción por falta de insumos
- Gestión reactiva en lugar de preventiva

---

## 2. Requisitos Funcionales

### RF-001: Configuración de Stock Mínimo
- Cada FincaProducto DEBE tener un campo `stockMinimo`
- El valor por defecto DEBE ser 0 (sin alerta)
- DEBE ser editable por el usuario

### RF-002: Detección de Stock Bajo
- El sistema DEBE identificar productos donde: `stock < stockMinimo`
- DEBE proveer endpoint para listar productos en alerta
- DEBE calcular automáticamente después de cada movimiento

### RF-003: Indicadores Visuales
- Los productos con stock bajo DEBEN mostrar badge de alerta
- Color rojo para crítico (stock = 0)
- Color amarillo para bajo (0 < stock < stockMinimo)
- Color verde para normal (stock >= stockMinimo)

### RF-004: Dashboard de Alertas
- Vista consolidada de todos los productos con stock bajo
- Agrupados por finca
- Ordenados por criticidad (más bajo primero)
- Acceso rápido para crear entrada de stock

### RF-005: Notificaciones (Futuro)
- Opcional: Enviar email/notificación cuando stock cae bajo mínimo
- Frecuencia configurable (inmediata, diaria, semanal)

---

## 3. Requisitos No Funcionales

### RNF-001: Rendimiento
- La consulta de alertas DEBE responder en menos de 500ms
- NO DEBE afectar el rendimiento de movimientos de stock

### RNF-002: Escalabilidad
- DEBE funcionar con 1000+ productos sin degradación

---

## 4. Modelo de Datos

### Modificación a FincaProducto

```java
@Entity
@Table(name = "finca_producto")
public class FincaProducto {
    // Campos existentes...

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 0;  // NUEVO

    @Column(name = "stock_maximo")
    private Integer stockMaximo;  // NUEVO (opcional, para sobrestock)

    // Método helper
    public EstadoStock getEstadoStock() {
        if (stock == 0) return EstadoStock.CRITICO;
        if (stock < stockMinimo) return EstadoStock.BAJO;
        if (stockMaximo != null && stock > stockMaximo) return EstadoStock.EXCESO;
        return EstadoStock.NORMAL;
    }
}
```

### Nuevo Enum: EstadoStock

```java
public enum EstadoStock {
    CRITICO,   // stock = 0
    BAJO,      // 0 < stock < stockMinimo
    NORMAL,    // stockMinimo <= stock <= stockMaximo
    EXCESO     // stock > stockMaximo (opcional)
}
```

---

## 5. DTOs

### FincaProductoDto (Modificado)

```java
public class FincaProductoDto {
    // Campos existentes...

    private Integer stockMinimo;
    private Integer stockMaximo;
    private EstadoStock estadoStock;  // Calculado
}
```

### AlertaStockDto (Nuevo)

```java
public class AlertaStockDto {
    private UUID fincaProductoId;
    private String fincaCode;
    private String fincaName;
    private String productoCode;
    private String productoName;
    private String unidadMedida;
    private Integer stockActual;
    private Integer stockMinimo;
    private Integer deficit;  // stockMinimo - stockActual
    private EstadoStock estado;
}
```

### ResumenAlertasDto (Nuevo)

```java
public class ResumenAlertasDto {
    private Integer totalProductos;
    private Integer productosCriticos;  // stock = 0
    private Integer productosBajos;     // 0 < stock < minimo
    private Integer productosNormales;
    private List<AlertaStockDto> alertas;
}
```

---

## 6. API

### Obtener Alertas de Stock
```
GET /api/finca-producto/alertas
Query params:
  - fincaId (opcional): filtrar por finca
  - estado (opcional): CRITICO, BAJO, TODOS
  - limit (opcional): máximo de resultados (default: 50)

Response: {
  totalProductos: 150,
  productosCriticos: 3,
  productosBajos: 12,
  productosNormales: 135,
  alertas: [
    {
      fincaProductoId: "uuid",
      fincaCode: "0003",
      fincaName: "Managua",
      productoCode: "P001",
      productoName: "Fertilizante NPK",
      unidadMedida: "KG",
      stockActual: 5,
      stockMinimo: 50,
      deficit: 45,
      estado: "BAJO"
    },
    ...
  ]
}
```

### Actualizar Stock Mínimo
```
PATCH /api/finca-producto/{id}/stock-minimo
Body: {
  stockMinimo: 50,
  stockMaximo: 200  // opcional
}
Response: FincaProductoDto
```

### Actualización Masiva
```
POST /api/finca-producto/stock-minimo/bulk
Body: {
  actualizaciones: [
    { fincaProductoId: "uuid1", stockMinimo: 50 },
    { fincaProductoId: "uuid2", stockMinimo: 100 },
    ...
  ]
}
Response: {
  actualizados: 15,
  errores: []
}
```

---

## 7. Componentes Vue

### Badge de Estado (Reutilizable)

```vue
<!-- StockBadge.vue -->
<template>
  <span :class="['stock-badge', estadoClass]">
    <span v-if="estado === 'CRITICO'" class="icon">⚠️</span>
    <span v-else-if="estado === 'BAJO'" class="icon">⬇️</span>
    {{ stock }} {{ unidad }}
  </span>
</template>

<style scoped>
.stock-badge.critico {
  background: #ffebee;
  color: #c62828;
  border: 1px solid #ef5350;
}
.stock-badge.bajo {
  background: #fff3e0;
  color: #e65100;
  border: 1px solid #ff9800;
}
.stock-badge.normal {
  background: #e8f5e9;
  color: #2e7d32;
}
</style>
```

### Dashboard de Alertas

```vue
<!-- AlertasStock.vue -->
<template>
  <div class="alertas-stock">
    <h2>Alertas de Stock</h2>

    <!-- Resumen -->
    <div class="resumen-cards">
      <div class="card critico">
        <div class="numero">{{ resumen.productosCriticos }}</div>
        <div class="label">Críticos (sin stock)</div>
      </div>
      <div class="card bajo">
        <div class="numero">{{ resumen.productosBajos }}</div>
        <div class="label">Stock Bajo</div>
      </div>
      <div class="card normal">
        <div class="numero">{{ resumen.productosNormales }}</div>
        <div class="label">Normales</div>
      </div>
    </div>

    <!-- Filtros -->
    <div class="filtros">
      <select v-model="filtroFinca">
        <option value="">Todas las fincas</option>
        <option v-for="f in fincas" :value="f.id">{{ f.name }}</option>
      </select>
      <select v-model="filtroEstado">
        <option value="">Todos</option>
        <option value="CRITICO">Solo críticos</option>
        <option value="BAJO">Solo bajos</option>
      </select>
    </div>

    <!-- Lista de alertas -->
    <table class="alertas-table">
      <thead>
        <tr>
          <th>Finca</th>
          <th>Producto</th>
          <th>Stock Actual</th>
          <th>Stock Mínimo</th>
          <th>Déficit</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="alerta in alertas" :class="alerta.estado.toLowerCase()">
          <td>{{ alerta.fincaName }}</td>
          <td>{{ alerta.productoCode }} - {{ alerta.productoName }}</td>
          <td>
            <StockBadge :stock="alerta.stockActual" :estado="alerta.estado" />
          </td>
          <td>{{ alerta.stockMinimo }}</td>
          <td class="deficit">-{{ alerta.deficit }}</td>
          <td>
            <button @click="crearEntrada(alerta)">+ Entrada</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
```

### Modificar FincaProductoList.vue
- Agregar columna "Estado" con StockBadge
- Agregar columna "Stock Mínimo" editable inline
- Link a dashboard de alertas

---

## 8. Servicio

### IFincaProductoService (Modificado)

```java
public interface IFincaProductoService {
    // Existentes...

    // Nuevos
    ResumenAlertasDto getAlertasStock(UUID fincaId, EstadoStock estado, int limit);
    FincaProductoDto actualizarStockMinimo(UUID id, Integer stockMinimo, Integer stockMaximo);
    int actualizarStockMinimoBulk(List<ActualizacionStockMinimoDto> actualizaciones);
}
```

### Query Optimizada

```sql
SELECT
    fp.id,
    f.code as finca_code,
    f.name as finca_name,
    p.code as producto_code,
    p.name as producto_name,
    p.unidad_medida,
    fp.stock,
    fp.stock_minimo,
    (fp.stock_minimo - fp.stock) as deficit,
    CASE
        WHEN fp.stock = 0 THEN 'CRITICO'
        WHEN fp.stock < fp.stock_minimo THEN 'BAJO'
        ELSE 'NORMAL'
    END as estado
FROM finca_producto fp
JOIN finca f ON f.id = fp.finca_id
JOIN producto p ON p.id = fp.producto_id
WHERE fp.stock < fp.stock_minimo
  AND fp.activo = true
ORDER BY
    CASE WHEN fp.stock = 0 THEN 0 ELSE 1 END,
    (fp.stock_minimo - fp.stock) DESC
LIMIT :limit;
```

---

## 9. Integración con Movimientos

Después de cada movimiento de stock, verificar si el nuevo stock cayó bajo el mínimo:

```java
@Service
public class MovimientoStockServiceImpl {

    @Transactional
    public MovimientoStockDto registrar(MovimientoStockDto dto) {
        // Lógica existente de registro...

        // Nueva verificación
        FincaProducto fp = fincaProductoRepository.findById(dto.getFincaProductoId());
        if (fp.getStock() < fp.getStockMinimo()) {
            // Log de alerta o evento
            log.warn("ALERTA: Producto {} en finca {} bajo stock mínimo. Actual: {}, Mínimo: {}",
                fp.getProducto().getName(),
                fp.getFinca().getName(),
                fp.getStock(),
                fp.getStockMinimo()
            );
            // Futuro: Emitir evento para notificaciones
            // eventPublisher.publish(new StockBajoEvent(fp));
        }

        return movimiento;
    }
}
```

---

## 10. Migración

```sql
-- Agregar columnas
ALTER TABLE finca_producto
ADD COLUMN stock_minimo INTEGER NOT NULL DEFAULT 0,
ADD COLUMN stock_maximo INTEGER;

-- Opcional: Establecer mínimos por defecto basados en consumo histórico
-- (Esto requiere análisis de datos previo)
```

---

## 11. Criterios de Aceptación

- [ ] FincaProducto tiene campo stockMinimo editable
- [ ] Endpoint de alertas retorna productos con stock < mínimo
- [ ] Dashboard muestra resumen y lista de alertas
- [ ] Badge visual indica estado en lista de productos
- [ ] Productos críticos (stock=0) aparecen primero
- [ ] Botón de acción rápida para crear entrada de stock
- [ ] Actualización masiva de stock mínimo funciona

---

## 12. Dependencias

- Ninguna externa
- Modificar: `FincaProducto`, `FincaProductoService`, `FincaProductoController`
- Nuevo componente: `AlertasStock.vue`, `StockBadge.vue`

---

## 13. Extensiones Futuras

| Feature | Descripción |
|---------|-------------|
| Notificaciones email | Enviar alerta cuando stock cae bajo mínimo |
| Stock máximo | Alertar sobrestock |
| Predicción | Estimar días hasta agotamiento basado en consumo |
| Sugerencias de compra | Calcular cantidad óptima a reponer |
