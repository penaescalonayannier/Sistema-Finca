package com.kynsoft.report.applications.command.trabajador.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class DeleteTrabajadorMessage implements ICommandMessage {

    private final UUID id;
    private final List<String> advertencias;
    private final String command = "DELETE_TRABAJADOR";

    public DeleteTrabajadorMessage(UUID id, List<String> advertencias) {
        this.id = id;
        this.advertencias = advertencias;
    }
}