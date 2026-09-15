package com.kynsoft.report.applications.command.variedad.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteVariedadMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_VARIEDAD";

    public DeleteVariedadMessage(UUID id) {
        this.id = id;
    }
}