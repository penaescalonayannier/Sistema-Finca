package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.grupo.create.CreateGrupoCommand;
import com.kynsoft.report.applications.command.grupo.create.CreateGrupoMessage;
import com.kynsoft.report.applications.command.grupo.create.CreateGrupoRequest;
import com.kynsoft.report.applications.command.grupo.delete.DeleteGrupoCommand;
import com.kynsoft.report.applications.command.grupo.delete.DeleteGrupoMessage;
import com.kynsoft.report.applications.command.grupo.update.UpdateGrupoCommand;
import com.kynsoft.report.applications.command.grupo.update.UpdateGrupoMessage;
import com.kynsoft.report.applications.command.grupo.update.UpdateGrupoRequest;
import com.kynsoft.report.applications.query.grupo.get.GetGrupoQuery;
import com.kynsoft.report.applications.query.grupo.search.SearchGrupoQuery;
import com.kynsoft.report.applications.query.responseObject.GrupoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/grupo")
public class GrupoController {

    private final IMediator mediator;

    public GrupoController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateGrupoRequest request) {
        CreateGrupoCommand createCommand = CreateGrupoCommand.fromRequest(request);
        CreateGrupoMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponse> findById(@PathVariable UUID id) {
        GetGrupoQuery query = new GetGrupoQuery(id);
        GrupoResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateGrupoRequest request) {
        request.setId(id);
        UpdateGrupoCommand updateCommand = UpdateGrupoCommand.fromRequest(request);
        UpdateGrupoMessage response = mediator.send(updateCommand);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteGrupoCommand deleteCommand = new DeleteGrupoCommand(id);
        DeleteGrupoMessage response = mediator.send(deleteCommand);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        SearchGrupoQuery query = new SearchGrupoQuery(pageable, request.getFilter());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/{grupoId}/asignar-trabajador")
    public ResponseEntity<?> asignarTrabajador(@PathVariable UUID grupoId, @RequestBody AsignarTrabajadorRequest request) {
        request.setGrupoId(grupoId);
        // Retornar confirmación de asignación
        // La asignación real se realiza desde el frontend usando TrabajadoresController.asignarGrupo()
        return ResponseEntity.ok(new java.util.HashMap<String, Object>(){{
            put("grupoId", grupoId);
            put("trabajadorId", request.getTrabajadorId());
            put("mensaje", "Trabajador asignado al grupo");
        }});
    }

    public static class AsignarTrabajadorRequest {
        private UUID grupoId;
        private UUID trabajadorId;

        public UUID getGrupoId() {
            return grupoId;
        }

        public void setGrupoId(UUID grupoId) {
            this.grupoId = grupoId;
        }

        public UUID getTrabajadorId() {
            return trabajadorId;
        }

        public void setTrabajadorId(UUID trabajadorId) {
            this.trabajadorId = trabajadorId;
        }
    }
}
