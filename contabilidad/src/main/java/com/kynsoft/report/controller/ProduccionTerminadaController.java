package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.produccionterminada.create.CreateProduccionTerminadaCommand;
import com.kynsoft.report.applications.command.produccionterminada.create.CreateProduccionTerminadaMessage;
import com.kynsoft.report.applications.command.produccionterminada.create.CreateProduccionTerminadaRequest;
import com.kynsoft.report.applications.command.produccionterminada.delete.DeleteProduccionTerminadaCommand;
import com.kynsoft.report.applications.command.produccionterminada.delete.DeleteProduccionTerminadaMessage;
import com.kynsoft.report.applications.command.produccionterminada.update.UpdateProduccionTerminadaCommand;
import com.kynsoft.report.applications.command.produccionterminada.update.UpdateProduccionTerminadaMessage;
import com.kynsoft.report.applications.command.produccionterminada.update.UpdateProduccionTerminadaRequest;
import com.kynsoft.report.applications.query.produccionterminada.getall.GetAllProduccionTerminadaQuery;
import com.kynsoft.report.applications.query.produccionterminada.getbyid.FindProduccionTerminadaByIdQuery;
import com.kynsoft.report.applications.query.responseObject.ProduccionTerminadaResponse;
import com.kynsoft.report.domain.dto.ProduccionTerminadaDto;
import com.kynsoft.report.domain.services.IProduccionTerminadaService;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produccion-terminada")
public class ProduccionTerminadaController {

    private final IMediator mediator;
    private final IProduccionTerminadaService service;

    public ProduccionTerminadaController(IMediator mediator, IProduccionTerminadaService service) {
        this.mediator = mediator;
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CreateProduccionTerminadaMessage> create(
            @RequestBody CreateProduccionTerminadaRequest request) {
        CreateProduccionTerminadaCommand command = CreateProduccionTerminadaCommand.fromRequest(request);
        CreateProduccionTerminadaMessage response = mediator.send(command);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateProduccionTerminadaMessage> update(
            @PathVariable UUID id,
            @RequestBody UpdateProduccionTerminadaRequest request) {
        UpdateProduccionTerminadaCommand command = UpdateProduccionTerminadaCommand.fromRequest(request, id);
        UpdateProduccionTerminadaMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteProduccionTerminadaMessage> delete(@PathVariable UUID id) {
        DeleteProduccionTerminadaCommand command = new DeleteProduccionTerminadaCommand(id);
        DeleteProduccionTerminadaMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProduccionTerminadaResponse> findById(@PathVariable UUID id) {
        FindProduccionTerminadaByIdQuery query = new FindProduccionTerminadaByIdQuery(id);
        ProduccionTerminadaResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetAllProduccionTerminadaQuery query = new GetAllProduccionTerminadaQuery(
                pageable,
                request.getFilter(),
                request.getQuery()
        );

        PaginatedResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/por-finca/{fincaId}")
    public ResponseEntity<?> findByFinca(
            @PathVariable UUID fincaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<ProduccionTerminadaDto> producciones;
        if (fechaInicio != null && fechaFin != null) {
            producciones = service.findByFincaIdAndFechaBetween(fincaId, fechaInicio, fechaFin);
        } else {
            producciones = service.findByFincaId(fincaId);
        }

        List<ProduccionTerminadaResponse> content = producciones.stream()
                .map(ProduccionTerminadaResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("totalElements", content.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/por-producto/{productoId}")
    public ResponseEntity<?> findByProducto(@PathVariable UUID productoId) {
        List<ProduccionTerminadaDto> producciones = service.findByProductoId(productoId);

        List<ProduccionTerminadaResponse> content = producciones.stream()
                .map(ProduccionTerminadaResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("totalElements", content.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/por-trabajador/{trabajadorId}")
    public ResponseEntity<?> findByTrabajador(
            @PathVariable UUID trabajadorId,
            @RequestParam(defaultValue = "AMBOS") String rol) {

        List<ProduccionTerminadaDto> producciones;

        switch (rol.toUpperCase()) {
            case "ENTREGA":
                producciones = service.findByTrabajadorEntregaId(trabajadorId);
                break;
            case "RECIBE":
                producciones = service.findByTrabajadorRecibeId(trabajadorId);
                break;
            default:
                // AMBOS - unir ambas listas sin duplicados
                List<ProduccionTerminadaDto> entrega = service.findByTrabajadorEntregaId(trabajadorId);
                List<ProduccionTerminadaDto> recibe = service.findByTrabajadorRecibeId(trabajadorId);
                producciones = entrega;
                recibe.stream()
                        .filter(p -> entrega.stream().noneMatch(e -> e.getId().equals(p.getId())))
                        .forEach(producciones::add);
                break;
        }

        List<ProduccionTerminadaResponse> content = producciones.stream()
                .map(ProduccionTerminadaResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("totalElements", content.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/por-fecha")
    public ResponseEntity<?> findByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) UUID fincaId) {

        List<ProduccionTerminadaDto> producciones;
        if (fincaId != null) {
            producciones = service.findByFincaIdAndFechaBetween(fincaId, fechaInicio, fechaFin);
        } else {
            producciones = service.findByFechaBetween(fechaInicio, fechaFin);
        }

        List<ProduccionTerminadaResponse> content = producciones.stream()
                .map(ProduccionTerminadaResponse::new)
                .collect(Collectors.toList());

        Double totalCantidad = producciones.stream()
                .mapToDouble(ProduccionTerminadaDto::getCantidadTerminada)
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("totalElements", content.size());
        response.put("totalCantidad", totalCantidad);

        return ResponseEntity.ok(response);
    }
}
