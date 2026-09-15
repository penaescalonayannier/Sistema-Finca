package com.kynsoft.report.applications.command.campos.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateCamposMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_CAMPOS";

    public UpdateCamposMessage(UUID id) {
        this.id = id;
    }
}