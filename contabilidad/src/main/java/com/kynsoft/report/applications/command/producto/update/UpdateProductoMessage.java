package com.kynsoft.report.applications.command.producto.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateProductoMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_PRODUCTO";

    public UpdateProductoMessage(UUID id) {
        this.id = id;
    }
}