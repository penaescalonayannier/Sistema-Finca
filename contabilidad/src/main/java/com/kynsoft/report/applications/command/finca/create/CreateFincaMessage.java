package com.kynsoft.report.applications.command.finca.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateFincaMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_FINCA";

    public CreateFincaMessage(UUID id) {
        this.id = id;
    }
}