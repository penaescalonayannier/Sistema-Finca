package com.kynsoft.report.applications.command.cargo.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import java.util.UUID;

public class DeleteCargoMessage implements ICommandMessage {
    private final UUID id;

    public DeleteCargoMessage(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
