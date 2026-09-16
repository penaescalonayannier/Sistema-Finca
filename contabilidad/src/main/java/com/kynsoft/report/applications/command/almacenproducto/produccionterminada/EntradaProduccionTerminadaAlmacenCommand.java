package com.kynsoft.report.applications.command.almacenproducto.produccionterminada;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EntradaProduccionTerminadaAlmacenCommand implements ICommand {
    private final UUID almacenId;
    private final UUID almacenFincaProductoId;
    private final Double cantidadTerminada;
    private final UUID trabajadorEntregaId;
    private final UUID trabajadorRecibeId;
    private final String observaciones;
    private final Double costoUnitario;
    private final String lote;
    private final String centroCosto;
    private UUID produccionTerminadaId;
    private Double stockNuevo;

    public EntradaProduccionTerminadaAlmacenCommand(
            UUID almacenId,
            UUID almacenFincaProductoId,
            Double cantidadTerminada,
            UUID trabajadorEntregaId,
            UUID trabajadorRecibeId,
            String observaciones,
            Double costoUnitario,
            String lote,
            String centroCosto) {
        this.almacenId = almacenId;
        this.almacenFincaProductoId = almacenFincaProductoId;
        this.cantidadTerminada = cantidadTerminada;
        this.trabajadorEntregaId = trabajadorEntregaId;
        this.trabajadorRecibeId = trabajadorRecibeId;
        this.observaciones = observaciones;
        this.costoUnitario = costoUnitario;
        this.lote = lote;
        this.centroCosto = centroCosto;
    }

    public static EntradaProduccionTerminadaAlmacenCommand fromRequest(
            UUID almacenId, EntradaProduccionTerminadaAlmacenRequest request) {
        return new EntradaProduccionTerminadaAlmacenCommand(
                almacenId,
                request.getAlmacenFincaProductoId(),
                request.getCantidadTerminada(),
                request.getTrabajadorEntregaId(),
                request.getTrabajadorRecibeId(),
                request.getObservaciones(),
                request.getCostoUnitario(),
                request.getLote(),
                request.getCentroCosto());
    }

    @Override
    public ICommandMessage getMessage() {
        return new EntradaProduccionTerminadaAlmacenMessage(produccionTerminadaId, almacenFincaProductoId, stockNuevo);
    }
}
