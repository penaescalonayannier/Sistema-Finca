package com.kynsoft.report.controller;

import com.kynsoft.report.applications.command.planingresoventa.PlanIngresoVentaRequest;
import com.kynsoft.report.applications.command.planingresoventa.create.CreatePlanIngresoVentaCommand;
import com.kynsoft.report.applications.command.planingresoventa.create.CreatePlanIngresoVentaMessage;
import com.kynsoft.report.applications.command.planingresoventa.delete.DeletePlanIngresoVentaCommand;
import com.kynsoft.report.applications.command.planingresoventa.delete.DeletePlanIngresoVentaMessage;
import com.kynsoft.report.applications.command.planingresoventa.update.UpdatePlanIngresoVentaCommand;
import com.kynsoft.report.applications.command.planingresoventa.update.UpdatePlanIngresoVentaMessage;
import com.kynsoft.report.applications.query.planingresoventa.getbyid.FindPlanIngresoVentaByIdQuery;
import com.kynsoft.report.applications.query.planingresoventa.search.GetSearchPlanIngresoVentaQuery;
import com.kynsoft.report.applications.query.responseObject.PlanIngresoVentaResponse;
import com.kynsoft.report.domain.dto.EstadoPlanIngresoVenta;
import com.kynsoft.report.domain.services.IPlanIngresoVentaService;
import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.bus.IMediator;
import java.util.UUID;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

/** CRUD de planificación. No altera inventario, facturación, caja ni contabilidad. */
@RestController
@RequestMapping("/api/plan-ingreso-venta")
public class PlanIngresoVentaController {
    private final IMediator mediator;
    private final IPlanIngresoVentaService service;
    public PlanIngresoVentaController(IMediator mediator, IPlanIngresoVentaService service) { this.mediator = mediator; this.service = service; }

    @PostMapping
    public ResponseEntity<CreatePlanIngresoVentaMessage> create(@RequestBody PlanIngresoVentaRequest request) {
        return ResponseEntity.ok(mediator.send(CreatePlanIngresoVentaCommand.fromRequest(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanIngresoVentaResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(mediator.send(new FindPlanIngresoVentaByIdQuery(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdatePlanIngresoVentaMessage> update(@PathVariable UUID id,
            @RequestBody PlanIngresoVentaRequest request) {
        return ResponseEntity.ok(mediator.send(UpdatePlanIngresoVentaCommand.fromRequest(request, id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeletePlanIngresoVentaMessage> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(mediator.send(new DeletePlanIngresoVentaCommand(id)));
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(mediator.send(new GetSearchPlanIngresoVentaQuery(
                PageableUtil.createPageable(request), request.getFilter(), request.getQuery())));
    }

    /** Crea el borrador del período y precarga la última planificación disponible. */
    @PostMapping("/generar-siguiente")
    public ResponseEntity<PlanIngresoVentaResponse> generarSiguiente(@RequestParam LocalDate periodo) {
        return ResponseEntity.ok(new PlanIngresoVentaResponse(service.generarSiguiente(periodo)));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<PlanIngresoVentaResponse> cambiarEstado(@PathVariable UUID id,
            @RequestBody EstadoPlanRequest request) {
        return ResponseEntity.ok(new PlanIngresoVentaResponse(service.cambiarEstado(id, request.estado())));
    }

    @GetMapping("/{id}/ejecucion")
    public ResponseEntity<Map<String, Object>> ejecucion(@PathVariable UUID id) {
        return ResponseEntity.ok(service.ejecucion(id));
    }

    public record EstadoPlanRequest(EstadoPlanIngresoVenta estado) { }
}
