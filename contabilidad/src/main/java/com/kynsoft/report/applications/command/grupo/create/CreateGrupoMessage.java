package com.kynsoft.report.applications.command.grupo.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import java.util.UUID;

public class CreateGrupoMessage implements ICommandMessage {
    private final UUID id;

    public CreateGrupoMessage(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
