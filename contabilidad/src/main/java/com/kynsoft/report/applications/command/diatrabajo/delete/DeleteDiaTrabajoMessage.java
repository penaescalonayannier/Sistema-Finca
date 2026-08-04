package com.kynsoft.report.applications.command.diatrabajo.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteDiaTrabajoMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_DIA_TRABAJO";

    public DeleteDiaTrabajoMessage(UUID id) {
        this.id = id;
    }
}