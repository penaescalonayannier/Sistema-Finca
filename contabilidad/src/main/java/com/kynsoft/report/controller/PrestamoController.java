package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.prestamo.create.CreatePrestamoCommand;
import com.kynsoft.report.applications.command.prestamo.create.CreatePrestamoMessage;
import com.kynsoft.report.applications.command.prestamo.create.CreatePrestamoRequest;
import com.kynsoft.report.applications.command.prestamo.delete.DeletePrestamoCommand;
import com.kynsoft.report.applications.command.prestamo.delete.DeletePrestamoMessage;
import com.kynsoft.report.applications.command.prestamo.update.UpdatePrestamoCommand;
import com.kynsoft.report.applications.command.prestamo.update.UpdatePrestamoMessage;
import com.kynsoft.report.applications.command.prestamo.update.UpdatePrestamoRequest;
import com.kynsoft.report.applications.query.prestamo.getById.FindPrestamoByIdQuery;
import com.kynsoft.report.applications.query.prestamo.search.GetSearchPrestamoQuery;
import com.kynsoft.report.applications.query.responseObject.PrestamoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/prestamo")
public class PrestamoController {

    private final IMediator mediator;

    public PrestamoController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreatePrestamoRequest request) {
        CreatePrestamoCommand createCommand = CreatePrestamoCommand.fromRequest(request);
        CreatePrestamoMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoResponse> findById(@PathVariable UUID id) {
        FindPrestamoByIdQuery query = new FindPrestamoByIdQuery(id);
        PrestamoResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdatePrestamoRequest request) {
        UpdatePrestamoCommand updateCommand = UpdatePrestamoCommand.fromRequest(request, id);
        UpdatePrestamoMessage response = mediator.send(updateCommand);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeletePrestamoCommand deleteCommand = new DeletePrestamoCommand(id);
        DeletePrestamoMessage response = mediator.send(deleteCommand);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchPrestamoQuery query = new GetSearchPrestamoQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
