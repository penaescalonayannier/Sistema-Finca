package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.labor.create.CreateLaborCommand;
import com.kynsoft.report.applications.command.labor.create.CreateLaborMessage;
import com.kynsoft.report.applications.command.labor.create.CreateLaborRequest;
import com.kynsoft.report.applications.command.labor.delete.DeleteLaborCommand;
import com.kynsoft.report.applications.command.labor.delete.DeleteLaborMessage;
import com.kynsoft.report.applications.command.labor.update.UpdateLaborCommand;
import com.kynsoft.report.applications.command.labor.update.UpdateLaborMessage;
import com.kynsoft.report.applications.command.labor.update.UpdateLaborRequest;
import com.kynsoft.report.applications.query.labor.getById.FindLaborByIdQuery;
import com.kynsoft.report.applications.query.labor.search.GetSearchLaborQuery;
import com.kynsoft.report.applications.query.responseObject.LaborResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/labor")
public class LaborController {

    private final IMediator mediator;

    public LaborController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateLaborRequest request) {
        CreateLaborCommand createCommand = CreateLaborCommand.fromRequest(request);
        CreateLaborMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaborResponse> findById(@PathVariable UUID id) {
        FindLaborByIdQuery query = new FindLaborByIdQuery(id);
        LaborResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateLaborRequest request) {
        UpdateLaborCommand updateCommand = UpdateLaborCommand.fromRequest(request, id);
        UpdateLaborMessage response = mediator.send(updateCommand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteLaborCommand deleteCommand = new DeleteLaborCommand(id);
        DeleteLaborMessage response = mediator.send(deleteCommand);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchLaborQuery query = new GetSearchLaborQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}