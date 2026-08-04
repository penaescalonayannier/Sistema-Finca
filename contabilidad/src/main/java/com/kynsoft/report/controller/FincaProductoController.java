package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.fincaproducto.asignar.AsignarProductoAFincaCommand;
import com.kynsoft.report.applications.command.fincaproducto.asignar.AsignarProductoAFincaRequest;
import com.kynsoft.report.applications.command.fincaproducto.asignar.AsignarProductoAFincaMessage;
import com.kynsoft.report.applications.command.fincaproducto.actualizar.ActualizarStockFincaProductoCommand;
import com.kynsoft.report.applications.command.fincaproducto.actualizar.ActualizarStockFincaProductoRequest;
import com.kynsoft.report.applications.command.fincaproducto.actualizar.ActualizarStockFincaProductoMessage;
import com.kynsoft.report.applications.query.fincaproducto.getall.GetAllFincaProductoQuery;
import com.kynsoft.report.applications.query.fincaproducto.getproductos.GetProductosDeFincaQuery;
import com.kynsoft.report.applications.query.responseObject.FincaProductoListResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finca-producto")
public class FincaProductoController {

    private final IMediator mediator;

    public FincaProductoController(IMediator mediator) {
        this.mediator = mediator;
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
        // Implementación pendiente - filtrar solo productos activos
        GetProductosDeFincaQuery query = new GetProductosDeFincaQuery(fincaId);
        FincaProductoListResponse response = mediator.send(query);

        // Filtrar productos activos
        return ResponseEntity.ok(response);
    }
}
