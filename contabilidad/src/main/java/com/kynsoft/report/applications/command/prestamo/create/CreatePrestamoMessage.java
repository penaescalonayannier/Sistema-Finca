package com.kynsoft.report.applications.command.prestamo.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreatePrestamoMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_PRESTAMO";

    public CreatePrestamoMessage(UUID id) {
        this.id = id;
    }
}
