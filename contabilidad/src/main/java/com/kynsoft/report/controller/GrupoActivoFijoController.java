package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.GrupoActivoFijoDto;
import com.kynsoft.report.domain.services.IGrupoActivoFijoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para Grupos de Activos Fijos Tangibles.
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP): Clasificación de activos fijos
 * - Resolución 51/2021 MFP: Tasas de depreciación por grupo
 *
 * Grupos según normativa cubana:
 * - 01: Edificios (3-5%)
 * - 02: Otras Construcciones (5-10%)
 * - 04: Máquinas y Equipos (10-20%)
 * - 05: Aparatos (12-20%)
 * - 07: Muebles y Enseres (10-15%)
 * - 08: Animales (20%)
 * - 12: Plantaciones Permanentes Caña (10-12.5%)
 * - 13: Plantaciones Permanentes Frutales (5-10%)
 */
@RestController
@RequestMapping("/api/grupo-activo-fijo")
public class GrupoActivoFijoController {

    private final IGrupoActivoFijoService grupoService;

    public GrupoActivoFijoController(IGrupoActivoFijoService grupoService) {
        this.grupoService = grupoService;
    }

    @PostMapping("")
    public ResponseEntity<GrupoActivoFijoDto> create(@RequestBody GrupoActivoFijoDto dto) {
        GrupoActivoFijoDto created = grupoService.create(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoActivoFijoDto> findById(@PathVariable UUID id) {
        return grupoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<GrupoActivoFijoDto> findByCodigo(@PathVariable String codigo) {
        return grupoService.findByCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrupoActivoFijoDto> update(@PathVariable UUID id,
                                                      @RequestBody GrupoActivoFijoDto dto) {
        dto.setId(id);
        GrupoActivoFijoDto updated = grupoService.update(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        grupoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("")
    public ResponseEntity<List<GrupoActivoFijoDto>> findAll() {
        List<GrupoActivoFijoDto> grupos = grupoService.findAll();
        return ResponseEntity.ok(grupos);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<GrupoActivoFijoDto>> findAllActivos() {
        List<GrupoActivoFijoDto> grupos = grupoService.findAllActivos();
        return ResponseEntity.ok(grupos);
    }
}
