package com.kynsoft.report.applications.command.finca.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteFincaMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_FINCA";

    public DeleteFincaMessage(UUID id) {
        this.id = id;
    }
}