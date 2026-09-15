package com.kynsoft.report.applications.command.cepa.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteCepaMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_CEPA";

    public DeleteCepaMessage(UUID id) {
        this.id = id;
    }
}