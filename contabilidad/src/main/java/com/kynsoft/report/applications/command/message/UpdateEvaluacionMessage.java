package com.kynsoft.report.applications.command.message;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;

public class UpdateEvaluacionMessage implements ICommandMessage {
    private final UUID id;

    public UpdateEvaluacionMessage(UUID id, UUID trabajadorId, UUID jefeId, String mes, Integer year,
                                  Integer calificacion, String comentarios, Object o) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
