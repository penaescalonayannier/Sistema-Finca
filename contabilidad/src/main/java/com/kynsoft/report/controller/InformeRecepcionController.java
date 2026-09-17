package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.InformeRecepcionDto;
import com.kynsoft.report.infrastructure.services.InformeRecepcionPdfService;
import com.kynsoft.report.infrastructure.services.InformeRecepcionService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

/** Solo consulta: el SC-2-04 registrado no puede editarse ni eliminarse. */
@RestController @RequestMapping("/api/informes-recepcion")
public class InformeRecepcionController {
    private final InformeRecepcionService service; private final InformeRecepcionPdfService pdf;
    public InformeRecepcionController(InformeRecepcionService service, InformeRecepcionPdfService pdf){this.service=service;this.pdf=pdf;}
    @GetMapping("/{id}") public ResponseEntity<InformeRecepcionDto> obtener(@PathVariable UUID id){return ResponseEntity.ok(service.obtener(id));}
    @GetMapping(value="/{id}/pdf",produces=MediaType.APPLICATION_PDF_VALUE) public ResponseEntity<byte[]> descargar(@PathVariable UUID id) throws Exception {InformeRecepcionDto i=service.obtener(id);byte[] data=pdf.generar(id);return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=SC-2-04_"+i.getNumeroDocumento()+".pdf").contentLength(data.length).body(data);}
}
