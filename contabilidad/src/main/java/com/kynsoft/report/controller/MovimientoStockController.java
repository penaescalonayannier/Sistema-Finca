package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.BalanceProductoDto;
import com.kynsoft.report.domain.dto.KardexDto;
import com.kynsoft.report.domain.dto.MovimientoStockDto;
import com.kynsoft.report.domain.dto.ResumenMovimientosDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.dto.reportes.ReporteMovimientosConsolidadoDto;
import com.kynsoft.report.domain.services.IMovimientoStockService;
import com.kynsoft.report.infrastructure.services.MovimientoStockPdfService;
import com.kynsoft.report.infrastructure.services.TarjetaEstibaPdfService;
import com.kynsoft.report.infrastructure.services.TarjetaEstibaFincaPdfService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/movimiento-stock")
public class MovimientoStockController {

    private final IMovimientoStockService movimientoStockService;
    private final MovimientoStockPdfService movimientoStockPdfService;
    private final TarjetaEstibaPdfService tarjetaEstibaPdfService;
    private final TarjetaEstibaFincaPdfService tarjetaEstibaFincaPdfService;

    public MovimientoStockController(IMovimientoStockService movimientoStockService,
                                     MovimientoStockPdfService movimientoStockPdfService,
                                     TarjetaEstibaPdfService tarjetaEstibaPdfService,
                                     TarjetaEstibaFincaPdfService tarjetaEstibaFincaPdfService) {
        this.movimientoStockService = movimientoStockService;
        this.movimientoStockPdfService = movimientoStockPdfService;
        this.tarjetaEstibaPdfService = tarjetaEstibaPdfService;
        this.tarjetaEstibaFincaPdfService = tarjetaEstibaFincaPdfService;
    }

    @PostMapping("/ajuste")
    public ResponseEntity<?> crearAjuste(@RequestBody CreateAjusteRequest request) {
        MovimientoStockDto result = movimientoStockService.crearAjuste(
                request.getAlmacenId(),
                request.getFincaProductoId(),
                request.getTipoMovimiento(),
                request.getCantidad(),
                request.getObservaciones()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("id", result.getId());
        response.put("stockAnterior", result.getStockAnterior());
        response.put("stockNuevo", result.getStockNuevo());

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene reporte consolidado de movimientos por rango de fechas.
     * Agrupa entradas por producto y salidas por destino (TRABAJADORES, COMEDOR, VENTA_ESTADO, etc.)
     *
     * @param fechaInicio Fecha inicio del rango (formato: yyyy-MM-dd)
     * @param fechaFin Fecha fin del rango (formato: yyyy-MM-dd)
     * @param fincaId Opcional: filtrar por finca específica
     * @return Reporte consolidado con entradas por producto y salidas por destino
     */
    @GetMapping("/consolidado")
    public ResponseEntity<ReporteMovimientosConsolidadoDto> getConsolidado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) UUID fincaId) {

        ReporteMovimientosConsolidadoDto consolidado = movimientoStockService.getConsolidadoMovimientos(
                fechaInicio, fechaFin, fincaId);
        return ResponseEntity.ok(consolidado);
    }

