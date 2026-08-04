package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.trabajadorReporte.create.CreateTrabajadorReporteCommand;
import com.kynsoft.report.applications.command.trabajadorReporte.create.CreateTrabajadorReporteMessage;
import com.kynsoft.report.applications.command.trabajadorReporte.create.CreateTrabajadorReporteRequest;
import com.kynsoft.report.applications.command.trabajadorReporte.delete.DeleteTrabajadorReporteCommand;
import com.kynsoft.report.applications.command.trabajadorReporte.delete.DeleteTrabajadorReporteMessage;
import com.kynsoft.report.applications.command.trabajadorReporte.update.UpdateTrabajadorReporteCommand;
import com.kynsoft.report.applications.command.trabajadorReporte.update.UpdateTrabajadorReporteMessage;
import com.kynsoft.report.applications.command.trabajadorReporte.update.UpdateTrabajadorReporteRequest;
import com.kynsoft.report.applications.query.responseObject.TrabajadorReporteDetailListResponse;
import com.kynsoft.report.applications.query.responseObject.TrabajadorReporteResponse;
import com.kynsoft.report.applications.query.trabajadorReporte.getById.FindTrabajadorReporteByIdQuery;
import com.kynsoft.report.applications.query.trabajadorReporte.getByReporte.GetTrabajadoresByReporteQuery;
import com.kynsoft.report.applications.query.trabajadorReporte.search.GetSearchTrabajadorReporteQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/trabajdor-reporte")
public class TrabajadorReporteController {

    private final IMediator mediator;

    public TrabajadorReporteController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateTrabajadorReporteRequest request) {
        CreateTrabajadorReporteCommand createCommand = CreateTrabajadorReporteCommand.fromRequest(request);
        CreateTrabajadorReporteMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrabajadorReporteResponse> findById(@PathVariable UUID id) {
        FindTrabajadorReporteByIdQuery query = new FindTrabajadorReporteByIdQuery(id);
        TrabajadorReporteResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateTrabajadorReporteRequest request) {
        UpdateTrabajadorReporteCommand updateCommand = UpdateTrabajadorReporteCommand.fromRequest(request, id);
        UpdateTrabajadorReporteMessage response = mediator.send(updateCommand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteTrabajadorReporteCommand deleteCommand = new DeleteTrabajadorReporteCommand(id);
        DeleteTrabajadorReporteMessage response = mediator.send(deleteCommand);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchTrabajadorReporteQuery query = new GetSearchTrabajadorReporteQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    // ==================== NUEVO ENDPOINT ====================
    @GetMapping("/reporte/{reporteId}")
    public ResponseEntity<TrabajadorReporteDetailListResponse> getTrabajadoresByReporte(@PathVariable UUID reporteId) {
        GetTrabajadoresByReporteQuery query = new GetTrabajadoresByReporteQuery(reporteId);
        TrabajadorReporteDetailListResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }
}