package com.kynsoft.report.applications.command.message;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;

public class DeleteEvaluacionMessage implements ICommandMessage {
    private final UUID id;

    public DeleteEvaluacionMessage(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
