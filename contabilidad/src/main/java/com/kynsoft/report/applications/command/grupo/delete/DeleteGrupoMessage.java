package com.kynsoft.report.applications.command.grupo.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import java.util.UUID;

public class DeleteGrupoMessage implements ICommandMessage {
    private final UUID id;

    public DeleteGrupoMessage(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
