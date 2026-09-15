package com.kynsoft.report.applications.command.bloque.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateBloqueMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_BLOQUE";

    public CreateBloqueMessage(UUID id) {
        this.id = id;
    }
}