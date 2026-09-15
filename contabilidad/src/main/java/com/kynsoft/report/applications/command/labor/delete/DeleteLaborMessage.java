package com.kynsoft.report.applications.command.labor.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteLaborMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_LABOR";

    public DeleteLaborMessage(UUID id) {
        this.id = id;
    }
}