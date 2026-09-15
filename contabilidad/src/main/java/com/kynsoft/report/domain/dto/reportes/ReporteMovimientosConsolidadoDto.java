package com.kynsoft.report.domain.dto.reportes;

import com.kynsoft.report.domain.dto.DestinoSalida;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO para el reporte consolidado de movimientos de stock.
 * Agrupa entradas por producto y salidas por destino en un rango de fechas.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteMovimientosConsolidadoDto {

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    // Totales generales
    private Double totalEntradas;
    private Double totalSalidas;

    // Entradas agrupadas por producto
    private List<EntradaPorProducto> entradasPorProducto;

    // Salidas agrupadas por destino
    private List<SalidaPorDestino> salidasPorDestino;

    // Detalle de entradas por tipo de movimiento
    private Map<TipoMovimientoStock, Double> entradasPorTipo;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntradaPorProducto {
        private String productoCode;
        private String productoName;
        private String unidadMedida;
        private Double cantidadTotal;
        private List<EntradaDetalle> detalles;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntradaDetalle {
        private TipoMovimientoStock tipo;
        private Double cantidad;
        private String descripcion;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalidaPorDestino {
        private DestinoSalida destino;
        private String destinoNombre;
        private Double cantidadTotal;
        private Double valorTotal;
        private List<SalidaProductoDetalle> productos;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalidaProductoDetalle {
        private String productoCode;
        private String productoName;
        private Double cantidad;
        private Double precio;
        private Double valorTotal;
    }
}
