package com.kynsoft.report.applications.command.report.trabajador.upload;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.List;

@Getter
public class ImportClienteCsvMessage implements ICommandMessage {

    private final String message;
    private final String command = "IMPORT_CLIENTE_CSV";
    private final List<TrabajadorImportadoDto> trabajadoresImportados;

    /**
     * Constructor utilizado por el Command para generar el mensaje de respuesta.
     * * @param message Mensaje de estado de la importación (ej: "Importación completada").
     * @param trabajadoresImportados Lista de los registros procesados.
     */
    public ImportClienteCsvMessage(String message, List<TrabajadorImportadoDto> trabajadoresImportados) {
        this.message = message;
        this.trabajadoresImportados = trabajadoresImportados;
    }
}
