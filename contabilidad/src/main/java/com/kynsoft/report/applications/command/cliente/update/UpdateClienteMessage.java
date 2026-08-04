package com.kynsoft.report.applications.command.cliente.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateClienteMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_CLIENTE";

    public UpdateClienteMessage(UUID id) {
        this.id = id;
    }
}
