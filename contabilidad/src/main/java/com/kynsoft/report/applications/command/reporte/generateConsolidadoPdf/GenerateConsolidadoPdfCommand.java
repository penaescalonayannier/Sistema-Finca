package com.kynsoft.report.applications.command.reporte.generateConsolidadoPdf;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenerateConsolidadoPdfCommand implements ICommand {
    private String year;
    private String mes;
    private byte[] pdfData;

    public GenerateConsolidadoPdfCommand(String year, String mes) {
        this.year = year;
        this.mes = mes;
    }

    @Override
    public ICommandMessage getMessage() {
        return new GenerateConsolidadoPdfMessage(year, mes, pdfData);
    }
}