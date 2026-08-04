package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.report.cuenta110.create.CreateCuenta110Command;
import com.kynsoft.report.applications.command.report.cuenta110.create.CreateCuenta110Message;
import com.kynsoft.report.applications.command.report.cuenta110.create.CreateCuenta110Request;
import com.kynsoft.report.applications.command.report.cuenta110.update.UpdateCuenta110Command;
import com.kynsoft.report.applications.command.report.cuenta110.update.UpdateCuenta110Request;
import com.kynsoft.report.applications.command.report.cuenta110.update.importe.UpdateCuenta110ImporteCommand;
import com.kynsoft.report.applications.command.report.cuenta110.update.importe.UpdateCuenta110ImporteRequest;
import com.kynsoft.report.applications.query.report.cuenta110.getById.FindCuenta110ByIdQuery;
import com.kynsoft.report.applications.query.report.cuenta110.getImporte.FindUniqueCuenta110Query;
import com.kynsoft.report.applications.query.report.cuenta110.search.GetSearchCuenta110Query;
import com.kynsoft.report.applications.query.responseObject.Cuenta110EfectivoBancoResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cuenta110")
public class Cuenta110Controller {

    private final IMediator mediator;

    public Cuenta110Controller(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateCuenta110Request request) {
        CreateCuenta110Command createCommand = CreateCuenta110Command.fromRequest(request);
        CreateCuenta110Message response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cuenta110EfectivoBancoResponse> getById(@PathVariable UUID id) {

        FindCuenta110ByIdQuery query = new FindCuenta110ByIdQuery(id);
        Cuenta110EfectivoBancoResponse resp = mediator.send(query);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ICommandMessage> update(@RequestBody UpdateCuenta110Request request, @PathVariable UUID id) {

        UpdateCuenta110Command createCommand = UpdateCuenta110Command.fromRequest(request, id);
        ICommandMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchCuenta110Query query = new GetSearchCuenta110Query(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping("/importe")
    public ResponseEntity<ICommandMessage> updateImporte(@RequestBody UpdateCuenta110ImporteRequest request) {

        // 1. Crear el Command a partir del Request. El handler buscará el ID único.
        UpdateCuenta110ImporteCommand updateCommand = UpdateCuenta110ImporteCommand.fromRequest(request);

        // 2. Enviar el Command al mediador
        ICommandMessage response = mediator.send(updateCommand);

        // Retornar la respuesta del comando
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unique") // <-- NUEVA RUTA
    public ResponseEntity<Cuenta110EfectivoBancoResponse> getUnique() {

        // 1. Crear el Query para buscar el único registro
        FindUniqueCuenta110Query query = new FindUniqueCuenta110Query();

        // 2. Enviar el Query
        Cuenta110EfectivoBancoResponse resp = mediator.send(query);

        return ResponseEntity.ok(resp);
    }
}
