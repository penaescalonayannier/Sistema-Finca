package com.kynsoft.report.applications.command.labor.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateLaborMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_LABOR";

    public UpdateLaborMessage(UUID id) {
        this.id = id;
    }
}