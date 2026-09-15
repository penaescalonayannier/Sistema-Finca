package com.kynsoft.report.applications.command.almacenproducto.transferencia;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TransferenciaAlmacenCommand implements ICommand {
    private UUID almacenFincaProductoId;
    private UUID destinoAlmacenId;
    private Integer cantidad;
    private String observaciones;
    private Integer stockOrigenNuevo;
    private Integer stockDestinoNuevo;

    public TransferenciaAlmacenCommand(UUID almacenFincaProductoId, UUID destinoAlmacenId,
                                        Integer cantidad, String observaciones) {
        this.almacenFincaProductoId = almacenFincaProductoId;
        this.destinoAlmacenId = destinoAlmacenId;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
    }

    public static TransferenciaAlmacenCommand fromRequest(TransferenciaAlmacenRequest request) {
        return new TransferenciaAlmacenCommand(
                request.getAlmacenFincaProductoId(),
                request.getDestinoAlmacenId(),
                request.getCantidad(),
                request.getObservaciones()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new TransferenciaAlmacenMessage(almacenFincaProductoId, destinoAlmacenId,
                cantidad, stockOrigenNuevo, stockDestinoNuevo);
    }
}
