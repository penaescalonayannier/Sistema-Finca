package com.kynsoft.report.applications.command.campos.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateCamposMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_CAMPOS";

    public CreateCamposMessage(UUID id) {
        this.id = id;
    }
}