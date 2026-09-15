package com.kynsoft.report.applications.command.cepa.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateCepaMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_CEPA";

    public CreateCepaMessage(UUID id) {
        this.id = id;
    }
}