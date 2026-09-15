package com.kynsoft.report.applications.command.reporte.generateConsolidadoPdf;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class GenerateConsolidadoPdfMessage implements ICommandMessage {
    private final String year;
    private final String mes;
    private final String command = "GENERATE_CONSOLIDADO_PDF";
    private byte[] pdfData;

    public GenerateConsolidadoPdfMessage(String year, String mes) {
        this.year = year;
        this.mes = mes;
    }

    public GenerateConsolidadoPdfMessage(String year, String mes, byte[] pdfData) {
        this.year = year;
        this.mes = mes;
        this.pdfData = pdfData;
    }
}