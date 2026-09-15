package com.kynsoft.report.applications.command.report.trabajador.upload;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class ImportTrabajadorCsvCommand implements ICommand {

    private final byte[] fileContent;
    private final String command = "IMPORT_CLIENTE_CSV";
    
    // Campo donde el Handler guardará los resultados
    private List<TrabajadorImportadoDto> resultados = new ArrayList<>(); 

    public ImportTrabajadorCsvCommand(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    // Setter para que el Handler actualice los resultados
    public void setResultados(List<TrabajadorImportadoDto> resultados) {
        this.resultados = resultados;
    }

    @Override
    public ICommandMessage getMessage() {
        // Usamos la lista de resultados para construir el mensaje
        return new ImportClienteCsvMessage(
            "Importación de Clientes completada. Total importados: " + this.resultados.size(),
            this.resultados
        );
    }
}