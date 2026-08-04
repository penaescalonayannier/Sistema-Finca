package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.bloque.create.CreateBloqueCommand;
import com.kynsoft.report.applications.command.bloque.create.CreateBloqueMessage;
import com.kynsoft.report.applications.command.bloque.create.CreateBloqueRequest;
import com.kynsoft.report.applications.command.bloque.delete.DeleteBloqueCommand;
import com.kynsoft.report.applications.command.bloque.delete.DeleteBloqueMessage;
import com.kynsoft.report.applications.command.bloque.update.UpdateBloqueCommand;
import com.kynsoft.report.applications.command.bloque.update.UpdateBloqueMessage;
import com.kynsoft.report.applications.command.bloque.update.UpdateBloqueRequest;
import com.kynsoft.report.applications.query.bloque.getById.FindBloqueByIdQuery;
import com.kynsoft.report.applications.query.bloque.search.GetSearchBloqueQuery;
import com.kynsoft.report.applications.query.responseObject.BloqueResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bloque")
public class BloqueController {

    private final IMediator mediator;

    public BloqueController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateBloqueRequest request) {
        CreateBloqueCommand createCommand = CreateBloqueCommand.fromRequest(request);
        CreateBloqueMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BloqueResponse> findById(@PathVariable UUID id) {
        FindBloqueByIdQuery query = new FindBloqueByIdQuery(id);
        BloqueResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateBloqueRequest request) {
        UpdateBloqueCommand updateCommand = UpdateBloqueCommand.fromRequest(request, id);
        UpdateBloqueMessage response = mediator.send(updateCommand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteBloqueCommand deleteCommand = new DeleteBloqueCommand(id);
        DeleteBloqueMessage response = mediator.send(deleteCommand);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchBloqueQuery query = new GetSearchBloqueQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}