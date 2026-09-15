package com.kynsoft.report.applications.command.fincaproducto.entradaconduce;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EntradaConduceCommand implements ICommand {
    private UUID id;
    private Integer cantidad;
    private String observaciones;
    private Double stockAnterior;
    private Double stockNuevo;

    public EntradaConduceCommand(UUID id, Integer cantidad, String observaciones) {
        this.id = id;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
    }

    public static EntradaConduceCommand fromRequest(UUID id, EntradaConduceRequest request) {
        return new EntradaConduceCommand(
                id,
                request.getCantidad(),
                request.getObservaciones()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new EntradaConduceMessage(stockAnterior, stockNuevo, cantidad);
    }
}
