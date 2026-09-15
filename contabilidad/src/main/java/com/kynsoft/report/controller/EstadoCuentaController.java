package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.report.estadoCuenta.create.CreateEstadoCuentaCommand;
import com.kynsoft.report.applications.command.report.estadoCuenta.create.CreateEstadoCuentaMessage;
import com.kynsoft.report.applications.command.report.estadoCuenta.create.CreateEstadoCuentaRequest;
import com.kynsoft.report.applications.command.report.estadoCuenta.createBatch.CreateEstadoCuentaBatchCommand;
import com.kynsoft.report.applications.command.report.estadoCuenta.createBatch.CreateEstadoCuentaBatchMessage;
import com.kynsoft.report.applications.command.report.estadoCuenta.createBatch.CreateEstadoCuentaBatchRequest;
import com.kynsoft.report.applications.command.report.estadoCuenta.upload.UploadXmlCommand;
import com.kynsoft.report.applications.query.report.estadoCuenta.export.GetExportEstadoCuentaQuery;
import com.kynsoft.report.applications.query.report.estadoCuenta.export.Response;
import com.kynsoft.report.applications.query.report.estadoCuenta.getByDateRange.GetEstadoCuentaByDateQuery;
import com.kynsoft.report.applications.query.report.estadoCuenta.getById.FindEstadoCuentaByIdQuery;
import com.kynsoft.report.applications.query.report.estadoCuenta.previewXml.PreviewXmlQuery;
import com.kynsoft.report.applications.query.report.estadoCuenta.previewXml.PreviewXmlResponse;
import com.kynsoft.report.applications.query.report.estadoCuenta.search.GetSearchEstadoCuentaQuery;
import com.kynsoft.report.applications.query.responseObject.Cuenta110EfectivoBancoResponse;
import com.kynsoft.report.applications.query.responseObject.EstadoCuentaByDateRangeResponse;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api/estado-cuenta")
public class EstadoCuentaController {

    private final IMediator mediator;

    public EstadoCuentaController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateEstadoCuentaRequest request) {
        CreateEstadoCuentaCommand createCommand = CreateEstadoCuentaCommand.fromRequest(request);
        CreateEstadoCuentaMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<?> createBatch(@RequestBody CreateEstadoCuentaBatchRequest request) {
        CreateEstadoCuentaBatchCommand batchCommand = CreateEstadoCuentaBatchCommand.fromRequest(request);
        CreateEstadoCuentaBatchMessage response = mediator.send(batchCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cuenta110EfectivoBancoResponse> findByIdentification(@PathVariable UUID id) {

        FindEstadoCuentaByIdQuery query = new FindEstadoCuentaByIdQuery(id);
        Cuenta110EfectivoBancoResponse resp = mediator.send(query);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchEstadoCuentaQuery query = new GetSearchEstadoCuentaQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PostMapping(value = "/upload-xml", consumes = "application/xml")
    public ResponseEntity<?> uploadXml(@RequestBody String xmlContent) {
        System.err.println("Contenido: " + xmlContent);

        // El Controller solo se encarga de recibir la petición HTTP y construir el Command
        UploadXmlCommand command = new UploadXmlCommand(xmlContent);

        // El Mediador envía el Command al Handler, donde ocurre la lógica de parsing y persistencia
        ICommandMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/preview-xml", consumes = "application/xml")
    public ResponseEntity<PreviewXmlResponse> previewXml(@RequestBody String xmlContent) {
        // El Controller solo se encarga de recibir la petición HTTP y construir el Query
        PreviewXmlQuery query = new PreviewXmlQuery(xmlContent);

        // El Mediador envía el Query al Handler, donde ocurre solo el parsing (sin persistencia)
        PreviewXmlResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    public ResponseEntity<StreamingResponseBody> exportToExcel(
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin) {

        // 1. Crear el Query con los parámetros de filtro
        GetExportEstadoCuentaQuery queryBus = new GetExportEstadoCuentaQuery(fechaInicio, fechaFin);

        // 2. El mediator envía el query y el handler devuelve el StreamingResponseBody (el contenido del archivo)
        Response response = mediator.send(queryBus);

        // 3. Configurar las cabeceras HTTP para la descarga del archivo
        String filename = "EstadoCuentas_Export.xlsx";
        return ResponseEntity.ok()
                // Indica al navegador que descargue el archivo con el nombre especificado
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                // Define el tipo MIME para archivos Excel (XLSX)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(response.getOutputStream());
    }

    @GetMapping("/search-by-date")
    public ResponseEntity<EstadoCuentaByDateRangeResponse> searchByDate(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {

        // 1. Crear el Query con los parámetros de fecha
        GetEstadoCuentaByDateQuery queryBus = new GetEstadoCuentaByDateQuery(
                fechaInicio, fechaFin
        );

        // 2. El mediator envía el query y el handler devuelve la lista de respuestas
        EstadoCuentaByDateRangeResponse response = mediator.send(queryBus);

        return ResponseEntity.ok(response);
    }
}
