package com.kynsoft.report.applications.command.cliente.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteClienteMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_CLIENTE";

    public DeleteClienteMessage(UUID id) {
        this.id = id;
    }
}
