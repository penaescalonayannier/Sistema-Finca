package com.kynsoft.report.applications.command.fincaproducto.entradafactura;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EntradaFacturaCommand implements ICommand {
    private UUID id;
    private Integer cantidad;
    private String numeroFactura;
    private String observaciones;
    private Integer stockAnterior;
    private Integer stockNuevo;

    public EntradaFacturaCommand(UUID id, Integer cantidad, String numeroFactura, String observaciones) {
        this.id = id;
        this.cantidad = cantidad;
        this.numeroFactura = numeroFactura;
        this.observaciones = observaciones;
    }

    public static EntradaFacturaCommand fromRequest(UUID id, EntradaFacturaRequest request) {
        return new EntradaFacturaCommand(
                id,
                request.getCantidad(),
                request.getNumeroFactura(),
                request.getObservaciones()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new EntradaFacturaMessage(stockAnterior, stockNuevo, cantidad);
    }
}
