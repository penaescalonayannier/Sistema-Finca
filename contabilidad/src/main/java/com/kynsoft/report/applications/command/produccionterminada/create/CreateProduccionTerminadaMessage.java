package com.kynsoft.report.applications.command.produccionterminada.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateProduccionTerminadaMessage implements ICommandMessage {
    private final UUID id;
    private final Integer stockAnterior;
    private final Integer stockNuevo;
    private final String command = "CREATE_PRODUCCION_TERMINADA";

    public CreateProduccionTerminadaMessage(UUID id, Integer stockAnterior, Integer stockNuevo) {
        this.id = id;
        this.stockAnterior = stockAnterior;
        this.stockNuevo = stockNuevo;
    }
}
