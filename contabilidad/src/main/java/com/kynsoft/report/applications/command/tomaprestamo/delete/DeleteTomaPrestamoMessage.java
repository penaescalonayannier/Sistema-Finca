package com.kynsoft.report.applications.command.tomaprestamo.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteTomaPrestamoMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_TOMA_PRESTAMO";

    public DeleteTomaPrestamoMessage(UUID id) {
        this.id = id;
    }
}