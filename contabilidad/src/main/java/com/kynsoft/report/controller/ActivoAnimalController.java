package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.ActivoAnimalDto;
import com.kynsoft.report.domain.dto.enums.CategoriaAnimal;
import com.kynsoft.report.domain.dto.enums.TipoGanado;
import com.kynsoft.report.domain.services.IActivoAnimalService;
import com.kynsoft.report.infrastructure.services.ResumenAnimalDto;
import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para Activos Animales (Grupo 08).
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP): Grupo 08 - Animales
 * - Resolución 51/2021 MFP: Tasa de depreciación 20%
 */
@RestController
@RequestMapping("/api/activo-animal")
public class ActivoAnimalController {

    private final IActivoAnimalService animalService;

    public ActivoAnimalController(IActivoAnimalService animalService) {
        this.animalService = animalService;
    }

    @PostMapping("")
    public ResponseEntity<ActivoAnimalDto> create(@RequestBody ActivoAnimalDto dto) {
        ActivoAnimalDto created = animalService.create(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivoAnimalDto> findById(@PathVariable UUID id) {
        return animalService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivoAnimalDto> update(@PathVariable UUID id,
                                                   @RequestBody ActivoAnimalDto dto) {
        dto.setId(id);
        ActivoAnimalDto updated = animalService.update(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        animalService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<Page<ActivoAnimalDto>> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        String query = request.getQuery();

        // Simple search without filter processing - use query parameter endpoints for filtering
        Page<ActivoAnimalDto> result = animalService.search(query, null, null, null, pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * Search with filters via query parameters.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ActivoAnimalDto>> searchWithParams(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) TipoGanado tipoGanado,
            @RequestParam(required = false) CategoriaAnimal categoria,
            @RequestParam(required = false) UUID fincaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        Page<ActivoAnimalDto> result = animalService.search(query, tipoGanado, categoria, fincaId, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tipo/{tipoGanado}")
    public ResponseEntity<List<ActivoAnimalDto>> findByTipoGanado(
            @PathVariable TipoGanado tipoGanado) {
        List<ActivoAnimalDto> animales = animalService.findByTipoGanado(tipoGanado);
        return ResponseEntity.ok(animales);
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ActivoAnimalDto>> findByCategoria(
            @PathVariable CategoriaAnimal categoria) {
        List<ActivoAnimalDto> animales = animalService.findByCategoria(categoria);
        return ResponseEntity.ok(animales);
    }

    @GetMapping("/finca/{fincaId}")
    public ResponseEntity<List<ActivoAnimalDto>> findByFinca(@PathVariable UUID fincaId) {
        List<ActivoAnimalDto> animales = animalService.findByFinca(fincaId);
        return ResponseEntity.ok(animales);
    }

    /**
     * Resumen de animales por categoría para una finca.
     */
    @GetMapping("/finca/{fincaId}/resumen")
    public ResponseEntity<List<ResumenAnimalDto>> getResumenPorCategoria(
            @PathVariable UUID fincaId) {
        List<ResumenAnimalDto> resumen = animalService.getResumenPorCategoria(fincaId);
        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/categorias")
    public ResponseEntity<CategoriaAnimal[]> getCategorias() {
        return ResponseEntity.ok(CategoriaAnimal.values());
    }

    @GetMapping("/tipos-ganado")
    public ResponseEntity<TipoGanado[]> getTiposGanado() {
        return ResponseEntity.ok(TipoGanado.values());
    }
}
