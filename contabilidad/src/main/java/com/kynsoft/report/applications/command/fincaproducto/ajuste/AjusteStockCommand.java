package com.kynsoft.report.applications.command.fincaproducto.ajuste;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AjusteStockCommand implements ICommand {
    private UUID id;
    private Double cantidad;
    private String observaciones;
    private Double stockAnterior;
    private Double stockNuevo;

    public AjusteStockCommand(UUID id, Double cantidad, String observaciones) {
        this.id = id;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
    }

    public static AjusteStockCommand fromRequest(UUID id, AjusteStockRequest request) {
        return new AjusteStockCommand(
                id,
                request.getCantidad(),
                request.getObservaciones()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new AjusteStockMessage(stockAnterior, stockNuevo, cantidad);
    }
}
