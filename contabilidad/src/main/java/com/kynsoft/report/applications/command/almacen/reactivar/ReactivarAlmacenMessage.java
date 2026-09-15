package com.kynsoft.report.applications.command.almacen.reactivar;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ReactivarAlmacenMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "REACTIVAR_ALMACEN";

    public ReactivarAlmacenMessage(UUID id) {
        this.id = id;
    }
}
