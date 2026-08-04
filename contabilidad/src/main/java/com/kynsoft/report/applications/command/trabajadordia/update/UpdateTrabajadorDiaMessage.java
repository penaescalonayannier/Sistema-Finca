package com.kynsoft.report.applications.command.trabajadordia.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateTrabajadorDiaMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_TRABAJADOR_DIA";

    public UpdateTrabajadorDiaMessage(UUID id) {
        this.id = id;
    }
}