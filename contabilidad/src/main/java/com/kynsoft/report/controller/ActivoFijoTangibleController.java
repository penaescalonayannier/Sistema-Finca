package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.ActivoFijoTangibleDto;
import com.kynsoft.report.domain.services.IActivoFijoTangibleService;
import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller REST para Activos Fijos Tangibles.
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP): Control y registro de activos fijos
 * - Resolución 60/2011 CGR: Control interno de recursos
 */
@RestController
@RequestMapping("/api/activo-fijo")
public class ActivoFijoTangibleController {

    private final IActivoFijoTangibleService activoService;

    public ActivoFijoTangibleController(IActivoFijoTangibleService activoService) {
        this.activoService = activoService;
    }

    @PostMapping("")
    public ResponseEntity<ActivoFijoTangibleDto> create(@RequestBody ActivoFijoTangibleDto dto) {
        ActivoFijoTangibleDto created = activoService.create(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivoFijoTangibleDto> findById(@PathVariable UUID id) {
        return activoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/inventario/{numeroInventario}")
    public ResponseEntity<ActivoFijoTangibleDto> findByNumeroInventario(
            @PathVariable String numeroInventario) {
        return activoService.findByNumeroInventario(numeroInventario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivoFijoTangibleDto> update(@PathVariable UUID id,
                                                         @RequestBody ActivoFijoTangibleDto dto) {
        dto.setId(id);
        ActivoFijoTangibleDto updated = activoService.update(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        activoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<Page<ActivoFijoTangibleDto>> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        String query = request.getQuery();

        // Simple search without filter processing
        Page<ActivoFijoTangibleDto> result = activoService.search(query, null, null, null, pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * Search with filters via query parameters.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ActivoFijoTangibleDto>> searchWithParams(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) UUID grupoId,
            @RequestParam(required = false) UUID fincaId,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        Page<ActivoFijoTangibleDto> result = activoService.search(query, grupoId, fincaId, activo, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<ActivoFijoTangibleDto>> findByGrupo(@PathVariable UUID grupoId) {
        List<ActivoFijoTangibleDto> activos = activoService.findByGrupo(grupoId);
        return ResponseEntity.ok(activos);
    }

    @GetMapping("/finca/{fincaId}")
    public ResponseEntity<List<ActivoFijoTangibleDto>> findByFinca(@PathVariable UUID fincaId) {
        List<ActivoFijoTangibleDto> activos = activoService.findByFinca(fincaId);
        return ResponseEntity.ok(activos);
    }

    /**
     * Dar de baja un activo fijo según Resolución 60/2011 CGR.
     */
    @PostMapping("/{id}/baja")
    public ResponseEntity<ActivoFijoTangibleDto> darDeBaja(@PathVariable UUID id,
                                                           @RequestBody Map<String, String> body) {
        String motivo = body.get("motivo");
        if (motivo == null || motivo.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        ActivoFijoTangibleDto result = activoService.darDeBaja(id, motivo);
        return ResponseEntity.ok(result);
    }
}
