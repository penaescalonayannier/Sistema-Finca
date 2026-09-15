package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.fincaproducto.asignar.AsignarProductoAFincaCommand;
import com.kynsoft.report.applications.command.fincaproducto.asignar.AsignarProductoAFincaRequest;
import com.kynsoft.report.applications.command.fincaproducto.asignar.AsignarProductoAFincaMessage;
import com.kynsoft.report.applications.command.fincaproducto.actualizar.ActualizarStockFincaProductoCommand;
import com.kynsoft.report.applications.command.fincaproducto.actualizar.ActualizarStockFincaProductoRequest;
import com.kynsoft.report.applications.command.fincaproducto.actualizar.ActualizarStockFincaProductoMessage;
import com.kynsoft.report.applications.command.fincaproducto.entradaproduccion.EntradaProduccionCommand;
import com.kynsoft.report.applications.command.fincaproducto.entradaproduccion.EntradaProduccionRequest;
import com.kynsoft.report.applications.command.fincaproducto.entradaproduccion.EntradaProduccionMessage;
import com.kynsoft.report.applications.command.fincaproducto.entradafactura.EntradaFacturaCommand;
import com.kynsoft.report.applications.command.fincaproducto.entradafactura.EntradaFacturaRequest;
import com.kynsoft.report.applications.command.fincaproducto.entradafactura.EntradaFacturaMessage;
import com.kynsoft.report.applications.command.fincaproducto.entradaconduce.EntradaConduceCommand;
import com.kynsoft.report.applications.command.fincaproducto.entradaconduce.EntradaConduceRequest;
import com.kynsoft.report.applications.command.fincaproducto.entradaconduce.EntradaConduceMessage;
import com.kynsoft.report.applications.command.fincaproducto.ajuste.AjusteStockCommand;
import com.kynsoft.report.applications.command.fincaproducto.ajuste.AjusteStockRequest;
import com.kynsoft.report.applications.command.fincaproducto.ajuste.AjusteStockMessage;
import com.kynsoft.report.applications.command.fincaproducto.remover.RemoverProductoDeFincaCommand;
import com.kynsoft.report.applications.command.fincaproducto.remover.RemoverProductoDeFincaRequest;
import com.kynsoft.report.applications.command.fincaproducto.remover.RemoverProductoDeFincaMessage;
import com.kynsoft.report.applications.query.fincaproducto.getall.GetAllFincaProductoQuery;
import com.kynsoft.report.applications.query.fincaproducto.getbyid.GetFincaProductoByIdQuery;
import com.kynsoft.report.applications.query.fincaproducto.alertas.GetAlertasStockBajoQuery;
import com.kynsoft.report.applications.query.fincaproducto.getproductos.GetProductosDeFincaQuery;
import com.kynsoft.report.applications.query.responseObject.FincaProductoListResponse;
import com.kynsoft.report.applications.query.responseObject.FincaProductoResponse;
import com.kynsoft.report.domain.dto.EstadoStock;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import com.kynsoft.report.domain.dto.ResumenAlertasDto;
import com.kynsoft.report.domain.services.IFincaProductoService;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finca-producto")
public class FincaProductoController {

    private final IMediator mediator;
    private final IFincaProductoService fincaProductoService;

    public FincaProductoController(IMediator mediator, IFincaProductoService fincaProductoService) {
        this.mediator = mediator;
        this.fincaProductoService = fincaProductoService;
    }

    // ==================== COMMANDS ====================
    @PostMapping("/asignar")
    public ResponseEntity<AsignarProductoAFincaMessage> asignarProductoAFinca(
            @RequestBody AsignarProductoAFincaRequest request) {
        AsignarProductoAFincaCommand command = AsignarProductoAFincaCommand.fromRequest(request);
        AsignarProductoAFincaMessage response = mediator.send(command);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/stock")
    public ResponseEntity<ActualizarStockFincaProductoMessage> actualizarStock(
            @RequestBody ActualizarStockFincaProductoRequest request) {
        ActualizarStockFincaProductoCommand command = ActualizarStockFincaProductoCommand.fromRequest(request);
        ActualizarStockFincaProductoMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/entrada-produccion")
    public ResponseEntity<EntradaProduccionMessage> entradaProduccion(
            @RequestBody EntradaProduccionRequest request) {
        EntradaProduccionCommand command = EntradaProduccionCommand.fromRequest(request);
        EntradaProduccionMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remover")
    public ResponseEntity<RemoverProductoDeFincaMessage> removerProductoDeFinca(
            @RequestBody RemoverProductoDeFincaRequest request) {
        RemoverProductoDeFincaCommand command = RemoverProductoDeFincaCommand.fromRequest(request);
        RemoverProductoDeFincaMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> searchFincaProductos(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetAllFincaProductoQuery query = new GetAllFincaProductoQuery(
                pageable,
                request.getFilter(),
                request.getQuery()
        );

        PaginatedResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/finca/{fincaId}/productos/activos")
    public ResponseEntity<FincaProductoListResponse> obtenerProductosActivosDeFinca(@PathVariable UUID fincaId) {
        GetProductosDeFincaQuery query = new GetProductosDeFincaQuery(fincaId);
        FincaProductoListResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    // ==================== NEW ENDPOINTS ====================

    @GetMapping("/{id}")
    public ResponseEntity<FincaProductoResponse> getById(@PathVariable UUID id) {
        GetFincaProductoByIdQuery query = new GetFincaProductoByIdQuery(id);
        FincaProductoResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/entrada-factura")
    public ResponseEntity<EntradaFacturaMessage> entradaFactura(
            @PathVariable UUID id,
            @RequestBody EntradaFacturaRequest request) {
        EntradaFacturaCommand command = EntradaFacturaCommand.fromRequest(id, request);
        EntradaFacturaMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/entrada-conduce")
    public ResponseEntity<EntradaConduceMessage> entradaConduce(
            @PathVariable UUID id,
            @RequestBody EntradaConduceRequest request) {
        EntradaConduceCommand command = EntradaConduceCommand.fromRequest(id, request);
        EntradaConduceMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/ajuste")
    public ResponseEntity<AjusteStockMessage> ajusteStock(
            @PathVariable UUID id,
            @RequestBody AjusteStockRequest request) {
        AjusteStockCommand command = AjusteStockCommand.fromRequest(id, request);
        AjusteStockMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/alertas")
    public ResponseEntity<PaginatedResponse> getAlertasStockBajo(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        GetAlertasStockBajoQuery query = new GetAlertasStockBajoQuery(pageable);
        PaginatedResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/alertas/resumen")
    public ResponseEntity<ResumenAlertasDto> getResumenAlertas(
            @RequestParam(required = false) UUID fincaId,
            @RequestParam(required = false) EstadoStock estado,
            @RequestParam(defaultValue = "50") int limit) {
        ResumenAlertasDto resumen = fincaProductoService.getResumenAlertas(fincaId, estado, limit);
        return ResponseEntity.ok(resumen);
    }

    @PatchMapping("/{id}/stock-minmax")
    public ResponseEntity<FincaProductoResponse> actualizarStockMinMax(
            @PathVariable UUID id,
            @RequestParam(required = false) Integer stockMinimo,
            @RequestParam(required = false) Integer stockMaximo) {
        FincaProductoDto dto = fincaProductoService.actualizarStockMinMax(id, stockMinimo, stockMaximo);
        FincaProductoResponse response = new FincaProductoResponse(dto);
        return ResponseEntity.ok(response);
    }
}
