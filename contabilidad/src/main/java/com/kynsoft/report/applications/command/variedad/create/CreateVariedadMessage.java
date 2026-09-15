package com.kynsoft.report.applications.command.variedad.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateVariedadMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_VARIEDAD";

    public CreateVariedadMessage(UUID id) {
        this.id = id;
    }
}