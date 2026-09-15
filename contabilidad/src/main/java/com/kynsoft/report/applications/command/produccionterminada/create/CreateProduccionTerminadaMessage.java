package com.kynsoft.report.applications.command.produccionterminada.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateProduccionTerminadaMessage implements ICommandMessage {
    private final UUID id;
    private final Double stockAnterior;
    private final Double stockNuevo;
    private final String command = "CREATE_PRODUCCION_TERMINADA";

    public CreateProduccionTerminadaMessage(UUID id, Double stockAnterior, Double stockNuevo) {
        this.id = id;
        this.stockAnterior = stockAnterior;
        this.stockNuevo = stockNuevo;
    }
}
