package com.kynsoft.report.applications.command.instrumentoTrabajo.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteInstrumentoTrabajoMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_INSTRUMENTO_TRABAJO";

    public DeleteInstrumentoTrabajoMessage(UUID id) {
        this.id = id;
    }
}