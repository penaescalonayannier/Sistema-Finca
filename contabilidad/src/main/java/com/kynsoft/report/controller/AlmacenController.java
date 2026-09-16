package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.almacen.create.CreateAlmacenCommand;
import com.kynsoft.report.applications.command.almacen.create.CreateAlmacenMessage;
import com.kynsoft.report.applications.command.almacen.create.CreateAlmacenRequest;
import com.kynsoft.report.applications.command.almacen.delete.DeleteAlmacenCommand;
import com.kynsoft.report.applications.command.almacen.delete.DeleteAlmacenMessage;
import com.kynsoft.report.applications.command.almacen.update.UpdateAlmacenCommand;
import com.kynsoft.report.applications.command.almacen.update.UpdateAlmacenMessage;
import com.kynsoft.report.applications.command.almacen.update.UpdateAlmacenRequest;
import com.kynsoft.report.applications.command.almacen.reactivar.ReactivarAlmacenCommand;
import com.kynsoft.report.applications.command.almacen.reactivar.ReactivarAlmacenMessage;
import com.kynsoft.report.applications.command.almacen.establecerprincipal.EstablecerPrincipalCommand;
import com.kynsoft.report.applications.command.almacen.establecerprincipal.EstablecerPrincipalMessage;
import com.kynsoft.report.applications.command.almacenproducto.entrada.EntradaAlmacenCommand;
import com.kynsoft.report.applications.command.almacenproducto.entrada.EntradaAlmacenMessage;
import com.kynsoft.report.applications.command.almacenproducto.entrada.EntradaAlmacenRequest;
import com.kynsoft.report.applications.command.almacenproducto.produccionterminada.EntradaProduccionTerminadaAlmacenCommand;
import com.kynsoft.report.applications.command.almacenproducto.produccionterminada.EntradaProduccionTerminadaAlmacenMessage;
import com.kynsoft.report.applications.command.almacenproducto.produccionterminada.EntradaProduccionTerminadaAlmacenRequest;
import com.kynsoft.report.applications.command.almacenproducto.salida.SalidaAlmacenCommand;
import com.kynsoft.report.applications.command.almacenproducto.salida.SalidaAlmacenMessage;
import com.kynsoft.report.applications.command.almacenproducto.salida.SalidaAlmacenRequest;
import com.kynsoft.report.applications.command.almacenproducto.salidamultiple.SalidaMultipleAlmacenCommand;
import com.kynsoft.report.applications.command.almacenproducto.salidamultiple.SalidaMultipleAlmacenMessage;
import com.kynsoft.report.applications.command.almacenproducto.salidamultiple.SalidaMultipleAlmacenRequest;
import com.kynsoft.report.applications.command.almacenproducto.transferencia.TransferenciaAlmacenCommand;
import com.kynsoft.report.applications.command.almacenproducto.transferencia.TransferenciaAlmacenMessage;
import com.kynsoft.report.applications.command.almacenproducto.transferencia.TransferenciaAlmacenRequest;
import com.kynsoft.report.applications.query.almacen.getById.FindAlmacenByIdQuery;
import com.kynsoft.report.applications.query.almacen.search.GetSearchAlmacenQuery;
import com.kynsoft.report.applications.query.almacen.porfinca.GetAlmacenesPorFincaQuery;
import com.kynsoft.report.applications.query.responseObject.AlmacenFincaProductoResponse;
import com.kynsoft.report.applications.query.responseObject.AlmacenResponse;
import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.services.IAlmacenFincaProductoService;
import com.kynsoft.report.domain.services.IAlmacenService;
import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.response.ErrorField;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import java.util.UUID;

@RestController
@RequestMapping("/api/almacen")
public class AlmacenController {

    private final IMediator mediator;
    private final IAlmacenService almacenService;
    private final IAlmacenFincaProductoService almacenFincaProductoService;

