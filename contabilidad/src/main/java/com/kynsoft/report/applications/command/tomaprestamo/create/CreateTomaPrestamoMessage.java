package com.kynsoft.report.applications.command.tomaprestamo.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateTomaPrestamoMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_TOMA_PRESTAMO";

    public CreateTomaPrestamoMessage(UUID id) {
        this.id = id;
    }
}