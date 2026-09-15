package com.kynsoft.report.applications.command.cargo.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;

import java.util.UUID;

public class CreateCargoMessage implements ICommandMessage {
    private final UUID id;

    public CreateCargoMessage(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
