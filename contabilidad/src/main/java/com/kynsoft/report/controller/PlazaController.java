package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.PlazaDto;
import com.kynsoft.report.domain.services.IPlazaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/plazas")
public class PlazaController {
    private final IPlazaService service;
    public PlazaController(IPlazaService service) { this.service = service; }
    @PostMapping public ResponseEntity<PlazaDto> create(@RequestBody PlazaDto dto){return ResponseEntity.ok(service.create(dto));}
    @PutMapping("/{id}") public ResponseEntity<PlazaDto> update(@PathVariable UUID id,@RequestBody PlazaDto dto){return ResponseEntity.ok(service.update(id,dto));}
    @GetMapping("/{id}") public ResponseEntity<PlazaDto> get(@PathVariable UUID id){return ResponseEntity.ok(service.findById(id));}
    @GetMapping public ResponseEntity<List<PlazaDto>> list(@RequestParam UUID fincaId){return ResponseEntity.ok(service.findByFinca(fincaId));}
    @DeleteMapping("/{id}") public ResponseEntity<Void> deactivate(@PathVariable UUID id){service.desactivar(id);return ResponseEntity.noContent().build();}
    @PostMapping("/{id}/asignar-trabajador/{trabajadorId}") public ResponseEntity<PlazaDto> asignar(@PathVariable UUID id,@PathVariable UUID trabajadorId){return ResponseEntity.ok(service.asignarTrabajador(id,trabajadorId));}
    @PostMapping("/{id}/desasignar-trabajador") public ResponseEntity<PlazaDto> desasignar(@PathVariable UUID id){return ResponseEntity.ok(service.desasignarTrabajador(id));}
}
