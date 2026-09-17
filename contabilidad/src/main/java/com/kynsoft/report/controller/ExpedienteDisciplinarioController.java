package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.ActualizarExpedienteDisciplinarioRequest;
import com.kynsoft.report.domain.dto.AnularExpedienteDisciplinarioRequest;
import com.kynsoft.report.domain.dto.CrearExpedienteDisciplinarioRequest;
import com.kynsoft.report.domain.dto.ExpedienteDisciplinarioDto;
import com.kynsoft.report.domain.dto.ResolverExpedienteDisciplinarioRequest;
import com.kynsoft.report.domain.services.IExpedienteDisciplinarioService;
import com.kynsoft.report.infrastructure.services.ExpedienteDisciplinarioPdfService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expedientes-disciplinarios")
public class ExpedienteDisciplinarioController {
    private final IExpedienteDisciplinarioService service; private final ExpedienteDisciplinarioPdfService pdfService;
    public ExpedienteDisciplinarioController(IExpedienteDisciplinarioService service, ExpedienteDisciplinarioPdfService pdfService) { this.service = service; this.pdfService = pdfService; }
    @PostMapping public ResponseEntity<Map<String, UUID>> crear(@RequestBody CrearExpedienteDisciplinarioRequest request) { return ResponseEntity.ok(Map.of("id", service.crear(request))); }
    @PutMapping("/{id}") public ResponseEntity<Void> actualizar(@PathVariable UUID id, @RequestBody ActualizarExpedienteDisciplinarioRequest request) { service.actualizar(id, request); return ResponseEntity.noContent().build(); }
    @PostMapping("/{id}/notificar") public ResponseEntity<Void> notificar(@PathVariable UUID id) { service.notificar(id); return ResponseEntity.noContent().build(); }
    @PostMapping("/{id}/resolver") public ResponseEntity<Void> resolver(@PathVariable UUID id, @RequestBody ResolverExpedienteDisciplinarioRequest request) { service.resolver(id, request); return ResponseEntity.noContent().build(); }
    @PostMapping("/{id}/anular") public ResponseEntity<Void> anular(@PathVariable UUID id, @RequestBody AnularExpedienteDisciplinarioRequest request) { service.anular(id, request); return ResponseEntity.noContent().build(); }
    @GetMapping("/{id}") public ResponseEntity<ExpedienteDisciplinarioDto> detalle(@PathVariable UUID id) { return ResponseEntity.ok(service.detalle(id)); }
    @GetMapping public ResponseEntity<List<ExpedienteDisciplinarioDto>> listar(@RequestParam UUID fincaId, @RequestParam(required = false) UUID trabajadorId, @RequestParam(required = false) LocalDate desde, @RequestParam(required = false) LocalDate hasta, @RequestParam(defaultValue = "false") boolean incluirAnulados) { return ResponseEntity.ok(service.listar(fincaId, trabajadorId, desde, hasta, incluirAnulados)); }
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE) public ResponseEntity<byte[]> pdf(@PathVariable UUID id) { return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("expediente-disciplinario-" + id + ".pdf", StandardCharsets.UTF_8).build().toString()).body(pdfService.generar(id)); }
}
