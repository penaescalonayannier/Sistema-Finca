package com.kynsoft.report.applications.command.instrumentoTrabajo.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateInstrumentoTrabajoMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_INSTRUMENTO_TRABAJO";

    public CreateInstrumentoTrabajoMessage(UUID id) {
        this.id = id;
    }
}