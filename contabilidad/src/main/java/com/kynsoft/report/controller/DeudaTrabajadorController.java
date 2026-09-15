package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.deudaTrabajador.create.CreateDeudaTrabajadorCommand;
import com.kynsoft.report.applications.command.deudaTrabajador.create.CreateDeudaTrabajadorMessage;
import com.kynsoft.report.applications.command.deudaTrabajador.create.CreateDeudaTrabajadorRequest;
import com.kynsoft.report.applications.command.deudaTrabajador.delete.DeleteDeudaTrabajadorCommand;
import com.kynsoft.report.applications.command.deudaTrabajador.delete.DeleteDeudaTrabajadorMessage;
import com.kynsoft.report.applications.command.deudaTrabajador.update.UpdateDeudaTrabajadorCommand;
import com.kynsoft.report.applications.command.deudaTrabajador.update.UpdateDeudaTrabajadorMessage;
import com.kynsoft.report.applications.command.deudaTrabajador.update.UpdateDeudaTrabajadorRequest;
import com.kynsoft.report.applications.query.deudaTrabajador.getById.FindDeudaTrabajadorByIdQuery;
import com.kynsoft.report.applications.query.deudaTrabajador.search.GetSearchDeudaTrabajadorQuery;
import com.kynsoft.report.applications.query.responseObject.DeudaTrabajadorResponse;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDetalleDto;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDto;
import com.kynsoft.report.domain.dto.FormaPago;
import com.kynsoft.report.domain.dto.MovimientoDeudaResult;
import com.kynsoft.report.domain.dto.PagoDeudaDto;
import com.kynsoft.report.domain.services.IDeudaTrabajadorDetalleService;
import com.kynsoft.report.domain.services.IDeudaTrabajadorService;
import com.kynsoft.report.domain.services.IPagoDeudaService;
import com.kynsoft.report.infrastructure.services.ReciboPdfService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/deuda-trabajador")
public class DeudaTrabajadorController {

    private final IMediator mediator;
    private final IDeudaTrabajadorService deudaService;
    private final IDeudaTrabajadorDetalleService detalleService;
    private final IPagoDeudaService pagoDeudaService;
    private final ReciboPdfService reciboPdfService;

    public DeudaTrabajadorController(IMediator mediator,
                                      IDeudaTrabajadorService deudaService,
                                      IDeudaTrabajadorDetalleService detalleService,
                                      IPagoDeudaService pagoDeudaService,
                                      ReciboPdfService reciboPdfService) {
        this.mediator = mediator;
        this.deudaService = deudaService;
        this.detalleService = detalleService;
        this.pagoDeudaService = pagoDeudaService;
        this.reciboPdfService = reciboPdfService;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateDeudaTrabajadorRequest request) {
        CreateDeudaTrabajadorCommand createCommand = CreateDeudaTrabajadorCommand.fromRequest(request);
        CreateDeudaTrabajadorMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{trabajadorId}")
    public ResponseEntity<?> findByTrabajadorId(@PathVariable UUID trabajadorId) {
        DeudaTrabajadorDto deuda = deudaService.findByTrabajadorId(trabajadorId);
        if (deuda != null) {
            return ResponseEntity.ok(new DeudaTrabajadorResponse(deuda));
        }
        // Retornar deuda en 0 si no existe
        return ResponseEntity.ok(DeudaTrabajadorResponse.builder()
                .trabajadorId(trabajadorId)
                .importe(0.0)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateDeudaTrabajadorRequest request) {
        request.setId(id);
        UpdateDeudaTrabajadorCommand updateCommand = UpdateDeudaTrabajadorCommand.fromRequest(request);
        UpdateDeudaTrabajadorMessage response = mediator.send(updateCommand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteDeudaTrabajadorCommand deleteCommand = new DeleteDeudaTrabajadorCommand(id);
        DeleteDeudaTrabajadorMessage response = mediator.send(deleteCommand);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchDeudaTrabajadorQuery query = new GetSearchDeudaTrabajadorQuery(pageable, request.getFilter());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{trabajadorId}/detalle")
    public ResponseEntity<List<DeudaTrabajadorDetalleDto>> getDetalles(@PathVariable UUID trabajadorId) {
        List<DeudaTrabajadorDetalleDto> detalles = detalleService.findByTrabajadorId(trabajadorId);
        return ResponseEntity.ok(detalles);
    }

    @PostMapping("/pago")
    public ResponseEntity<MovimientoDeudaResult> registrarPago(@RequestBody RegistrarPagoRequest request) {
        MovimientoDeudaResult result = deudaService.registrarPago(
                request.getTrabajadorId(),
                request.getMonto(),
                request.getFormaPago(),
                request.getReferenciaBancaria(),
                request.getObservaciones()
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping("/ajuste")
    public ResponseEntity<MovimientoDeudaResult> registrarAjuste(@RequestBody RegistrarAjusteRequest request) {
        MovimientoDeudaResult result = deudaService.registrarAjuste(
                request.getTrabajadorId(),
                request.getMonto(),
                request.getObservaciones()
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping("/carga-inicial")
    public ResponseEntity<MovimientoDeudaResult> registrarCargaInicial(@RequestBody RegistrarCargaInicialRequest request) {
        MovimientoDeudaResult result = deudaService.registrarCargaInicial(
                request.getTrabajadorId(),
                request.getMonto(),
                request.getObservaciones()
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping("/historial/search")
    public ResponseEntity<PaginatedResponse> searchHistorial(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        PaginatedResponse data = detalleService.search(pageable, request.getFilter());
        return ResponseEntity.ok(data);
    }

    @GetMapping("/pago/{id}/comprobante")
    public ResponseEntity<byte[]> descargarComprobante(@PathVariable UUID id) {
        try {
            PagoDeudaDto pago = pagoDeudaService.findById(id);
            byte[] pdf = reciboPdfService.generarComprobante(pago);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Recibo_" + pago.getNumeroRecibo() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{trabajadorId}/pagos")
    public ResponseEntity<List<PagoDeudaDto>> getHistorialPagos(@PathVariable UUID trabajadorId) {
        List<PagoDeudaDto> pagos = pagoDeudaService.findByTrabajadorId(trabajadorId);
        return ResponseEntity.ok(pagos);
    }

    // Request classes
    @Getter
    @Setter
    public static class RegistrarPagoRequest {
        private UUID trabajadorId;
        private Double monto;
        private FormaPago formaPago;
        private String referenciaBancaria;
        private String observaciones;
    }

    @Getter
    @Setter
    public static class RegistrarAjusteRequest {
        private UUID trabajadorId;
        private Double monto;
        private String observaciones;
    }

    @Getter
    @Setter
    public static class RegistrarCargaInicialRequest {
        private UUID trabajadorId;
        private Double monto;
        private String observaciones;
    }
}
