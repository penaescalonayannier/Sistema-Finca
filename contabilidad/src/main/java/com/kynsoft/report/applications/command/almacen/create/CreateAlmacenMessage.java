package com.kynsoft.report.applications.command.almacen.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateAlmacenMessage implements ICommandMessage {
    private final UUID id;
    private final String inventario;
    private final String command = "CREATE_ALMACEN";

    public CreateAlmacenMessage(UUID id, String inventario) {
        this.id = id;
        this.inventario = inventario;
    }
}
