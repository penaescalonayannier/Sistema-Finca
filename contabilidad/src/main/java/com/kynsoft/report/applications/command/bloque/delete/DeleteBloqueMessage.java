package com.kynsoft.report.applications.command.bloque.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteBloqueMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_BLOQUE";

    public DeleteBloqueMessage(UUID id) {
        this.id = id;
    }
}