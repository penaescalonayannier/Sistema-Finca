package com.kynsoft.report.applications.command.prestamo.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdatePrestamoMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_PRESTAMO";

    public UpdatePrestamoMessage(UUID id) {
        this.id = id;
    }
}
