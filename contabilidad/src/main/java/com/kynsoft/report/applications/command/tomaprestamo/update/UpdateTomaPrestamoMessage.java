package com.kynsoft.report.applications.command.tomaprestamo.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateTomaPrestamoMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_TOMA_PRESTAMO";

    public UpdateTomaPrestamoMessage(UUID id) {
        this.id = id;
    }
}