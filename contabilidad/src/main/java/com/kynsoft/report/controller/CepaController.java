package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.cepa.create.CreateCepaCommand;
import com.kynsoft.report.applications.command.cepa.create.CreateCepaMessage;
import com.kynsoft.report.applications.command.cepa.create.CreateCepaRequest;
import com.kynsoft.report.applications.command.cepa.delete.DeleteCepaCommand;
import com.kynsoft.report.applications.command.cepa.delete.DeleteCepaMessage;
import com.kynsoft.report.applications.command.cepa.update.UpdateCepaCommand;
import com.kynsoft.report.applications.command.cepa.update.UpdateCepaMessage;
import com.kynsoft.report.applications.command.cepa.update.UpdateCepaRequest;
import com.kynsoft.report.applications.query.cepa.getById.FindCepaByIdQuery;
import com.kynsoft.report.applications.query.cepa.search.GetSearchCepaQuery;
import com.kynsoft.report.applications.query.responseObject.CepaResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cepa")
public class CepaController {

    private final IMediator mediator;

    public CepaController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateCepaRequest request) {
        CreateCepaCommand createCommand = CreateCepaCommand.fromRequest(request);
        CreateCepaMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CepaResponse> findById(@PathVariable UUID id) {
        FindCepaByIdQuery query = new FindCepaByIdQuery(id);
        CepaResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateCepaRequest request) {
        UpdateCepaCommand updateCommand = UpdateCepaCommand.fromRequest(request, id);
        UpdateCepaMessage response = mediator.send(updateCommand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteCepaCommand deleteCommand = new DeleteCepaCommand(id);
        DeleteCepaMessage response = mediator.send(deleteCommand);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchCepaQuery query = new GetSearchCepaQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}