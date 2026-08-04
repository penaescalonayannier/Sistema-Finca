package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.campos.create.CreateCamposCommand;
import com.kynsoft.report.applications.command.campos.create.CreateCamposMessage;
import com.kynsoft.report.applications.command.campos.create.CreateCamposRequest;
import com.kynsoft.report.applications.command.campos.delete.DeleteCamposCommand;
import com.kynsoft.report.applications.command.campos.delete.DeleteCamposMessage;
import com.kynsoft.report.applications.command.campos.update.UpdateCamposCommand;
import com.kynsoft.report.applications.command.campos.update.UpdateCamposMessage;
import com.kynsoft.report.applications.command.campos.update.UpdateCamposRequest;
import com.kynsoft.report.applications.query.campos.getById.FindCamposByIdQuery;
import com.kynsoft.report.applications.query.campos.search.GetSearchCamposQuery;
import com.kynsoft.report.applications.query.responseObject.CamposResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/campos")
public class CamposController {

    private final IMediator mediator;

    public CamposController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateCamposRequest request) {
        CreateCamposCommand createCommand = CreateCamposCommand.fromRequest(request);
        CreateCamposMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CamposResponse> findById(@PathVariable UUID id) {
        FindCamposByIdQuery query = new FindCamposByIdQuery(id);
        CamposResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateCamposRequest request) {
        UpdateCamposCommand updateCommand = UpdateCamposCommand.fromRequest(request, id);
        UpdateCamposMessage response = mediator.send(updateCommand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteCamposCommand deleteCommand = new DeleteCamposCommand(id);
        DeleteCamposMessage response = mediator.send(deleteCommand);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchCamposQuery query = new GetSearchCamposQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}