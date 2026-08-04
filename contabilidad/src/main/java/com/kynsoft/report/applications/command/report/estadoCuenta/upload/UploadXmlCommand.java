package com.kynsoft.report.applications.command.report.estadoCuenta.upload;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.applications.command.report.estadoCuenta.create.CreateEstadoCuentaMessage; // Reutilizamos el mensaje de respuesta

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UploadXmlCommand implements ICommand {

    private final String xmlContent;
    private final UUID id = UUID.randomUUID();

    public UploadXmlCommand(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateEstadoCuentaMessage(id);
    }
}