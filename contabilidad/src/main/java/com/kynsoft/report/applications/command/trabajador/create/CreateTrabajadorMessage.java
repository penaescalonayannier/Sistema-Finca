package com.kynsoft.report.applications.command.trabajador.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateTrabajadorMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_TRABAJADOR";

    public CreateTrabajadorMessage(UUID id) {
        this.id = id;
    }
}
