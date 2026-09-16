package com.kynsoft.report.applications.command.produccionterminada.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateProduccionTerminadaMessage implements ICommandMessage {
    private final UUID id;
    private final Double stockAnterior;
    private final Double stockNuevo;
    private final Double ajuste;
    private final String command = "UPDATE_PRODUCCION_TERMINADA";

    public UpdateProduccionTerminadaMessage(UUID id, Double stockAnterior, Double stockNuevo, Double ajuste) {
        this.id = id;
        this.stockAnterior = stockAnterior;
        this.stockNuevo = stockNuevo;
        this.ajuste = ajuste;
    }
}
