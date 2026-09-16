package com.kynsoft.report.controller;
import com.kynsoft.report.domain.dto.*; import com.kynsoft.report.infrastructure.services.DocumentoCajaControlService; import com.kynsoft.report.infrastructure.services.DocumentoCajaPdfService; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/documentos-caja") public class DocumentoCajaController {
 private final DocumentoCajaControlService service; private final DocumentoCajaPdfService pdf; public DocumentoCajaController(DocumentoCajaControlService service,DocumentoCajaPdfService pdf){this.service=service;this.pdf=pdf;}
 @PostMapping public ResponseEntity<Map<String,UUID>> crear(@RequestBody DocumentoCajaRequest request){return ResponseEntity.ok(Map.of("id",service.crear(request)));}
 @GetMapping public List<DocumentoCajaResponse> listar(@RequestParam UUID fincaId){return service.listar(fincaId);}
 @GetMapping("/{id}") public DocumentoCajaResponse obtener(@PathVariable UUID id){return service.obtener(id);}
 @GetMapping(value="/{id}/pdf",produces=MediaType.APPLICATION_PDF_VALUE) public ResponseEntity<byte[]> pdf(@PathVariable UUID id){return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=documento-caja-"+id+".pdf").body(pdf.generar(id));}
 @PostMapping("/{id}/anular") public ResponseEntity<Void> anular(@PathVariable UUID id,@RequestBody(required=false) Map<String,String> body){service.anular(id,body==null?null:body.get("observaciones"));return ResponseEntity.noContent().build();}
}
