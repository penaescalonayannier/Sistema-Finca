package com.kynsoft.report.applications.command.reporte.generateConsolidadoPorResponsablePdf;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenerateConsolidadoPorResponsablePdfMessage implements ICommandMessage {
    private String year;
    private String mes;
    private byte[] pdfData;
}