    public AlmacenController(IMediator mediator, IAlmacenService almacenService,
                              IAlmacenFincaProductoService almacenFincaProductoService) {
        this.mediator = mediator;
        this.almacenService = almacenService;
        this.almacenFincaProductoService = almacenFincaProductoService;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateAlmacenRequest request) {
        CreateAlmacenCommand createCommand = CreateAlmacenCommand.fromRequest(request);
        CreateAlmacenMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlmacenResponse> findById(@PathVariable UUID id) {
        FindAlmacenByIdQuery query = new FindAlmacenByIdQuery(id);
        AlmacenResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateAlmacenRequest request) {
        UpdateAlmacenCommand updateCommand = UpdateAlmacenCommand.fromRequest(request, id);
        UpdateAlmacenMessage response = mediator.send(updateCommand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteAlmacenCommand deleteCommand = new DeleteAlmacenCommand(id);
        DeleteAlmacenMessage response = mediator.send(deleteCommand);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchAlmacenQuery query = new GetSearchAlmacenQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/{id}/reactivar")
    public ResponseEntity<?> reactivar(@PathVariable UUID id) {
        ReactivarAlmacenCommand command = new ReactivarAlmacenCommand(id);
        ReactivarAlmacenMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/establecer-principal")
    public ResponseEntity<?> establecerPrincipal(@PathVariable UUID id) {
        EstablecerPrincipalCommand command = new EstablecerPrincipalCommand(id);
        EstablecerPrincipalMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/por-finca/{fincaId}")
    public ResponseEntity<PaginatedResponse> findByFinca(@PathVariable UUID fincaId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        GetAlmacenesPorFincaQuery query = new GetAlmacenesPorFincaQuery(fincaId, pageable);
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    // Endpoints para manejo de productos

    @PostMapping("/{almacenId}/productos")
    public ResponseEntity<?> addProducto(@PathVariable UUID almacenId, @RequestBody AddProductoRequest request) {
        almacenService.addProducto(almacenId, request.getFincaProductoId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{almacenId}/productos/{fincaProductoId}")
    public ResponseEntity<?> removeProducto(@PathVariable UUID almacenId, @PathVariable UUID fincaProductoId) {
        almacenService.removeProducto(almacenId, fincaProductoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{almacenId}/productos")
    public ResponseEntity<AlmacenResponse> getProductos(@PathVariable UUID almacenId) {
        FindAlmacenByIdQuery query = new FindAlmacenByIdQuery(almacenId);
        AlmacenResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    // ==================== STOCK POR ALMACÉN ====================

    @GetMapping("/{almacenId}/stock")
    public ResponseEntity<List<AlmacenFincaProductoResponse>> getStockAlmacen(@PathVariable UUID almacenId) {
        List<AlmacenFincaProductoDto> productos = almacenFincaProductoService.findByAlmacenId(almacenId);
        List<AlmacenFincaProductoResponse> response = productos.stream()
                .map(AlmacenFincaProductoResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/finca-producto/{fincaProductoId}/almacenes")
    public ResponseEntity<List<AlmacenFincaProductoResponse>> getAlmacenesPorProductoFinca(
            @PathVariable UUID fincaProductoId) {
        List<AlmacenFincaProductoResponse> response = almacenFincaProductoService
                .findByFincaProductoId(fincaProductoId)
                .stream()
                .map(AlmacenFincaProductoResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{almacenId}/stock/total")
    public ResponseEntity<Double> getStockTotalAlmacen(@PathVariable UUID almacenId) {
        Double total = almacenFincaProductoService.getStockTotalAlmacen(almacenId);
        return ResponseEntity.ok(total);
    }

    // ==================== ENTRADAS ====================

    @PostMapping("/{almacenId}/entrada")
    public ResponseEntity<?> entradaStock(@PathVariable UUID almacenId,
                                           @RequestBody EntradaAlmacenRequest request) {
        validarProductoPerteneceAlmacen(almacenId, request.getAlmacenFincaProductoId());
        EntradaAlmacenCommand command = EntradaAlmacenCommand.fromRequest(request);
        EntradaAlmacenMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Registra una producción terminada directamente en el almacén. La operación
     * crea el documento y actualiza el inventario físico y el de finca una sola vez.
     */
    @PostMapping("/{almacenId}/entrada-produccion-terminada")
    public ResponseEntity<EntradaProduccionTerminadaAlmacenMessage> entradaProduccionTerminada(
            @PathVariable UUID almacenId,
            @RequestBody EntradaProduccionTerminadaAlmacenRequest request) {
        EntradaProduccionTerminadaAlmacenCommand command =
                EntradaProduccionTerminadaAlmacenCommand.fromRequest(almacenId, request);
        EntradaProduccionTerminadaAlmacenMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    // ==================== SALIDAS ====================

    @PostMapping("/{almacenId}/salida")
    public ResponseEntity<?> salidaStock(@PathVariable UUID almacenId,
                                          @RequestBody SalidaAlmacenRequest request) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Toda salida debe generar un vale o factura; use salida-multiple incluso para un solo producto.");
    }

    @PostMapping("/{almacenId}/salida-multiple")
    public ResponseEntity<SalidaMultipleAlmacenMessage> salidaMultiple(
            @PathVariable UUID almacenId,
            @RequestBody SalidaMultipleAlmacenRequest request) {
        SalidaMultipleAlmacenCommand command = SalidaMultipleAlmacenCommand.fromRequest(almacenId, request);
        SalidaMultipleAlmacenMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    // ==================== TRANSFERENCIAS ====================

    @PostMapping("/{almacenId}/transferir")
    public ResponseEntity<?> transferirStock(@PathVariable UUID almacenId,
                                              @RequestBody TransferenciaAlmacenRequest request) {
        validarProductoPerteneceAlmacen(almacenId, request.getAlmacenFincaProductoId());
        TransferenciaAlmacenCommand command = TransferenciaAlmacenCommand.fromRequest(request);
        TransferenciaAlmacenMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{almacenId}/productos/{fincaProductoId}/destinos-disponibles")
    public ResponseEntity<List<AlmacenFincaProductoResponse>> getDestinosDisponibles(
            @PathVariable UUID almacenId, @PathVariable UUID fincaProductoId) {
        List<AlmacenFincaProductoDto> destinos = almacenFincaProductoService
                .getAlmacenesDestinoDisponibles(almacenId, fincaProductoId);
        List<AlmacenFincaProductoResponse> response = destinos.stream()
                .map(AlmacenFincaProductoResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // ==================== ASIGNAR PRODUCTO CON STOCK ====================

    @PostMapping("/{almacenId}/asignar-producto")
    public ResponseEntity<?> asignarProductoConStock(@PathVariable UUID almacenId,
                                                      @RequestBody AsignarProductoStockRequest request) {
        UUID id = almacenFincaProductoService.asignarProducto(
                almacenId,
                request.getFincaProductoId(),
                request.getStockInicial(),
                request.getStockMinimo(),
                request.getStockMaximo()
        );
        return ResponseEntity.ok(new AsignarProductoResponse(id));
    }

    // Request class para agregar producto
    @lombok.Getter
    @lombok.Setter
    public static class AddProductoRequest {
        private UUID fincaProductoId;
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AsignarProductoStockRequest {
        private UUID fincaProductoId;
        private Double stockInicial;
        private Double stockMinimo;
        private Double stockMaximo;
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class AsignarProductoResponse {
        private UUID id;
    }

    /**
     * Evita que el identificador de un producto de otro almacén sea usado con
     * una URL diferente. Además de ser una validación de seguridad, preserva
     * la trazabilidad física exigida para los movimientos de inventario.
     */
    private void validarProductoPerteneceAlmacen(UUID almacenId, UUID almacenFincaProductoId) {
        if (almacenFincaProductoId == null) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("almacenFincaProductoId", "Debe indicar el producto del almacén.")));
        }
        AlmacenFincaProductoDto productoAlmacen = almacenFincaProductoService.findById(almacenFincaProductoId);
        if (!almacenId.equals(productoAlmacen.getAlmacenId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("almacenFincaProductoId", "El producto no pertenece al almacén indicado en la operación.")));
        }
    }
}
