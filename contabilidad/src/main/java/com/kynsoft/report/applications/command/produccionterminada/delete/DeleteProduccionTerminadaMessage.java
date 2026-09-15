package com.kynsoft.report.applications.command.produccionterminada.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteProduccionTerminadaMessage implements ICommandMessage {
    private final UUID id;
    private final Integer stockAnterior;
    private final Integer stockNuevo;
    private final Integer cantidadRevertida;
    private final String command = "DELETE_PRODUCCION_TERMINADA";

    public DeleteProduccionTerminadaMessage(UUID id, Integer stockAnterior, Integer stockNuevo, Integer cantidadRevertida) {
        this.id = id;
        this.stockAnterior = stockAnterior;
        this.stockNuevo = stockNuevo;
        this.cantidadRevertida = cantidadRevertida;
    }
}
