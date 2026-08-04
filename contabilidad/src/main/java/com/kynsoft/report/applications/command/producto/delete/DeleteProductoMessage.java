package com.kynsoft.report.applications.command.producto.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteProductoMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_PRODUCTO";

    public DeleteProductoMessage(UUID id) {
        this.id = id;
    }
}