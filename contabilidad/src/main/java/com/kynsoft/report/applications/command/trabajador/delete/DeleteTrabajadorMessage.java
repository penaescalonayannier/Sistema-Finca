package com.kynsoft.report.applications.command.trabajador.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteTrabajadorMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_TRABAJADOR";

    public DeleteTrabajadorMessage(UUID id) {
        this.id = id;
    }
}