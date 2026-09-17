package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.HistorialSalarioDto;
import com.kynsoft.report.domain.services.IHistorialSalarioService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial-salarial")
public class HistorialSalarioController {
    private final IHistorialSalarioService service;
    public HistorialSalarioController(IHistorialSalarioService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> registrar(@RequestBody HistorialSalarioDto solicitud) {
        return ResponseEntity.ok(Map.of("id", service.registrar(solicitud)));
    }
    @GetMapping("/trabajador/{trabajadorId}")
    public ResponseEntity<List<HistorialSalarioDto>> listar(@PathVariable UUID trabajadorId) {
        return ResponseEntity.ok(service.listarPorTrabajador(trabajadorId));
    }
    @GetMapping("/trabajador/{trabajadorId}/vigente")
    public ResponseEntity<HistorialSalarioDto> vigente(@PathVariable UUID trabajadorId) {
        HistorialSalarioDto vigente = service.vigente(trabajadorId);
        return vigente == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(vigente);
    }
    @PostMapping("/{id}/anular")
    public ResponseEntity<Void> anular(@PathVariable UUID id, @RequestBody Map<String, String> solicitud) {
        service.anular(id, solicitud == null ? null : solicitud.get("motivo"));
        return ResponseEntity.noContent().build();
    }
}
