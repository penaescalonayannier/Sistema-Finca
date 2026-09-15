package com.kynsoft.report.applications.command.grupo.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;

import java.util.UUID;

public class UpdateGrupoMessage implements ICommandMessage {
    private final UUID id;

    public UpdateGrupoMessage(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
