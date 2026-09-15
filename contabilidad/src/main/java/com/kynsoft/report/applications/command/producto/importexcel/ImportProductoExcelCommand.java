package com.kynsoft.report.applications.command.producto.importexcel;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Setter
@AllArgsConstructor
public class ImportProductoExcelCommand implements ICommand {
    private InputStream excelInputStream;
    private String fileName;

    @Override
    public ICommandMessage getMessage() {
        return new ImportProductoExcelMessage();
    }
}
