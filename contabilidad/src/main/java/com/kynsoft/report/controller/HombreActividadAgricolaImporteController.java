package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.hombreactividadagricolaimporte.create.CreateHombreActividadAgricolaImporteCommand;
import com.kynsoft.report.applications.command.hombreactividadagricolaimporte.create.CreateHombreActividadAgricolaImporteMessage;
import com.kynsoft.report.applications.command.hombreactividadagricolaimporte.create.CreateHombreActividadAgricolaImporteRequest;
import com.kynsoft.report.applications.command.hombreactividadagricolaimporte.createbatch.CreateBatchHombreActividadAgricolaImporteCommand;
import com.kynsoft.report.applications.command.hombreactividadagricolaimporte.createbatch.CreateBatchHombreActividadAgricolaImporteMessage;
import com.kynsoft.report.applications.command.hombreactividadagricolaimporte.createbatch.CreateBatchHombreActividadAgricolaImporteRequest;
import com.kynsoft.report.applications.command.hombreactividadagricolaimporte.delete.DeleteHombreActividadAgricolaImporteCommand;
import com.kynsoft.report.applications.command.hombreactividadagricolaimporte.delete.DeleteHombreActividadAgricolaImporteMessage;
import com.kynsoft.report.applications.command.hombreactividadagricolaimporte.update.UpdateHombreActividadAgricolaImporteCommand;
import com.kynsoft.report.applications.command.hombreactividadagricolaimporte.update.UpdateHombreActividadAgricolaImporteMessage;
import com.kynsoft.report.applications.command.hombreactividadagricolaimporte.update.UpdateHombreActividadAgricolaImporteRequest;
import com.kynsoft.report.applications.query.hombreactividadagricolaimporte.getById.FindHombreActividadAgricolaImporteByIdQuery;
import com.kynsoft.report.applications.query.hombreactividadagricolaimporte.search.GetSearchHombreActividadAgricolaImporteQuery;
import com.kynsoft.report.applications.query.responseObject.HombreActividadAgricolaImporteResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/hombre-actividad-agricola-importe")
public class HombreActividadAgricolaImporteController {

    private final IMediator mediator;

    public HombreActividadAgricolaImporteController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateHombreActividadAgricolaImporteRequest request) {
        CreateHombreActividadAgricolaImporteCommand createCommand = CreateHombreActividadAgricolaImporteCommand.fromRequest(request);
        CreateHombreActividadAgricolaImporteMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HombreActividadAgricolaImporteResponse> findById(@PathVariable UUID id) {
        FindHombreActividadAgricolaImporteByIdQuery query = new FindHombreActividadAgricolaImporteByIdQuery(id);
        HombreActividadAgricolaImporteResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateHombreActividadAgricolaImporteRequest request) {
        UpdateHombreActividadAgricolaImporteCommand updateCommand = UpdateHombreActividadAgricolaImporteCommand.fromRequest(request, id);
        UpdateHombreActividadAgricolaImporteMessage response = mediator.send(updateCommand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteHombreActividadAgricolaImporteCommand deleteCommand = new DeleteHombreActividadAgricolaImporteCommand(id);
        DeleteHombreActividadAgricolaImporteMessage response = mediator.send(deleteCommand);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchHombreActividadAgricolaImporteQuery query = new GetSearchHombreActividadAgricolaImporteQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/batch")
    public ResponseEntity<?> createBatch(@RequestBody CreateBatchHombreActividadAgricolaImporteRequest request) {
        CreateBatchHombreActividadAgricolaImporteCommand createCommand = CreateBatchHombreActividadAgricolaImporteCommand.fromRequest(request);
        CreateBatchHombreActividadAgricolaImporteMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }
}
