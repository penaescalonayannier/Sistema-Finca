package com.kynsoft.report.applications.command.produccionterminada.update;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateProduccionTerminadaCommand implements ICommand {
    private UUID id;
    private Integer cantidadTerminada;
    private UUID trabajadorEntregaId;
    private UUID trabajadorRecibeId;
    private String observaciones;

    // Resultado del servicio
    private Integer stockAnterior;
    private Integer stockNuevo;
    private Integer ajuste;

    public UpdateProduccionTerminadaCommand(
            UUID id,
            Integer cantidadTerminada,
            UUID trabajadorEntregaId,
            UUID trabajadorRecibeId,
            String observaciones) {
        this.id = id;
        this.cantidadTerminada = cantidadTerminada;
        this.trabajadorEntregaId = trabajadorEntregaId;
        this.trabajadorRecibeId = trabajadorRecibeId;
        this.observaciones = observaciones;
    }

    public static UpdateProduccionTerminadaCommand fromRequest(UpdateProduccionTerminadaRequest request, UUID id) {
        return new UpdateProduccionTerminadaCommand(
                id,
                request.getCantidadTerminada(),
                request.getTrabajadorEntregaId(),
                request.getTrabajadorRecibeId(),
                request.getObservaciones()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateProduccionTerminadaMessage(id, stockAnterior, stockNuevo, ajuste);
    }
}
