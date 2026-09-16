package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
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
import com.kynsoft.report.infrastructure.services.ProduccionTerminadaPdfService;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produccion-terminada")
public class ProduccionTerminadaController {

    private static final Logger log = LoggerFactory.getLogger(ProduccionTerminadaController.class);

    private final IMediator mediator;
    private final IProduccionTerminadaService service;
    private final ProduccionTerminadaPdfService produccionTerminadaPdfService;

    public ProduccionTerminadaController(IMediator mediator, IProduccionTerminadaService service,
                                         ProduccionTerminadaPdfService produccionTerminadaPdfService) {
        this.mediator = mediator;
        this.service = service;
        this.produccionTerminadaPdfService = produccionTerminadaPdfService;
    }

    @PostMapping
    public ResponseEntity<CreateProduccionTerminadaMessage> create(
            @RequestBody CreateProduccionTerminadaRequest request) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "La producción terminada debe registrarse desde la entrada del almacén receptor.");
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

    /**
     * Descarga el modelo SC-2-06 de una producción ya registrada. Esta operación
     * es de solo lectura: no modifica producción, almacén, existencias ni contabilidad.
     */
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarModeloOficial(@PathVariable UUID id) {
        try {
            byte[] pdf = produccionTerminadaPdfService.generar(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "SC-2-06_produccion_"
                    + id.toString().substring(0, 8) + ".pdf");
            headers.setContentLength(pdf.length);
            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (BusinessNotFoundException exception) {
            log.warn("No fue posible generar el modelo SC-2-06 de producción {}: {}", id, exception.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception exception) {
            log.error("Error al generar el modelo SC-2-06 de producción {}", id, exception);
            return ResponseEntity.internalServerError().build();
        }
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
