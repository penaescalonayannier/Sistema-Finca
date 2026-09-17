package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.AreaTrabajoDto;
import com.kynsoft.report.domain.services.IAreaTrabajoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/areas-trabajo")
public class AreaTrabajoController {
    private final IAreaTrabajoService service;
    public AreaTrabajoController(IAreaTrabajoService service) { this.service = service; }
    @PostMapping public ResponseEntity<AreaTrabajoDto> create(@RequestBody AreaTrabajoDto dto) { return ResponseEntity.ok(service.create(dto)); }
    @PutMapping("/{id}") public ResponseEntity<AreaTrabajoDto> update(@PathVariable UUID id,@RequestBody AreaTrabajoDto dto){return ResponseEntity.ok(service.update(id,dto));}
    @GetMapping("/{id}") public ResponseEntity<AreaTrabajoDto> get(@PathVariable UUID id){return ResponseEntity.ok(service.findById(id));}
    @GetMapping public ResponseEntity<List<AreaTrabajoDto>> list(@RequestParam UUID fincaId){return ResponseEntity.ok(service.findByFinca(fincaId));}
    @DeleteMapping("/{id}") public ResponseEntity<Void> deactivate(@PathVariable UUID id){service.desactivar(id);return ResponseEntity.noContent().build();}
}
