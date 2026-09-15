package com.kynsoft.report.applications.command.report.estadoCuenta.createBatch;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class CreateEstadoCuentaBatchMessage implements ICommandMessage {

    private final List<UUID> createdIds;
    private final int totalCreated;
    private final String command = "CREATE_ESTADO_CUENTA_BATCH";

    public CreateEstadoCuentaBatchMessage(List<UUID> createdIds) {
        this.createdIds = createdIds;
        this.totalCreated = createdIds.size();
    }
}
