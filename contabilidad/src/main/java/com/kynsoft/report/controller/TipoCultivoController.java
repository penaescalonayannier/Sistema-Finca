package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.CategoriaTipoCultivo;
import com.kynsoft.report.domain.dto.TipoCultivoDto;
import com.kynsoft.report.domain.services.ITipoCultivoService;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tipo-cultivo")
public class TipoCultivoController {

    private final ITipoCultivoService service;

    public TipoCultivoController(ITipoCultivoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody TipoCultivoRequest request) {
        UUID id = UUID.randomUUID();
        TipoCultivoDto dto = TipoCultivoDto.builder()
                .id(id)
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .requiereCampo(request.getRequiereCampo())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .orden(request.getOrden())
                .build();

        service.create(dto);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody TipoCultivoRequest request) {
        TipoCultivoDto dto = TipoCultivoDto.builder()
                .id(id)
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .requiereCampo(request.getRequiereCampo())
                .activo(request.getActivo())
                .orden(request.getOrden())
                .build();

        service.update(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoCultivoDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        List<FilterCriteria> filterCriteria = request.getFilter();

        return ResponseEntity.ok(service.search(pageable, filterCriteria));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TipoCultivoDto>> findAllActive() {
        return ResponseEntity.ok(service.findAllActive());
    }

    @GetMapping("/by-categoria/{categoria}")
    public ResponseEntity<List<TipoCultivoDto>> findActiveByCategoria(@PathVariable CategoriaTipoCultivo categoria) {
        return ResponseEntity.ok(service.findActiveByCategoria(categoria));
    }

    // Inner class for request
    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TipoCultivoRequest {
        private String codigo;
        private String nombre;
        private String descripcion;
        private CategoriaTipoCultivo categoria;
        private Boolean requiereCampo;
        private Boolean activo;
        private Integer orden;
    }
}
