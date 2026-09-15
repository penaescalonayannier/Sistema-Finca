package com.kynsoft.report.applications.command.trabajadordia.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateTrabajadorDiaMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_TRABAJADOR_DIA";

    public CreateTrabajadorDiaMessage(UUID id) {
        this.id = id;
    }
}