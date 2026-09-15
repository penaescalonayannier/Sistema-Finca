package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.diatrabajo.create.CreateDiaTrabajoCommand;
import com.kynsoft.report.applications.command.diatrabajo.create.CreateDiaTrabajoMessage;
import com.kynsoft.report.applications.command.diatrabajo.create.CreateDiaTrabajoRequest;
import com.kynsoft.report.applications.command.diatrabajo.delete.DeleteDiaTrabajoCommand;
import com.kynsoft.report.applications.command.diatrabajo.delete.DeleteDiaTrabajoMessage;
import com.kynsoft.report.applications.command.reporte.create.CreateReporteCommand;
import com.kynsoft.report.applications.command.reporte.create.CreateReporteMessage;
import com.kynsoft.report.applications.command.reporte.create.CreateReporteRequest;
import com.kynsoft.report.applications.command.reporte.delete.DeleteReporteCommand;
import com.kynsoft.report.applications.command.reporte.delete.DeleteReporteMessage;
import com.kynsoft.report.applications.command.reporte.generateConsolidadoPdf.GenerateConsolidadoPdfCommand;
import com.kynsoft.report.applications.command.reporte.generateConsolidadoPdf.GenerateConsolidadoPdfMessage;
import com.kynsoft.report.applications.command.reporte.generatePdf.GenerateReportePdfCommand;
import com.kynsoft.report.applications.command.reporte.generatePdf.GenerateReportePdfMessage;
import com.kynsoft.report.applications.command.reporte.update.UpdateReporteCommand;
import com.kynsoft.report.applications.command.reporte.update.UpdateReporteMessage;
import com.kynsoft.report.applications.command.reporte.update.UpdateReporteRequest;
import com.kynsoft.report.applications.query.diatrabajo.getByReporte.GetDiasByReporteQuery;
import com.kynsoft.report.applications.query.reporte.consolidado.GetReporteConsolidadoQuery;
import com.kynsoft.report.applications.query.reporte.consolidadoPorResponsable.GetReporteConsolidadoPorResponsableQuery;
import com.kynsoft.report.applications.query.reporte.getById.FindReporteByIdQuery;
import com.kynsoft.report.applications.query.reporte.search.GetSearchReporteQuery;
import com.kynsoft.report.applications.query.reporte.trabajadoresExcedidos.GetTrabajadoresConHorasExcedidasQuery;
import com.kynsoft.report.applications.query.metricas.ausentismo.GetAbsentismoQuery;
import com.kynsoft.report.applications.query.metricas.productividad.GetProductividadQuery;
import com.kynsoft.report.applications.query.metricas.rankings.GetRankingsQuery;
import com.kynsoft.report.applications.query.metricas.horasexcedidas.GetHorasExcedidasSummaryQuery;
import com.kynsoft.report.applications.query.responseObject.*;
import com.kynsoft.report.applications.query.responseObject.ReporteConsolidadoPorResponsableResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import com.kynsoft.report.domain.services.IReporteService;
import com.kynsoft.report.infrastructure.services.PrenominaExcelService;

@RestController
@RequestMapping("/api/reporte")
public class ReporteController {

    private final IMediator mediator;
    private final IReporteService reporteService;
    private final PrenominaExcelService prenominaExcelService;

    public ReporteController(IMediator mediator, IReporteService reporteService, PrenominaExcelService prenominaExcelService) {
        this.mediator = mediator;
        this.reporteService = reporteService;
        this.prenominaExcelService = prenominaExcelService;
    }

