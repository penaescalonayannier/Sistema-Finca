package com.kynsoft.report.applications.command.reporte.generatePdf;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class GenerateReportePdfCommand implements ICommand {
    private UUID reporteId;
    private byte[] pdfData;  // Campo para almacenar el PDF generado

    public GenerateReportePdfCommand(UUID reporteId) {
        this.reporteId = reporteId;
    }

    @Override
    public ICommandMessage getMessage() {
        return new GenerateReportePdfMessage(reporteId, pdfData);
    }
}