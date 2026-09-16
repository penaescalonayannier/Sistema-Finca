package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.ArqueoCajaDetalleDto;
import com.kynsoft.report.domain.dto.ArqueoCajaResumenDto;
import com.kynsoft.report.domain.dto.CerrarArqueoCajaRequest;
import com.kynsoft.report.domain.dto.CrearArqueoCajaRequest;
import com.kynsoft.report.domain.services.IArqueoCajaService;
import com.kynsoft.report.infrastructure.services.ArqueoCajaPdfService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/arqueos-caja")
public class ArqueoCajaController {
    private final IArqueoCajaService service;
    private final ArqueoCajaPdfService pdfService;

    public ArqueoCajaController(IArqueoCajaService service, ArqueoCajaPdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> crear(@RequestBody CrearArqueoCajaRequest request) {
        return ResponseEntity.ok(Map.of("id", service.crear(request)));
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<Void> cerrar(@PathVariable UUID id, @RequestBody CerrarArqueoCajaRequest request) {
        service.cerrar(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ArqueoCajaResumenDto>> listar(@RequestParam(required = false) UUID fincaId) {
        return ResponseEntity.ok(service.listar(fincaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArqueoCajaDetalleDto> detalle(@PathVariable UUID id) {
        return ResponseEntity.ok(service.detalle(id));
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(@PathVariable UUID id) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("arqueo-caja-" + id + ".pdf", StandardCharsets.UTF_8).build().toString())
                .body(pdfService.generar(id));
    }
}
