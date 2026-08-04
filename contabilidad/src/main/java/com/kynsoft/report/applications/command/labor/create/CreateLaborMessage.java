package com.kynsoft.report.applications.command.labor.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateLaborMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_LABOR";

    public CreateLaborMessage(UUID id) {
        this.id = id;
    }
}