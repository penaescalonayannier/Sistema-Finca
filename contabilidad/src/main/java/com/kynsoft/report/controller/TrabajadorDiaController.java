package com.kynsoft.report.controller;

import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.trabajadordia.create.CreateTrabajadorDiaCommand;
import com.kynsoft.report.applications.command.trabajadordia.create.CreateTrabajadorDiaMessage;
import com.kynsoft.report.applications.command.trabajadordia.create.CreateTrabajadorDiaRequest;
import com.kynsoft.report.applications.command.trabajadordia.delete.DeleteTrabajadorDiaCommand;
import com.kynsoft.report.applications.command.trabajadordia.delete.DeleteTrabajadorDiaMessage;
import com.kynsoft.report.applications.command.trabajadordia.update.UpdateTrabajadorDiaCommand;
import com.kynsoft.report.applications.command.trabajadordia.update.UpdateTrabajadorDiaMessage;
import com.kynsoft.report.applications.command.trabajadordia.update.UpdateTrabajadorDiaRequest;
import com.kynsoft.report.applications.query.trabajadordia.getByDia.GetTrabajadoresByDiaQuery;
import com.kynsoft.report.applications.query.responseObject.TrabajadorDiaListResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/trabajador-dia")
public class TrabajadorDiaController {

    private final IMediator mediator;

    public TrabajadorDiaController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/{diaTrabajoId}/trabajadores")
    public ResponseEntity<CreateTrabajadorDiaMessage> agregarTrabajadorADia(
            @PathVariable UUID diaTrabajoId,
            @RequestBody CreateTrabajadorDiaRequest request) {
        CreateTrabajadorDiaCommand command = CreateTrabajadorDiaCommand.fromRequest(request, diaTrabajoId);
        CreateTrabajadorDiaMessage response = mediator.send(command);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateTrabajadorDiaMessage> actualizarTrabajadorDia(
            @PathVariable UUID id,
            @RequestBody UpdateTrabajadorDiaRequest request) {
        UpdateTrabajadorDiaCommand command = UpdateTrabajadorDiaCommand.fromRequest(request, id);
        UpdateTrabajadorDiaMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteTrabajadorDiaMessage> eliminarTrabajadorDia(@PathVariable UUID id) {
        DeleteTrabajadorDiaCommand command = new DeleteTrabajadorDiaCommand(id);
        DeleteTrabajadorDiaMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dia/{diaTrabajoId}")
    public ResponseEntity<TrabajadorDiaListResponse> getTrabajadoresByDia(@PathVariable UUID diaTrabajoId) {
        GetTrabajadoresByDiaQuery query = new GetTrabajadoresByDiaQuery(diaTrabajoId);
        TrabajadorDiaListResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }
}