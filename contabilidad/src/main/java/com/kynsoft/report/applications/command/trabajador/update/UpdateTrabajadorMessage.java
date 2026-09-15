package com.kynsoft.report.applications.command.trabajador.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateTrabajadorMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_TRABAJADOR";

    public UpdateTrabajadorMessage(UUID id) {
        this.id = id;
    }
}