package com.kynsoft.report.controller;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.applications.query.responseObject.AuditoriaResponse;
import com.kynsoft.report.domain.dto.AuditoriaDto;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.services.IAuditoriaService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auditoria")
@PreAuthorize("hasRole('ADMIN')")
public class AuditoriaController {

    private final IAuditoriaService auditoriaService;

    public AuditoriaController(IAuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Sort.Direction direction = Sort.Direction.DESC;
        if (request.getSortType() != null && "ASC".equalsIgnoreCase(request.getSortType().name())) {
            direction = Sort.Direction.ASC;
        }
        Sort sort = Sort.by(
                direction,
                request.getSortBy() != null ? request.getSortBy() : "createdAt"
        );

        Pageable pageable = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getPageSize() != null ? request.getPageSize() : 20,
                sort
        );

        List<FilterCriteria> filters = request.getFilter() != null ? request.getFilter() : List.of();
        PaginatedResponse response = auditoriaService.search(pageable, filters);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditoriaResponse> getById(@PathVariable UUID id) {
        AuditoriaDto dto = auditoriaService.findById(id);
        return ResponseEntity.ok(new AuditoriaResponse(dto));
    }

    @GetMapping("/entidad/{entidad}/{entidadId}")
    public ResponseEntity<List<AuditoriaDto>> getHistorialEntidad(
            @PathVariable String entidad,
            @PathVariable UUID entidadId) {
        List<AuditoriaDto> historial = auditoriaService.getHistorialEntidad(entidad, entidadId);
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/entidades")
    public ResponseEntity<List<String>> getEntidades() {
        return ResponseEntity.ok(auditoriaService.getEntidades());
    }

    @GetMapping("/acciones")
    public ResponseEntity<TipoAccion[]> getAcciones() {
        return ResponseEntity.ok(TipoAccion.values());
    }
}
