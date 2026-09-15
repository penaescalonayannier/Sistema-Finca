package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.ConfiguracionEmpresaDto;
import com.kynsoft.report.domain.services.IConfiguracionEmpresaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for company configuration.
 * Required for official Cuban document models (SC-2-08, SC-2-12).
 */
@RestController
@RequestMapping("/api/configuracion-empresa")
public class ConfiguracionEmpresaController {

    private final IConfiguracionEmpresaService service;

    public ConfiguracionEmpresaController(IConfiguracionEmpresaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ConfiguracionEmpresaDto> getActive() {
        return service.findActive()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConfiguracionEmpresaDto> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ConfiguracionEmpresaDto> create(@RequestBody ConfiguracionEmpresaDto dto) {
        ConfiguracionEmpresaDto created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfiguracionEmpresaDto> update(@PathVariable UUID id, @RequestBody ConfiguracionEmpresaDto dto) {
        dto.setId(id);
        ConfiguracionEmpresaDto updated = service.update(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
