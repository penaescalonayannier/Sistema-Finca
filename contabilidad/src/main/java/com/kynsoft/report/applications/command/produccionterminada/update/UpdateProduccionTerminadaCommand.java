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
    private Double cantidadTerminada;
    private UUID trabajadorEntregaId;
    private UUID trabajadorRecibeId;
    private String observaciones;

    // Resultado del servicio
    private Double stockAnterior;
    private Double stockNuevo;
    private Double ajuste;

    public UpdateProduccionTerminadaCommand(
            UUID id,
            Double cantidadTerminada,
            UUID trabajadorEntregaId,
            UUID trabajadorRecibeId,
            String observaciones) {
        this.id = id;
        this.cantidadTerminada = cantidadTerminada;
        this.trabajadorEntregaId = trabajadorEntregaId;
        this.trabajadorRecibeId = trabajadorRecibeId;
        this.observaciones = observaciones;
    }

    public void setCantidadTerminada(Number cantidadTerminada) {
        this.cantidadTerminada = cantidadTerminada != null ? cantidadTerminada.doubleValue() : null;
    }

    public void setAjuste(Number ajuste) {
        this.ajuste = ajuste != null ? ajuste.doubleValue() : null;
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
