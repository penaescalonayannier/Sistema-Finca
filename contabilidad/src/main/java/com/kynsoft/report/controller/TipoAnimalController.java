package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.TipoAnimalDto;
import com.kynsoft.report.domain.services.ITipoAnimalService;
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
@RequestMapping("/api/tipo-animal")
public class TipoAnimalController {

    private final ITipoAnimalService service;

    public TipoAnimalController(ITipoAnimalService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody TipoAnimalRequest request) {
        UUID id = UUID.randomUUID();
        TipoAnimalDto dto = TipoAnimalDto.builder()
                .id(id)
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .orden(request.getOrden())
                .build();

        service.create(dto);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody TipoAnimalRequest request) {
        TipoAnimalDto dto = TipoAnimalDto.builder()
                .id(id)
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
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
    public ResponseEntity<TipoAnimalDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        List<FilterCriteria> filterCriteria = request.getFilter();

        return ResponseEntity.ok(service.search(pageable, filterCriteria));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TipoAnimalDto>> findAllActive() {
        return ResponseEntity.ok(service.findAllActive());
    }

    // Inner class for request
    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TipoAnimalRequest {
        private String codigo;
        private String nombre;
        private String descripcion;
        private Boolean activo;
        private Integer orden;
    }
}
