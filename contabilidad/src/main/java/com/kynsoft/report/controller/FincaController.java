package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.finca.asignarResponsable.AsignarResponsableCommand;
import com.kynsoft.report.applications.command.finca.asignarResponsable.AsignarResponsableMessage;
import com.kynsoft.report.applications.command.finca.asignarResponsable.AsignarResponsableRequest;
import com.kynsoft.report.applications.command.finca.create.CreateFincaCommand;
import com.kynsoft.report.applications.command.finca.create.CreateFincaMessage;
import com.kynsoft.report.applications.command.finca.create.CreateFincaRequest;
import com.kynsoft.report.applications.command.finca.delete.DeleteFincaCommand;
import com.kynsoft.report.applications.command.finca.delete.DeleteFincaMessage;
import com.kynsoft.report.applications.command.finca.reactivar.ReactivarFincaCommand;
import com.kynsoft.report.applications.command.finca.reactivar.ReactivarFincaMessage;
import com.kynsoft.report.applications.command.finca.update.UpdateFincaCommand;
import com.kynsoft.report.applications.command.finca.update.UpdateFincaMessage;
import com.kynsoft.report.applications.command.finca.update.UpdateFincaRequest;
import com.kynsoft.report.applications.query.finca.getByCode.FindFincaByCodeQuery;
import com.kynsoft.report.applications.query.finca.getById.FindFincaByIdQuery;
import com.kynsoft.report.applications.query.finca.resumen.FincaResumenResponse;
import com.kynsoft.report.applications.query.finca.resumen.GetFincaResumenQuery;
import com.kynsoft.report.applications.query.finca.search.GetSearchFincaQuery;
import com.kynsoft.report.applications.query.responseObject.FincaResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/finca")
public class FincaController {

    private final IMediator mediator;

    public FincaController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateFincaRequest request) {
        CreateFincaCommand createCommand = CreateFincaCommand.fromRequest(request);
        CreateFincaMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FincaResponse> findById(@PathVariable UUID id) {
        FindFincaByIdQuery query = new FindFincaByIdQuery(id);
        FincaResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<FincaResponse> findByCode(@PathVariable String code) {
        FindFincaByCodeQuery query = new FindFincaByCodeQuery(code);
        FincaResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateFincaRequest request) {
        UpdateFincaCommand updateCommand = UpdateFincaCommand.fromRequest(request, id);
        UpdateFincaMessage response = mediator.send(updateCommand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteFincaCommand deleteCommand = new DeleteFincaCommand(id);
        DeleteFincaMessage response = mediator.send(deleteCommand);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reactivar")
    public ResponseEntity<?> reactivar(@PathVariable UUID id) {
        ReactivarFincaCommand command = new ReactivarFincaCommand(id);
        ReactivarFincaMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/asignar-responsable")
    public ResponseEntity<?> asignarResponsable(@PathVariable UUID id, @RequestBody AsignarResponsableRequest request) {
        AsignarResponsableCommand command = AsignarResponsableCommand.fromRequest(request, id);
        AsignarResponsableMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/resumen")
    public ResponseEntity<FincaResumenResponse> getResumen(@PathVariable UUID id) {
        GetFincaResumenQuery query = new GetFincaResumenQuery(id);
        FincaResumenResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchFincaQuery query = new GetSearchFincaQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}