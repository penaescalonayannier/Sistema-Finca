package com.kynsoft.report.applications.command.almacen.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteAlmacenMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_ALMACEN";

    public DeleteAlmacenMessage(UUID id) {
        this.id = id;
    }
}
