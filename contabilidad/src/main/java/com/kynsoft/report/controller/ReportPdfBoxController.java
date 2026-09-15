package com.kynsoft.report.controller;

import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.report.recetaMedica.ReportRecetaMedicaCMessage;
import com.kynsoft.report.applications.command.report.recetaMedica.ReportRecetaMedicaCommand;
import com.kynsoft.report.applications.command.report.recetaMedica.ReportRecetaMedicaRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/report")
public class ReportPdfBoxController {

    private final IMediator mediator;

    public ReportPdfBoxController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping(value = "/receta-medica", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateReportRecetaMedica(@RequestBody ReportRecetaMedicaRequest request) throws IOException {

        ReportRecetaMedicaCommand command = ReportRecetaMedicaCommand.fromRequest(request);
        ReportRecetaMedicaCMessage response = mediator.send(command);

        byte[] pdfBytes = response.getBaos().toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=seccion_a_exacta.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdfBytes.length)
                .body(pdfBytes);
    }

}
