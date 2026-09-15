package com.kynsoft.report.applications.command.producto.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateProductoMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_PRODUCTO";

    public CreateProductoMessage(UUID id) {
        this.id = id;
    }
}