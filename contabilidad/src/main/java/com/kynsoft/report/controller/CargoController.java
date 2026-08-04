package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.cargo.create.CreateCargoCommand;
import com.kynsoft.report.applications.command.cargo.create.CreateCargoMessage;
import com.kynsoft.report.applications.command.cargo.create.CreateCargoRequest;
import com.kynsoft.report.applications.command.cargo.delete.DeleteCargoCommand;
import com.kynsoft.report.applications.command.cargo.delete.DeleteCargoMessage;
import com.kynsoft.report.applications.command.cargo.update.UpdateCargoCommand;
import com.kynsoft.report.applications.command.cargo.update.UpdateCargoMessage;
import com.kynsoft.report.applications.command.cargo.update.UpdateCargoRequest;
import com.kynsoft.report.applications.query.cargo.get.GetCargoQuery;
import com.kynsoft.report.applications.query.cargo.search.SearchCargoQuery;
import com.kynsoft.report.applications.query.responseObject.CargoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cargo")
public class CargoController {

    private final IMediator mediator;

    public CargoController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateCargoRequest request) {
        CreateCargoCommand createCommand = CreateCargoCommand.fromRequest(request);
        CreateCargoMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoResponse> findById(@PathVariable UUID id) {
        GetCargoQuery query = new GetCargoQuery(id);
        CargoResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateCargoRequest request) {
        request.setId(id);
        UpdateCargoCommand updateCommand = UpdateCargoCommand.fromRequest(request);
        UpdateCargoMessage response = mediator.send(updateCommand);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteCargoCommand deleteCommand = new DeleteCargoCommand(id);
        DeleteCargoMessage response = mediator.send(deleteCommand);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        SearchCargoQuery query = new SearchCargoQuery(pageable, request.getFilter());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
