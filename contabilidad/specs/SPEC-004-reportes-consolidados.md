# SPEC-004: Reportes Consolidados

## Metadata
- **ID**: SPEC-004
- **Módulo**: Reportes
- **Prioridad**: MEDIA
- **Estado**: Propuesto
- **Fecha**: 2026-08-22

---

## 1. Problema

El sistema carece de reportes consolidados para toma de decisiones:
- No hay reporte de facturación mensual
- No hay vista de deudas pendientes consolidada
- No hay kardex multi-producto
- No hay gráficos de movimientos

### Impacto
- Dificultad para análisis financiero
- Imposible cerrar períodos contables
- Gestión basada en intuición, no en datos

---

## 2. Reportes Propuestos

### Reporte 1: Facturación Mensual
### Reporte 2: Deudas Pendientes
### Reporte 3: Kardex Consolidado
### Reporte 4: Movimientos por Tipo (Gráfico)

---

## 3. REPORTE 1: Facturación Mensual

### Descripción
Resumen de todos los vales y facturas emitidos en un período.

### Requisitos Funcionales
- Filtros: Finca, Rango de fechas, Tipo (Vale/Factura/Todos)
- Agrupación: Por finca, por tipo, por destino
- Totales: Cantidad de documentos, cantidad total de productos, valor total

### Endpoint
```
GET /api/reportes/facturacion
Query params:
  - fincaId (opcional)
  - fechaInicio (requerido)
  - fechaFin (requerido)
  - tipo (opcional): VALE, FACTURA
  - agruparPor: FINCA, TIPO, DESTINO

Response: {
  periodo: { inicio: "2026-08-01", fin: "2026-08-31" },
  filtros: { finca: "Managua", tipo: "TODOS" },
  resumen: {
    totalDocumentos: 45,
    totalVales: 30,
    totalFacturas: 15,
    cantidadProductos: 1250,
    valorTotal: 15000.00
  },
  detalle: [
    {
      finca: "Managua",
      tipo: "VALE",
      destino: "TRABAJADORES",
      documentos: 20,
      cantidad: 500,
      valor: 5000.00
    },
    ...
  ],
  documentos: [
    {
      numero: "VALE-2026-00001",
      fecha: "2026-08-05",
      producto: "Arroz",
      cantidad: 50,
      destino: "TRABAJADORES"
    },
    ...
  ]
}
```

### Vista UI
```
┌─────────────────────────────────────────────────────────────┐
│  REPORTE DE FACTURACIÓN                                     │
│  Período: 01/08/2026 - 31/08/2026                          │
├─────────────────────────────────────────────────────────────┤
│  Finca: [Todas ▼]  Tipo: [Todos ▼]  [🔍 Generar]           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │    45    │  │    30    │  │    15    │  │ $15,000  │   │
│  │Documentos│  │  Vales   │  │ Facturas │  │  Total   │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
│                                                             │
│  Detalle por Destino                                        │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ Destino      │ Docs │ Cantidad │ Valor    │ %      │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ Trabajadores │  20  │   500    │ $5,000   │ 33%    │   │
│  │ Comedor      │  10  │   300    │ $3,000   │ 20%    │   │
│  │ Población    │  15  │   450    │ $7,000   │ 47%    │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  [📥 Exportar Excel]  [📄 Exportar PDF]                    │
└─────────────────────────────────────────────────────────────┘
```

---

## 4. REPORTE 2: Deudas Pendientes

### Descripción
Lista de trabajadores con deuda > 0, ordenados por monto.

### Requisitos Funcionales
- Filtros: Finca, Rango de monto
- Incluir: Total de deuda por finca, promedio, trabajadores sin deuda
- Detalle: Últimos pagos de cada trabajador

### Endpoint
```
GET /api/reportes/deudas-pendientes
Query params:
  - fincaId (opcional)
  - montoMinimo (opcional)
  - montoMaximo (opcional)
  - incluirHistorial (boolean, default false)

Response: {
  fecha: "2026-08-22",
  resumen: {
    totalTrabajadores: 125,
    trabajadoresConDeuda: 45,
    trabajadoresSinDeuda: 80,
    montoTotalDeuda: 25000.00,
    promedioDeuda: 555.55,
    deudaMaxima: 2500.00,
    deudaMinima: 50.00
  },
  porFinca: [
    {
      finca: "Managua",
      trabajadoresConDeuda: 30,
      montoTotal: 18000.00
    },
    ...
  ],
  deudas: [
    {
      trabajador: { id, nombre, ruc },
      finca: "Managua",
      monto: 2500.00,
      ultimoPago: { fecha: "2026-08-15", monto: 100.00 }
    },
    ...
  ]
}
```

