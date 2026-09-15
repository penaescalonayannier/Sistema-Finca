package com.kynsoft.report.applications.command.finca.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateFincaMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_FINCA";

    public UpdateFincaMessage(UUID id) {
        this.id = id;
    }
}