package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.producto.create.CreateProductoCommand;
import com.kynsoft.report.applications.command.producto.create.CreateProductoMessage;
import com.kynsoft.report.applications.command.producto.create.CreateProductoRequest;
import com.kynsoft.report.applications.command.producto.delete.DeleteProductoCommand;
import com.kynsoft.report.applications.command.producto.delete.DeleteProductoMessage;
import com.kynsoft.report.applications.command.producto.update.UpdateProductoCommand;
import com.kynsoft.report.applications.command.producto.update.UpdateProductoMessage;
import com.kynsoft.report.applications.command.producto.update.UpdateProductoRequest;
import com.kynsoft.report.applications.query.producto.getById.FindProductoByIdQuery;
import com.kynsoft.report.applications.query.producto.search.GetSearchProductoQuery;
import com.kynsoft.report.applications.query.responseObject.ProductoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/producto")
public class ProductoController {

    private final IMediator mediator;

    public ProductoController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateProductoRequest request) {
        CreateProductoCommand createCommand = CreateProductoCommand.fromRequest(request);
        CreateProductoMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> findById(@PathVariable UUID id) {
        FindProductoByIdQuery query = new FindProductoByIdQuery(id);
        ProductoResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateProductoRequest request) {
        UpdateProductoCommand updateCommand = UpdateProductoCommand.fromRequest(request, id);
        UpdateProductoMessage response = mediator.send(updateCommand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteProductoCommand deleteCommand = new DeleteProductoCommand(id);
        DeleteProductoMessage response = mediator.send(deleteCommand);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchProductoQuery query = new GetSearchProductoQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}