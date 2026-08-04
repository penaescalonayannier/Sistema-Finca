package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.instrumentoTrabajo.create.CreateInstrumentoTrabajoCommand;
import com.kynsoft.report.applications.command.instrumentoTrabajo.create.CreateInstrumentoTrabajoMessage;
import com.kynsoft.report.applications.command.instrumentoTrabajo.create.CreateInstrumentoTrabajoRequest;
import com.kynsoft.report.applications.command.instrumentoTrabajo.delete.DeleteInstrumentoTrabajoCommand;
import com.kynsoft.report.applications.command.instrumentoTrabajo.delete.DeleteInstrumentoTrabajoMessage;
import com.kynsoft.report.applications.command.instrumentoTrabajo.update.UpdateInstrumentoTrabajoCommand;
import com.kynsoft.report.applications.command.instrumentoTrabajo.update.UpdateInstrumentoTrabajoMessage;
import com.kynsoft.report.applications.command.instrumentoTrabajo.update.UpdateInstrumentoTrabajoRequest;
import com.kynsoft.report.applications.query.instrumentoTrabajo.getById.FindInstrumentoTrabajoByIdQuery;
import com.kynsoft.report.applications.query.instrumentoTrabajo.search.GetSearchInstrumentoTrabajoQuery;
import com.kynsoft.report.applications.query.responseObject.InstrumentoTrabajoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/instrumento-trabajo")
public class InstrumentoTrabajoController {

    private final IMediator mediator;

    public InstrumentoTrabajoController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateInstrumentoTrabajoRequest request) {
        CreateInstrumentoTrabajoCommand createCommand = CreateInstrumentoTrabajoCommand.fromRequest(request);
        CreateInstrumentoTrabajoMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstrumentoTrabajoResponse> findById(@PathVariable UUID id) {
        FindInstrumentoTrabajoByIdQuery query = new FindInstrumentoTrabajoByIdQuery(id);
        InstrumentoTrabajoResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateInstrumentoTrabajoRequest request) {
        UpdateInstrumentoTrabajoCommand updateCommand = UpdateInstrumentoTrabajoCommand.fromRequest(request, id);
        UpdateInstrumentoTrabajoMessage response = mediator.send(updateCommand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteInstrumentoTrabajoCommand deleteCommand = new DeleteInstrumentoTrabajoCommand(id);
        DeleteInstrumentoTrabajoMessage response = mediator.send(deleteCommand);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchInstrumentoTrabajoQuery query = new GetSearchInstrumentoTrabajoQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}