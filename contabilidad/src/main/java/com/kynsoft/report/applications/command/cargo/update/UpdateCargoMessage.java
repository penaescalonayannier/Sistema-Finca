package com.kynsoft.report.applications.command.cargo.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import java.util.UUID;

public class UpdateCargoMessage implements ICommandMessage {
    private final UUID id;

    public UpdateCargoMessage(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
