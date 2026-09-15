package com.kynsoft.report.applications.command.cepa.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateCepaMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_CEPA";

    public UpdateCepaMessage(UUID id) {
        this.id = id;
    }
}