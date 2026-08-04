package com.kynsoft.report.applications.command.unidadmedida.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateUnidadMedidaMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_UNIDAD_MEDIDA";

    public CreateUnidadMedidaMessage(UUID id) {
        this.id = id;
    }
}