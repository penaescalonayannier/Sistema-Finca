package com.kynsoft.report.controller;
import com.kynsoft.report.domain.dto.*; import com.kynsoft.report.domain.services.IConteoFisicoAlmacenService; import com.kynsoft.report.infrastructure.services.ConteoFisicoAlmacenPdfService; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.nio.charset.StandardCharsets; import java.util.*;
@RestController @RequestMapping("/api/conteos-fisicos-almacen") public class ConteoFisicoAlmacenController {
 private final IConteoFisicoAlmacenService service; private final ConteoFisicoAlmacenPdfService pdf; public ConteoFisicoAlmacenController(IConteoFisicoAlmacenService service,ConteoFisicoAlmacenPdfService pdf){this.service=service;this.pdf=pdf;}
 @PostMapping public ResponseEntity<Map<String,UUID>> abrir(@RequestBody CrearConteoFisicoRequest r){return ResponseEntity.ok(Map.of("id",service.abrir(r)));}
 @PutMapping("/{id}/cerrar") public ResponseEntity<Void> cerrar(@PathVariable UUID id,@RequestBody CerrarConteoFisicoRequest r){service.cerrar(id,r);return ResponseEntity.noContent().build();}
 @GetMapping public ResponseEntity<List<ConteoFisicoDetalleDto>> listar(@RequestParam UUID fincaId){return ResponseEntity.ok(service.listar(fincaId));}
 @GetMapping("/{id}") public ResponseEntity<ConteoFisicoDetalleDto> detalle(@PathVariable UUID id){return ResponseEntity.ok(service.detalle(id));}
 @GetMapping(value="/{id}/pdf",produces=MediaType.APPLICATION_PDF_VALUE) public ResponseEntity<byte[]> pdf(@PathVariable UUID id){return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,ContentDisposition.attachment().filename("SC-2-15-inventario-"+id+".pdf",StandardCharsets.UTF_8).build().toString()).body(pdf.generar(id));}
}
