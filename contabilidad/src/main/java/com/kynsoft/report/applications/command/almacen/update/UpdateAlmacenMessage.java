package com.kynsoft.report.applications.command.almacen.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateAlmacenMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_ALMACEN";

    public UpdateAlmacenMessage(UUID id) {
        this.id = id;
    }
}
