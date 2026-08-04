package com.kynsoft.report.applications.command.reporte.generateConsolidadoPorResponsablePdf;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenerateConsolidadoPorResponsablePdfCommand implements ICommand {

    private String year;
    private String mes;
    private byte[] pdfData;

    public GenerateConsolidadoPorResponsablePdfCommand(String year, String mes) {
        this.year = year;
        this.mes = mes;
        this.pdfData = null;
    }

    @Override
    public ICommandMessage getMessage() {
        return new GenerateConsolidadoPorResponsablePdfMessage(year, mes, pdfData);
    }
}
