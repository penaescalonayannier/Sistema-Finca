package com.kynsoft.report.applications.command.variedad.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateVariedadMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_VARIEDAD";

    public UpdateVariedadMessage(UUID id) {
        this.id = id;
    }
}