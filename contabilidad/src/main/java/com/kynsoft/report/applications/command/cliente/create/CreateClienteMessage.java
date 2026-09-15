package com.kynsoft.report.applications.command.cliente.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateClienteMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_CLIENTE";

    public CreateClienteMessage(UUID id) {
        this.id = id;
    }
}
