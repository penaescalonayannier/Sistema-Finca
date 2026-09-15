package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.unidadmedida.create.CreateUnidadMedidaCommand;
import com.kynsoft.report.applications.command.unidadmedida.create.CreateUnidadMedidaMessage;
import com.kynsoft.report.applications.command.unidadmedida.create.CreateUnidadMedidaRequest;
import com.kynsoft.report.applications.command.unidadmedida.delete.DeleteUnidadMedidaCommand;
import com.kynsoft.report.applications.command.unidadmedida.delete.DeleteUnidadMedidaMessage;
import com.kynsoft.report.applications.command.unidadmedida.update.UpdateUnidadMedidaCommand;
import com.kynsoft.report.applications.command.unidadmedida.update.UpdateUnidadMedidaMessage;
import com.kynsoft.report.applications.command.unidadmedida.update.UpdateUnidadMedidaRequest;
import com.kynsoft.report.applications.query.unidadmedida.getById.FindUnidadMedidaByIdQuery;
import com.kynsoft.report.applications.query.unidadmedida.search.GetSearchUnidadMedidaQuery;
import com.kynsoft.report.applications.query.responseObject.UnidadMedidaResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/unidad-medida")
public class UnidadMedidaController {

    private final IMediator mediator;

    public UnidadMedidaController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateUnidadMedidaRequest request) {
        CreateUnidadMedidaCommand createCommand = CreateUnidadMedidaCommand.fromRequest(request);
        CreateUnidadMedidaMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadMedidaResponse> findById(@PathVariable UUID id) {
        FindUnidadMedidaByIdQuery query = new FindUnidadMedidaByIdQuery(id);
        UnidadMedidaResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateUnidadMedidaRequest request) {
        UpdateUnidadMedidaCommand updateCommand = UpdateUnidadMedidaCommand.fromRequest(request, id);
        UpdateUnidadMedidaMessage response = mediator.send(updateCommand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteUnidadMedidaCommand deleteCommand = new DeleteUnidadMedidaCommand(id);
        DeleteUnidadMedidaMessage response = mediator.send(deleteCommand);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchUnidadMedidaQuery query = new GetSearchUnidadMedidaQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}