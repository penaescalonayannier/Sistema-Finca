package com.kynsoft.report.applications.command.fincaproducto.entradaconduce;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class EntradaConduceMessage implements ICommandMessage {
    private final Double stockAnterior;
    private final Double stockNuevo;
    private final Integer cantidad;
    private final String command = "ENTRADA_CONDUCE";

    public EntradaConduceMessage(Double stockAnterior, Double stockNuevo, Integer cantidad) {
        this.stockAnterior = stockAnterior;
        this.stockNuevo = stockNuevo;
        this.cantidad = cantidad;
    }
}
