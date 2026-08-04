package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.tomaprestamo.create.CreateTomaPrestamoCommand;
import com.kynsoft.report.applications.command.tomaprestamo.create.CreateTomaPrestamoMessage;
import com.kynsoft.report.applications.command.tomaprestamo.create.CreateTomaPrestamoRequest;
import com.kynsoft.report.applications.command.tomaprestamo.delete.DeleteTomaPrestamoCommand;
import com.kynsoft.report.applications.command.tomaprestamo.delete.DeleteTomaPrestamoMessage;
import com.kynsoft.report.applications.command.tomaprestamo.update.UpdateTomaPrestamoCommand;
import com.kynsoft.report.applications.command.tomaprestamo.update.UpdateTomaPrestamoMessage;
import com.kynsoft.report.applications.command.tomaprestamo.update.UpdateTomaPrestamoRequest;
import com.kynsoft.report.applications.query.report.estadoCuenta.export.Response;
import com.kynsoft.report.applications.query.tomaprestamo.getById.FindTomaPrestamoByIdQuery;
import com.kynsoft.report.applications.query.tomaprestamo.search.GetSearchTomaPrestamoQuery;
import com.kynsoft.report.applications.query.responseObject.TomaPrestamoResponse;
import com.kynsoft.report.applications.query.tomaprestamo.respaldoCultural.export.GetExportRespaldoCulturalQuery;
import com.kynsoft.report.applications.query.tomaprestamo.respaldoCultural.solicitudDisposicion.export.GetExportSolicitudDisposicionQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api/toma-prestamo")
public class TomaPrestamoController {

    private final IMediator mediator;

    public TomaPrestamoController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateTomaPrestamoRequest request) {
        CreateTomaPrestamoCommand createCommand = CreateTomaPrestamoCommand.fromRequest(request);
        CreateTomaPrestamoMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TomaPrestamoResponse> findById(@PathVariable UUID id) {
        FindTomaPrestamoByIdQuery query = new FindTomaPrestamoByIdQuery(id);
        TomaPrestamoResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateTomaPrestamoRequest request) {
        UpdateTomaPrestamoCommand updateCommand = UpdateTomaPrestamoCommand.fromRequest(request, id);
        UpdateTomaPrestamoMessage response = mediator.send(updateCommand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteTomaPrestamoCommand deleteCommand = new DeleteTomaPrestamoCommand(id);
        DeleteTomaPrestamoMessage response = mediator.send(deleteCommand);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchTomaPrestamoQuery query = new GetSearchTomaPrestamoQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{id}/export-respaldo-cultural")
    public ResponseEntity<StreamingResponseBody> exportRespaldoCultural(@PathVariable UUID id) {
        // 1. Crear la query con el ID de la toma de préstamo
        GetExportRespaldoCulturalQuery query = GetExportRespaldoCulturalQuery.builder()
                .tomaPrestamoId(id)
                .build();

        // 2. El mediator envía la query y el handler devuelve el Response (que contiene el StreamingResponseBody)
        Response response = mediator.send(query);

        // 3. Configurar las cabeceras HTTP para la descarga del archivo
        String filename = "Formato_Apertura_Creditos.xlsx";
        return ResponseEntity.ok()
                // Indica al navegador que descargue el archivo con el nombre especificado
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                // Define el tipo MIME para archivos Excel (XLSX)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(response.getOutputStream());
    }

    @GetMapping("/{id}/export-solicitud-disposicion")
    public ResponseEntity<StreamingResponseBody> exportSolicitudDisposicion(@PathVariable UUID id) {
        GetExportSolicitudDisposicionQuery query = GetExportSolicitudDisposicionQuery.builder()
                .tomaPrestamoId(id)
                .build();

        Response response = mediator.send(query);

        String filename = "Solicitud_Disposicion_Prestamo.docx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(response.getOutputStream());
    }
}
