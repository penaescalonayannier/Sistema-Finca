package com.kynsoft.report.domain.dto.reportes;

import com.kynsoft.report.domain.dto.DestinoSalida;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    /** Cobros confirmados por pagos activos con forma de pago EFECTIVO. */
    private Double totalEfectivoCobrado;

    /** Cobros confirmados por pagos activos con forma de pago TRANSFERENCIA. */
    private Double totalTransferenciasCobradas;

    /** Renglones de cobros confirmados en efectivo; su fuente es el detalle PAGO. */
    private List<CobroEfectivo> cobrosEfectivo;

    /**
     * Vales y facturas emitidos en el período. No se les asigna cobro de forma
     * individual porque el modelo actual no conserva una relación pago-documento.
     */
    private List<DocumentoOrigenEmitido> documentosOrigenEmitidos;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CobroEfectivo {
        private java.util.UUID pagoDetalleId;
        private LocalDateTime fecha;
        private String numeroRecibo;
        private String trabajadorNombre;
        private String fincaNombre;
        private String tipoDocumento;
        private String numeroDocumento;
        private String destino;
        private Double saldoDocumento;
        private String estadoDocumento;
        private Double importe;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentoOrigenEmitido {
        private java.util.UUID salidaId;
        private LocalDateTime fecha;
        private String tipoDocumento;
        private String numeroDocumento;
        private String destino;
        private String fincaNombre;
        private Double cantidad;
        private Double importeDocumentado;
        private String estadoCobro;
    }

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
