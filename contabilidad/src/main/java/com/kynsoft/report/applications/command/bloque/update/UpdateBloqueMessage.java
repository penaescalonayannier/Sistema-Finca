package com.kynsoft.report.applications.command.bloque.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateBloqueMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_BLOQUE";

    public UpdateBloqueMessage(UUID id) {
        this.id = id;
    }
}