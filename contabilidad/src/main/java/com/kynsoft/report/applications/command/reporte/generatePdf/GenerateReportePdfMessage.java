package com.kynsoft.report.applications.command.reporte.generatePdf;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class GenerateReportePdfMessage implements ICommandMessage {
    private final UUID reporteId;
    private final String command = "GENERATE_REPORTE_PDF";
    private byte[] pdfData;  // Campo para almacenar el PDF

    public GenerateReportePdfMessage(UUID reporteId) {
        this.reporteId = reporteId;
    }

    public GenerateReportePdfMessage(UUID reporteId, byte[] pdfData) {
        this.reporteId = reporteId;
        this.pdfData = pdfData;
    }
}