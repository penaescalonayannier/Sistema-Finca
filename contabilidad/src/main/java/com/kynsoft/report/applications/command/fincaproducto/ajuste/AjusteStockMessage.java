package com.kynsoft.report.applications.command.fincaproducto.ajuste;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class AjusteStockMessage implements ICommandMessage {
    private final Double stockAnterior;
    private final Double stockNuevo;
    private final Double cantidad;
    private final String command = "AJUSTE_STOCK";

    public AjusteStockMessage(Double stockAnterior, Double stockNuevo, Double cantidad) {
        this.stockAnterior = stockAnterior;
        this.stockNuevo = stockNuevo;
        this.cantidad = cantidad;
    }
}