### Vista UI
```
┌─────────────────────────────────────────────────────────────┐
│  DEUDAS PENDIENTES                                          │
│  Fecha: 22/08/2026                                          │
├─────────────────────────────────────────────────────────────┤
│  Finca: [Todas ▼]  Monto mín: [____]  Monto máx: [____]    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │    45    │  │ $25,000  │  │  $555    │  │  $2,500  │   │
│  │Con Deuda │  │  Total   │  │ Promedio │  │  Máxima  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
│                                                             │
│  Lista de Deudores                                          │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ # │ Trabajador      │ RUC        │ Finca   │ Deuda  │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ 1 │ Juan Pérez      │ 1234567890 │ Managua │ $2,500 │   │
│  │ 2 │ María García    │ 0987654321 │ Managua │ $1,800 │   │
│  │ 3 │ Carlos López    │ 1122334455 │ Lorenzo │ $1,200 │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  [📥 Exportar Excel]  [📄 Exportar PDF]                    │
└─────────────────────────────────────────────────────────────┘
```

---

## 5. REPORTE 3: Kardex Consolidado

### Descripción
Kardex de múltiples productos en un solo reporte.

### Requisitos Funcionales
- Filtros: Finca, Productos (multi-select), Rango de fechas
- Mostrar: Stock inicial, entradas, salidas, stock final por producto
- Desglose: Por tipo de movimiento

### Endpoint
```
GET /api/reportes/kardex-consolidado
Query params:
  - fincaId (requerido)
  - fechaInicio (requerido)
  - fechaFin (requerido)
  - productoIds (opcional, CSV de UUIDs)

Response: {
  finca: { id, code, name },
  periodo: { inicio, fin },
  productos: [
    {
      producto: { id, code, name, unidad },
      stockInicial: 100,
      entradas: {
        total: 50,
        porTipo: {
          ENTRADA_PRODUCCION: 30,
          ENTRADA_FACTURA: 20
        }
      },
      salidas: {
        total: 80,
        porTipo: {
          SALIDA_VENTA: 50,
          SALIDA_AUTOCONSUMO: 30
        }
      },
      stockFinal: 70,
      movimientos: [
        { fecha, tipo, cantidad, stockResultante, referencia }
      ]
    }
  ],
  totales: {
    stockInicialTotal: 500,
    entradasTotal: 200,
    salidasTotal: 350,
    stockFinalTotal: 350
  }
}
```

---

## 6. REPORTE 4: Movimientos por Tipo (Gráfico)

### Descripción
Visualización gráfica de entradas vs salidas en el tiempo.

### Requisitos Funcionales
- Filtros: Finca, Producto, Rango de fechas, Granularidad (día/semana/mes)
- Gráfico de barras o líneas
- Comparación entradas vs salidas

### Endpoint
```
GET /api/reportes/movimientos-grafico
Query params:
  - fincaId (opcional)
  - productoId (opcional)
  - fechaInicio (requerido)
  - fechaFin (requerido)
  - granularidad: DIA, SEMANA, MES

Response: {
  etiquetas: ["01/08", "02/08", "03/08", ...],
  series: [
    {
      nombre: "Entradas",
      color: "#27ae60",
      datos: [50, 30, 0, 80, ...]
    },
    {
      nombre: "Salidas",
      color: "#e74c3c",
      datos: [20, 45, 60, 10, ...]
    }
  ],
  totales: {
    entradas: 500,
    salidas: 420,
    balance: 80
  }
}
```

### Vista UI (usando Chart.js o similar)
```
┌─────────────────────────────────────────────────────────────┐
│  MOVIMIENTOS DE STOCK                                       │
├─────────────────────────────────────────────────────────────┤
│  Finca: [Managua ▼]  Producto: [Todos ▼]  Mes: [Agosto ▼]  │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Entradas: 500 ██████████                                   │
│  Salidas:  420 ████████                                     │
│  Balance:  +80                                              │
│                                                             │
│     ▲                                                       │
│  80 │    ██                          ██                     │
│  60 │    ██  ░░      ░░              ██                     │
│  40 │ ██ ██  ░░  ░░  ░░  ██      ██  ██                     │
│  20 │ ██ ██  ░░  ░░  ░░  ██  ██  ██  ██                     │
│   0 └─────────────────────────────────────▶                 │
│       L  M  M  J  V  S  D  L  M  M  J                       │
│                                                             │
│  ██ Entradas   ░░ Salidas                                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 7. Controlador de Reportes

```java
@RestController
@RequestMapping("/api/reportes")
public class ReportesController {

