package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.cliente.create.CreateClienteCommand;
import com.kynsoft.report.applications.command.cliente.create.CreateClienteMessage;
import com.kynsoft.report.applications.command.cliente.create.CreateClienteRequest;
import com.kynsoft.report.applications.command.cliente.delete.DeleteClienteCommand;
import com.kynsoft.report.applications.command.cliente.delete.DeleteClienteMessage;
import com.kynsoft.report.applications.command.cliente.update.UpdateClienteCommand;
import com.kynsoft.report.applications.command.cliente.update.UpdateClienteMessage;
import com.kynsoft.report.applications.command.cliente.update.UpdateClienteRequest;
import com.kynsoft.report.applications.query.cliente.getById.FindClienteByIdQuery;
import com.kynsoft.report.applications.query.cliente.search.GetSearchClienteQuery;
import com.kynsoft.report.applications.query.responseObject.ClienteResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    private final IMediator mediator;

    public ClienteController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateClienteRequest request) {
        CreateClienteCommand createCommand = CreateClienteCommand.fromRequest(request);
        CreateClienteMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable UUID id) {
        FindClienteByIdQuery query = new FindClienteByIdQuery(id);
        ClienteResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateClienteRequest request) {
        UpdateClienteCommand updateCommand = UpdateClienteCommand.fromRequest(request, id);
        UpdateClienteMessage response = mediator.send(updateCommand);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteClienteCommand deleteCommand = new DeleteClienteCommand(id);
        DeleteClienteMessage response = mediator.send(deleteCommand);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        System.err.println("########################################");
        System.err.println("########################################");
        System.err.println("########################################");
        System.err.println("########################################");
        System.err.println("########################################");
        System.err.println("########################################");
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchClienteQuery query = new GetSearchClienteQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
