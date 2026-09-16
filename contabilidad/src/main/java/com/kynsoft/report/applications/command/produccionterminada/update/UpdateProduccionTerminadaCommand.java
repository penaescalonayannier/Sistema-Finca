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
    private Double costoUnitario;
    private String lote;
    private String centroCosto;

    // Resultado del servicio
    private Double stockAnterior;
    private Double stockNuevo;
    private Double ajuste;

    public UpdateProduccionTerminadaCommand(
            UUID id,
            Double cantidadTerminada,
            UUID trabajadorEntregaId,
            UUID trabajadorRecibeId,
            String observaciones, Double costoUnitario, String lote, String centroCosto) {
        this.id = id;
        this.cantidadTerminada = cantidadTerminada;
        this.trabajadorEntregaId = trabajadorEntregaId;
        this.trabajadorRecibeId = trabajadorRecibeId;
        this.observaciones = observaciones;
        this.costoUnitario = costoUnitario;
        this.lote = lote;
        this.centroCosto = centroCosto;
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
                request.getObservaciones(),
                request.getCostoUnitario(),
                request.getLote(),
                request.getCentroCosto()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateProduccionTerminadaMessage(id, stockAnterior, stockNuevo, ajuste);
    }
}