    @GetMapping("/facturacion")
    public ResponseEntity<ReporteFacturacionDto> getFacturacion(
        @RequestParam(required = false) UUID fincaId,
        @RequestParam LocalDate fechaInicio,
        @RequestParam LocalDate fechaFin,
        @RequestParam(required = false) TipoSalida tipo,
        @RequestParam(defaultValue = "FINCA") String agruparPor
    );

    @GetMapping("/deudas-pendientes")
    public ResponseEntity<ReporteDeudasDto> getDeudasPendientes(
        @RequestParam(required = false) UUID fincaId,
        @RequestParam(required = false) Double montoMinimo,
        @RequestParam(required = false) Double montoMaximo
    );

    @GetMapping("/kardex-consolidado")
    public ResponseEntity<ReporteKardexDto> getKardexConsolidado(
        @RequestParam UUID fincaId,
        @RequestParam LocalDate fechaInicio,
        @RequestParam LocalDate fechaFin,
        @RequestParam(required = false) List<UUID> productoIds
    );

    @GetMapping("/movimientos-grafico")
    public ResponseEntity<ReporteMovimientosGraficoDto> getMovimientosGrafico(
        @RequestParam(required = false) UUID fincaId,
        @RequestParam(required = false) UUID productoId,
        @RequestParam LocalDate fechaInicio,
        @RequestParam LocalDate fechaFin,
        @RequestParam(defaultValue = "DIA") Granularidad granularidad
    );

    // Exportaciones
    @GetMapping("/facturacion/excel")
    public ResponseEntity<byte[]> exportFacturacionExcel(...);

    @GetMapping("/facturacion/pdf")
    public ResponseEntity<byte[]> exportFacturacionPdf(...);

    @GetMapping("/deudas-pendientes/excel")
    public ResponseEntity<byte[]> exportDeudasExcel(...);
}
```

---

## 8. Componentes Vue

### Nuevo: ReportesView.vue
- Menú de reportes disponibles
- Navegación a cada reporte específico

### Nuevo: ReporteFacturacion.vue
### Nuevo: ReporteDeudasPendientes.vue
### Nuevo: ReporteKardex.vue
### Nuevo: ReporteMovimientos.vue (con Chart.js)

### Servicios TypeScript
```typescript
// ReportesService.ts
class ReportesService {
  getFacturacion(params: FacturacionParams): Promise<ReporteFacturacion>
  getDeudasPendientes(params: DeudasParams): Promise<ReporteDeudas>
  getKardexConsolidado(params: KardexParams): Promise<ReporteKardex>
  getMovimientosGrafico(params: GraficoParams): Promise<ReporteGrafico>

  exportFacturacionExcel(params): Promise<Blob>
  exportFacturacionPdf(params): Promise<Blob>
  exportDeudasExcel(params): Promise<Blob>
}
```

---

## 9. Dependencias

- Chart.js o ApexCharts para gráficos
- Apache POI para exportación Excel (ya existe)
- PDFBox para exportación PDF (ya existe)

---

## 10. Criterios de Aceptación

### Reporte Facturación
- [ ] Filtros funcionan correctamente
- [ ] Totales coinciden con suma de detalles
- [ ] Exportación Excel descarga archivo válido
- [ ] Exportación PDF genera documento legible

### Reporte Deudas
- [ ] Lista ordenada por monto descendente
- [ ] Totales y promedios calculados correctamente
- [ ] Filtro por finca funciona
- [ ] Exportaciones funcionan

### Kardex Consolidado
- [ ] Stock final = stock inicial + entradas - salidas
- [ ] Desglose por tipo de movimiento correcto
- [ ] Multi-producto funciona

### Gráfico Movimientos
- [ ] Gráfico renderiza correctamente
- [ ] Granularidad cambia visualización
- [ ] Tooltips muestran valores

---

## 11. Priorización de Implementación

| Reporte | Prioridad | Razón |
|---------|-----------|-------|
| Deudas Pendientes | 1 | Crítico para gestión de pagos |
| Facturación | 2 | Necesario para cierre mensual |
| Kardex Consolidado | 3 | Útil para inventario |
| Gráfico Movimientos | 4 | Nice-to-have, visual |
