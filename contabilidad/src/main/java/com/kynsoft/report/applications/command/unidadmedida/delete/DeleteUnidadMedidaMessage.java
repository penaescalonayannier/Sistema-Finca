package com.kynsoft.report.applications.command.unidadmedida.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteUnidadMedidaMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_UNIDAD_MEDIDA";

    public DeleteUnidadMedidaMessage(UUID id) {
        this.id = id;
    }
}