    // ==================== OBTENER PRÓXIMO CÓDIGO DISPONIBLE ====================
    @GetMapping("/next-codigo")
    public ResponseEntity<Map<String, String>> getNextCodigo(
            @RequestParam String year,
            @RequestParam String mes) {
        String codigo = reporteService.generateCodigo(year, mes);
        return ResponseEntity.ok(Map.of("codigo", codigo));
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateReporteRequest request) {
        CreateReporteCommand createCommand = CreateReporteCommand.fromRequest(request);
        CreateReporteMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteResponse> findById(@PathVariable UUID id) {
        FindReporteByIdQuery query = new FindReporteByIdQuery(id);
        ReporteResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateReporteRequest request) {
        UpdateReporteCommand updateCommand = UpdateReporteCommand.fromRequest(request, id);
        UpdateReporteMessage response = mediator.send(updateCommand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteReporteCommand deleteCommand = new DeleteReporteCommand(id);
        DeleteReporteMessage response = mediator.send(deleteCommand);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchReporteQuery query = new GetSearchReporteQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    // ==================== GENERAR PDF ====================
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdf(@PathVariable UUID id) {
        try {
            GenerateReportePdfCommand command = new GenerateReportePdfCommand(id);
            GenerateReportePdfMessage response = mediator.send(command);

            byte[] pdfBytes = response.getPdfData();

            if (pdfBytes == null || pdfBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(("Error: El PDF generado está vacío").getBytes());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_" + id + ".pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfBytes.length)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error: " + e.getMessage()).getBytes());
        }
    }

    // ==================== REPORTE CONSOLIDADO MENSUAL ====================
    @GetMapping("/consolidado")
    public ResponseEntity<ReporteConsolidadoResponse> getReporteConsolidado(
            @RequestParam String year,
            @RequestParam String mes) {
        GetReporteConsolidadoQuery query = new GetReporteConsolidadoQuery(year, mes);
        ReporteConsolidadoResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    // ==================== REPORTE CONSOLIDADO POR RESPONSABLE ====================
    @GetMapping("/consolidado-por-responsable")
    public ResponseEntity<ReporteConsolidadoPorResponsableResponse> getReporteConsolidadoPorResponsable(
            @RequestParam String year,
            @RequestParam String mes) {
        GetReporteConsolidadoPorResponsableQuery query = new GetReporteConsolidadoPorResponsableQuery(year, mes);
        ReporteConsolidadoPorResponsableResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    // ==================== EXPORTAR CONSOLIDADO A PDF ====================
    @GetMapping(value = "/consolidado/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportConsolidadoPdf(
            @RequestParam String year,
            @RequestParam String mes) {
        try {
            GenerateConsolidadoPdfCommand command = new GenerateConsolidadoPdfCommand(year, mes);
            GenerateConsolidadoPdfMessage response = mediator.send(command);
            
            byte[] pdfBytes = response.getPdfData();
            
            if (pdfBytes == null || pdfBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(("Error: El PDF generado está vacío").getBytes());
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=reporte_consolidado_" + year + "_" + mes + ".pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfBytes.length)
                    .body(pdfBytes);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error: " + e.getMessage()).getBytes());
        }
    }

    // ==================== EXPORTAR CONSOLIDADO POR RESPONSABLE A PDF ====================
    @GetMapping(value = "/consolidado-por-responsable/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportConsolidadoPorResponsablePdf(
            @RequestParam String year,
            @RequestParam String mes) {
        try {
            com.kynsoft.report.applications.command.reporte.generateConsolidadoPorResponsablePdf.GenerateConsolidadoPorResponsablePdfCommand command =
                    new com.kynsoft.report.applications.command.reporte.generateConsolidadoPorResponsablePdf.GenerateConsolidadoPorResponsablePdfCommand(year, mes);
            com.kynsoft.report.applications.command.reporte.generateConsolidadoPorResponsablePdf.GenerateConsolidadoPorResponsablePdfMessage response = mediator.send(command);

            byte[] pdfBytes = response.getPdfData();

            if (pdfBytes == null || pdfBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(("Error: El PDF generado está vacío").getBytes());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=reporte_consolidado_responsable_" + year + "_" + mes + ".pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfBytes.length)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error: " + e.getMessage()).getBytes());
        }
    }

    // ==================== TRABAJADORES CON HORAS EXCEDIDAS ====================
    @GetMapping("/consolidado/trabajadores-excedidos")
    public ResponseEntity<TrabajadorHorasExcedidasListResponse> getTrabajadoresConHorasExcedidas(
            @RequestParam String year,
            @RequestParam String mes) {
        GetTrabajadoresConHorasExcedidasQuery query = new GetTrabajadoresConHorasExcedidasQuery(year, mes);
        TrabajadorHorasExcedidasListResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reporteId}/dias")
    public ResponseEntity<CreateDiaTrabajoMessage> agregarDia(
            @PathVariable UUID reporteId,
            @RequestBody CreateDiaTrabajoRequest request) {
        CreateDiaTrabajoCommand command = CreateDiaTrabajoCommand.fromRequest(request, reporteId);
        CreateDiaTrabajoMessage response = mediator.send(command);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{reporteId}/dias")
    public ResponseEntity<DiaTrabajoListResponse> getDiasByReporte(@PathVariable UUID reporteId) {
        GetDiasByReporteQuery query = new GetDiasByReporteQuery(reporteId);
        DiaTrabajoListResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/dias/{diaId}")
    public ResponseEntity<DeleteDiaTrabajoMessage> eliminarDia(@PathVariable UUID diaId) {
        DeleteDiaTrabajoCommand command = new DeleteDiaTrabajoCommand(diaId);
        DeleteDiaTrabajoMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    // ==================== REPORTES ESTRATÉGICOS PARA GERENCIA ====================

    @GetMapping("/metricas/ausentismo")
    public ResponseEntity<AbsentismoListResponse> getAbsentismo(
            @RequestParam String year,
            @RequestParam String mes,
            @RequestParam(required = false) UUID trabajadorId) {
        GetAbsentismoQuery query = new GetAbsentismoQuery(year, mes, trabajadorId);
        AbsentismoListResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/metricas/productividad")
    public ResponseEntity<ProductividadListResponse> getProductividad(
            @RequestParam String year,
            @RequestParam String mes,
            @RequestParam(required = false) UUID trabajadorId) {
        GetProductividadQuery query = new GetProductividadQuery(year, mes, trabajadorId);
        ProductividadListResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/metricas/rankings")
    public ResponseEntity<RankingsListResponse> getRankings(
            @RequestParam String year,
            @RequestParam String mes,
            @RequestParam(required = false) String cargo) {
        GetRankingsQuery query = new GetRankingsQuery(year, mes, cargo);
        RankingsListResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/metricas/horas-excedidas-summary")
    public ResponseEntity<HorasExcedidasSummaryListResponse> getHorasExcedidasSummary(
            @RequestParam String year,
            @RequestParam String mes) {
        GetHorasExcedidasSummaryQuery query = new GetHorasExcedidasSummaryQuery(year, mes);
        HorasExcedidasSummaryListResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    // ==================== ESCRIBIR HORAS EN PRENÓMINA EXCEL Y DESCARGAR ====================
    @PostMapping(value = "/consolidado/escribir-prenomina", produces = "application/vnd.ms-excel")
    public ResponseEntity<?> escribirHorasPrenomina(
            @RequestBody Map<String, Double> horasPorRuc,
            @RequestParam(required = false, defaultValue = "PRENOMINA_HORAS") String nombreArchivo) {
        try {
            PrenominaExcelService.WriteResult resultado = prenominaExcelService.escribirHorasEnPrenomina(horasPorRuc);

            byte[] excelBytes = resultado.getArchivoBytes();

            if (excelBytes == null || excelBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "El archivo generado está vacío"));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo + ".xls");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel");
            headers.add("X-Actualizados", String.valueOf(resultado.getActualizados()));
            headers.add("X-No-Encontrados", String.valueOf(resultado.getNoEncontrados()));
            headers.add("X-Total", String.valueOf(resultado.getTotalEnConsolidado()));
            headers.add("Access-Control-Expose-Headers", "X-Actualizados, X-No-Encontrados, X-Total");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(excelBytes.length)
                    .body(excelBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(Map.of(
                        "success", false,
                        "error", "Error al escribir en el archivo de prenómina: " + e.getMessage()
                    ));
        }
    }

    // ==================== TRABAJADORES NO REPORTADOS EN CONSOLIDADO ====================
    @GetMapping("/consolidado/trabajadores-faltantes")
    public ResponseEntity<TrabajadoresFaltantesListResponse> getTrabajadoresFaltantes(
            @RequestParam String year,
            @RequestParam String mes) {
        com.kynsoft.report.applications.query.reporte.trabajadoresFaltantes.GetTrabajadoresFaltantesQuery query =
            new com.kynsoft.report.applications.query.reporte.trabajadoresFaltantes.GetTrabajadoresFaltantesQuery(year, mes);
        TrabajadoresFaltantesListResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    // ==================== REPORTES POR TRABAJADOR (para modal en consolidado) ====================
    @GetMapping("/por-trabajador/{trabajadorId}")
    public ResponseEntity<List<ReporteResponse>> getReportesPorTrabajador(
            @PathVariable UUID trabajadorId,
            @RequestParam String year,
            @RequestParam String mes) {
        List<com.kynsoft.report.domain.dto.ReporteDto> reportes =
            reporteService.getReportesPorTrabajador(trabajadorId, year, mes);
        List<ReporteResponse> responses = reportes.stream()
                .map(ReporteResponse::new)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