    @GetMapping(value = "/consolidado/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarConsolidadoPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) UUID fincaId) throws Exception {
        ReporteMovimientosConsolidadoDto consolidado = movimientoStockService.getConsolidadoMovimientos(
                fechaInicio, fechaFin, fincaId);
        byte[] pdf = movimientoStockPdfService.generar(consolidado);
        String filename = "Reporte_movimientos_por_destino_" + fechaInicio + "_" + fechaFin + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoStockDto> findById(@PathVariable UUID id) {
        MovimientoStockDto response = movimientoStockService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        PaginatedResponse response = movimientoStockService.search(pageable, request.getFilter());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/finca-producto/{fincaProductoId}")
    public ResponseEntity<List<MovimientoStockDto>> findByFincaProductoId(@PathVariable UUID fincaProductoId) {
        List<MovimientoStockDto> response = movimientoStockService.findByFincaProductoId(fincaProductoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/por-almacen/{almacenId}")
    public ResponseEntity<?> findByAlmacenId(
            @PathVariable UUID almacenId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<MovimientoStockDto> movimientos;
        if (fechaInicio != null && fechaFin != null) {
            movimientos = movimientoStockService.findByAlmacenIdAndFechaBetween(almacenId, fechaInicio, fechaFin);
        } else {
            movimientos = movimientoStockService.findByAlmacenId(almacenId);
        }

        double totalEntradas = movimientos.stream()
                .filter(m -> m.getTipo().isEntrada())
                .mapToDouble(m -> Math.abs(m.getCantidad() == null ? 0.0 : m.getCantidad()))
                .sum();
        double totalSalidas = movimientos.stream()
                .filter(m -> m.getTipo().isSalida())
                .mapToDouble(m -> Math.abs(m.getCantidad() == null ? 0.0 : m.getCantidad()))
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("content", movimientos);
        response.put("totalElements", movimientos.size());
        response.put("totalEntradas", totalEntradas);
        response.put("totalSalidas", totalSalidas);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/por-producto/{fincaProductoId}")
    public ResponseEntity<?> findByProducto(
            @PathVariable UUID fincaProductoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<MovimientoStockDto> movimientos = movimientoStockService.findByFincaProductoId(fincaProductoId);

        // Filtrar por fecha si se proporcionan
        if (fechaInicio != null && fechaFin != null) {
            final LocalDateTime inicio = fechaInicio;
            final LocalDateTime fin = fechaFin;
            movimientos = movimientos.stream()
                    .filter(m -> !m.getFecha().isBefore(inicio) && !m.getFecha().isAfter(fin))
                    .toList();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", movimientos);
        response.put("totalElements", movimientos.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/por-finca/{fincaId}")
    public ResponseEntity<?> findByFinca(
            @PathVariable UUID fincaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<MovimientoStockDto> movimientos;
        if (fechaInicio != null && fechaFin != null) {
            movimientos = movimientoStockService.findByFincaIdAndFechaBetween(fincaId, fechaInicio, fechaFin);
        } else {
            movimientos = movimientoStockService.findByFincaId(fincaId);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", movimientos);
        response.put("totalElements", movimientos.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/por-referencia/{referenciaId}")
    public ResponseEntity<?> findByReferencia(
            @PathVariable UUID referenciaId,
            @RequestParam(defaultValue = "") String referenciaTipo) {

        List<MovimientoStockDto> movimientos = movimientoStockService.findByReferencia(referenciaId, referenciaTipo);

        Map<String, Object> response = new HashMap<>();
        response.put("content", movimientos);
        response.put("totalElements", movimientos.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/por-tipo/{tipoMovimiento}")
    public ResponseEntity<?> findByTipo(
            @PathVariable TipoMovimientoStock tipoMovimiento,
            @RequestParam(required = false) UUID fincaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<MovimientoStockDto> movimientos;
        if (fincaId != null && fechaInicio != null && fechaFin != null) {
            movimientos = movimientoStockService.findByTipoAndFincaIdAndFechaBetween(tipoMovimiento, fincaId, fechaInicio, fechaFin);
        } else {
            movimientos = movimientoStockService.findByTipo(tipoMovimiento);
        }

        double totalCantidad = movimientos.stream()
                .mapToDouble(MovimientoStockDto::getCantidad)
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("content", movimientos);
        response.put("totalElements", movimientos.size());
        response.put("totalCantidad", totalCantidad);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenMovimientosDto> getResumen(
            @RequestParam UUID fincaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        ResumenMovimientosDto resumen = movimientoStockService.getResumen(fincaId, fechaInicio, fechaFin);
        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/balance-producto")
    public ResponseEntity<?> getBalanceProducto(
            @RequestParam UUID fincaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<BalanceProductoDto> balances = movimientoStockService.getBalanceProducto(fincaId, fechaInicio, fechaFin);

        Map<String, Object> response = new HashMap<>();
        response.put("content", balances);
        response.put("totalElements", balances.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/kardex/{fincaProductoId}")
    public ResponseEntity<KardexDto> getKardex(
            @PathVariable UUID fincaProductoId,
            @RequestParam(required = false) UUID almacenId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        KardexDto kardex = movimientoStockService.getKardex(fincaProductoId, almacenId, fechaInicio, fechaFin);
        return ResponseEntity.ok(kardex);
    }

    /**
     * Descarga el modelo SC-2-14 (Tarjeta de Estiba) para un único producto
     * asignado a un almacén. Es una consulta histórica y no genera movimientos.
     */
    @GetMapping(value = "/tarjeta-estiba/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarTarjetaEstiba(
            @RequestParam UUID fincaProductoId,
            @RequestParam UUID almacenId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) throws Exception {
        byte[] pdf = tarjetaEstibaPdfService.generar(fincaProductoId, almacenId, fechaInicio, fechaFin);
        String filename = "SC-2-14_tarjeta_estiba_" + fincaProductoId.toString().substring(0, 8)
                + "_" + fechaInicio + "_" + fechaFin + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    /** Tarjeta SC-2-14 consolidada del producto en todos los almacenes de una finca. */
    @GetMapping(value = "/tarjeta-estiba/finca/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarTarjetaEstibaFinca(
            @RequestParam UUID fincaProductoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) throws Exception {
        byte[] pdf = tarjetaEstibaFincaPdfService.generar(fincaProductoId, fechaInicio, fechaFin);
        String filename = "SC-2-14_tarjeta_estiba_finca_" + fincaProductoId.toString().substring(0, 8)
                + "_" + fechaInicio + "_" + fechaFin + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // Request class para crear ajuste
    @Getter
    @Setter
    public static class CreateAjusteRequest {
        private UUID almacenId;
        private UUID fincaProductoId;
        private TipoMovimientoStock tipoMovimiento;
        private Double cantidad;
        private String observaciones;
    }
}
