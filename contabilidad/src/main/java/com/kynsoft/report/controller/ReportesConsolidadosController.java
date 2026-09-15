package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.TipoSalida;
import com.kynsoft.report.domain.dto.reportes.*;
import com.kynsoft.report.domain.services.IReportesConsolidadosService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reportes")
public class ReportesConsolidadosController {

    private final IReportesConsolidadosService reportesService;

    public ReportesConsolidadosController(IReportesConsolidadosService reportesService) {
        this.reportesService = reportesService;
    }

    @GetMapping("/deudas-pendientes")
    public ResponseEntity<ReporteDeudasPendientesDto> getDeudasPendientes(
            @RequestParam(required = false) UUID fincaId,
            @RequestParam(required = false) Double montoMinimo,
            @RequestParam(required = false) Double montoMaximo,
            @RequestParam(defaultValue = "false") boolean incluirHistorial) {

        ReporteDeudasPendientesDto reporte = reportesService.getDeudasPendientes(
                fincaId, montoMinimo, montoMaximo, incluirHistorial);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/facturacion")
    public ResponseEntity<ReporteFacturacionDto> getFacturacion(
            @RequestParam(required = false) UUID fincaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) TipoSalida tipo,
            @RequestParam(defaultValue = "FINCA") String agruparPor) {

        ReporteFacturacionDto reporte = reportesService.getFacturacion(
                fincaId, fechaInicio, fechaFin, tipo, agruparPor);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/kardex-consolidado")
    public ResponseEntity<ReporteKardexDto> getKardexConsolidado(
            @RequestParam UUID fincaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) List<UUID> productoIds) {

        ReporteKardexDto reporte = reportesService.getKardexConsolidado(
                fincaId, fechaInicio, fechaFin, productoIds);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/movimientos-grafico")
    public ResponseEntity<ReporteMovimientosGraficoDto> getMovimientosGrafico(
            @RequestParam(required = false) UUID fincaId,
            @RequestParam(required = false) UUID productoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(defaultValue = "DIA") Granularidad granularidad) {

        ReporteMovimientosGraficoDto reporte = reportesService.getMovimientosGrafico(
                fincaId, productoId, fechaInicio, fechaFin, granularidad);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/pagos")
    public ResponseEntity<ResumenPagosDto> getResumenPagos(
            @RequestParam(required = false) UUID fincaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        ResumenPagosDto reporte = reportesService.getResumenPagos(fincaId, fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/ventas")
    public ResponseEntity<ResumenVentasDto> getResumenVentas(
            @RequestParam(required = false) UUID fincaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        ResumenVentasDto reporte = reportesService.getResumenVentas(fincaId, fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }
}
