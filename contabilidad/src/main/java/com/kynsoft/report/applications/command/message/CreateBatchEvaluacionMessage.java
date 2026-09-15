package com.kynsoft.report.applications.command.message;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;

public class CreateBatchEvaluacionMessage implements ICommandMessage {
    private final String message;

    public CreateBatchEvaluacionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
