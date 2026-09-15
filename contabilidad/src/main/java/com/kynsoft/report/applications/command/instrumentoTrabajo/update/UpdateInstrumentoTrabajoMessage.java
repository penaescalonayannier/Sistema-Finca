package com.kynsoft.report.applications.command.instrumentoTrabajo.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateInstrumentoTrabajoMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_INSTRUMENTO_TRABAJO";

    public UpdateInstrumentoTrabajoMessage(UUID id) {
        this.id = id;
    }
}