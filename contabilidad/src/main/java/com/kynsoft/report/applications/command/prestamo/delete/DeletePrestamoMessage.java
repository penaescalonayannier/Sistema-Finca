package com.kynsoft.report.applications.command.prestamo.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeletePrestamoMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_PRESTAMO";

    public DeletePrestamoMessage(UUID id) {
        this.id = id;
    }
}
