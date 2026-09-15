package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.PlantacionPermanenteDto;
import com.kynsoft.report.domain.dto.enums.TipoCepa;
import com.kynsoft.report.domain.dto.enums.TipoPlantacion;
import com.kynsoft.report.domain.services.IPlantacionPermanenteService;
import com.kynsoft.report.infrastructure.services.ResumenPlantacionDto;
import com.kynsoft.share.core.domain.request.PageableUtil;
import com.kynsoft.share.core.domain.request.SearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para Plantaciones Permanentes (Grupos 12 y 13).
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP): Grupos 12 y 13
 * - Resolución 51/2021 MFP: Tasas de depreciación
 */
@RestController
@RequestMapping("/api/plantacion-permanente")
public class PlantacionPermanenteController {

    private final IPlantacionPermanenteService plantacionService;

    public PlantacionPermanenteController(IPlantacionPermanenteService plantacionService) {
        this.plantacionService = plantacionService;
    }

    @PostMapping("")
    public ResponseEntity<PlantacionPermanenteDto> create(@RequestBody PlantacionPermanenteDto dto) {
        PlantacionPermanenteDto created = plantacionService.create(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantacionPermanenteDto> findById(@PathVariable UUID id) {
        return plantacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantacionPermanenteDto> update(@PathVariable UUID id,
                                                           @RequestBody PlantacionPermanenteDto dto) {
        dto.setId(id);
        PlantacionPermanenteDto updated = plantacionService.update(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        plantacionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<Page<PlantacionPermanenteDto>> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        String query = request.getQuery();

        // Simple search without filter processing
        Page<PlantacionPermanenteDto> result = plantacionService.search(query, null, null, null, pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * Search with filters via query parameters.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<PlantacionPermanenteDto>> searchWithParams(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) TipoPlantacion tipoPlantacion,
            @RequestParam(required = false) Integer bloque,
            @RequestParam(required = false) UUID fincaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        Page<PlantacionPermanenteDto> result = plantacionService.search(query, tipoPlantacion, bloque, fincaId, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/bloque/{bloque}")
    public ResponseEntity<List<PlantacionPermanenteDto>> findByBloque(@PathVariable Integer bloque) {
        List<PlantacionPermanenteDto> plantaciones = plantacionService.findByBloque(bloque);
        return ResponseEntity.ok(plantaciones);
    }

    @GetMapping("/tipo/{tipoPlantacion}")
    public ResponseEntity<List<PlantacionPermanenteDto>> findByTipoPlantacion(
            @PathVariable TipoPlantacion tipoPlantacion) {
        List<PlantacionPermanenteDto> plantaciones = plantacionService.findByTipoPlantacion(tipoPlantacion);
        return ResponseEntity.ok(plantaciones);
    }

    @GetMapping("/finca/{fincaId}")
    public ResponseEntity<List<PlantacionPermanenteDto>> findByFinca(@PathVariable UUID fincaId) {
        List<PlantacionPermanenteDto> plantaciones = plantacionService.findByFinca(fincaId);
        return ResponseEntity.ok(plantaciones);
    }

    /**
     * Resumen de plantaciones por tipo para una finca.
     */
    @GetMapping("/finca/{fincaId}/resumen")
    public ResponseEntity<List<ResumenPlantacionDto>> getResumenPorTipo(
            @PathVariable UUID fincaId) {
        List<ResumenPlantacionDto> resumen = plantacionService.getResumenPorTipo(fincaId);
        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/tipos-plantacion")
    public ResponseEntity<TipoPlantacion[]> getTiposPlantacion() {
        return ResponseEntity.ok(TipoPlantacion.values());
    }

    @GetMapping("/tipos-cepa")
    public ResponseEntity<TipoCepa[]> getTiposCepa() {
        return ResponseEntity.ok(TipoCepa.values());
    }
}
