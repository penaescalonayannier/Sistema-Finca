package com.kynsoft.report.applications.command.unidadmedida.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateUnidadMedidaMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_UNIDAD_MEDIDA";

    public UpdateUnidadMedidaMessage(UUID id) {
        this.id = id;
    }
}