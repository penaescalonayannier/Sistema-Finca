package com.kynsoft.report.applications.command.producto.importexcel;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ImportProductoExcelMessage implements ICommandMessage {
    private int totalImportados;
    private int totalErrores;
    private List<UUID> productosCreados;
    private List<String> errores;

    public ImportProductoExcelMessage() {
        this.totalImportados = 0;
        this.totalErrores = 0;
        this.productosCreados = new ArrayList<>();
        this.errores = new ArrayList<>();
    }

    public String getMessage() {
        return "Importación completada: " + totalImportados + " productos importados, " + totalErrores + " errores.";
    }
}
