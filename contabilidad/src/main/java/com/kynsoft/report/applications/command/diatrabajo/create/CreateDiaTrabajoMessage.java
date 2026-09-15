package com.kynsoft.report.applications.command.diatrabajo.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateDiaTrabajoMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_DIA_TRABAJO";

    public CreateDiaTrabajoMessage(UUID id) {
        this.id = id;
    }
}