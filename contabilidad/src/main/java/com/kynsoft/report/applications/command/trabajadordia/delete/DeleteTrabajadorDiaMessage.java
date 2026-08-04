package com.kynsoft.report.applications.command.trabajadordia.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteTrabajadorDiaMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_TRABAJADOR_DIA";

    public DeleteTrabajadorDiaMessage(UUID id) {
        this.id = id;
    }
}