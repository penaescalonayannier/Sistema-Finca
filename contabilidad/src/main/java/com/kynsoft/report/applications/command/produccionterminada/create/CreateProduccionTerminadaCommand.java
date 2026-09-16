package com.kynsoft.report.applications.command.produccionterminada.create;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateProduccionTerminadaCommand implements ICommand {
    private UUID id;
    private UUID fincaId;
    private UUID productoId;
    private LocalDateTime fecha;
    private Double cantidadTerminada;
    private UUID almacenFincaProductoId;
    private UUID trabajadorEntregaId;
    private UUID trabajadorRecibeId;
    private String observaciones;
    private Double costoUnitario;
    private String lote;
    private String centroCosto;

    // Resultado del servicio
    private Double stockAnterior;
    private Double stockNuevo;

    public CreateProduccionTerminadaCommand(
            UUID fincaId,
            UUID productoId,
            LocalDateTime fecha,
            Double cantidadTerminada,
            UUID trabajadorEntregaId,
            UUID trabajadorRecibeId,
            String observaciones,
            Double costoUnitario,
            String lote,
            String centroCosto) {
        this.id = UUID.randomUUID();
        this.fincaId = fincaId;
        this.productoId = productoId;
        this.fecha = fecha;
        this.cantidadTerminada = cantidadTerminada;
        this.trabajadorEntregaId = trabajadorEntregaId;
        this.trabajadorRecibeId = trabajadorRecibeId;
        this.observaciones = observaciones;
        this.costoUnitario = costoUnitario;
        this.lote = lote;
        this.centroCosto = centroCosto;
    }

    public static CreateProduccionTerminadaCommand fromRequest(CreateProduccionTerminadaRequest request) {
        return new CreateProduccionTerminadaCommand(
                request.getFincaId(),
                request.getProductoId(),
                request.getFecha(),
                request.getCantidadTerminada(),
                request.getTrabajadorEntregaId(),
                request.getTrabajadorRecibeId(),
                request.getObservaciones(),
                request.getCostoUnitario(),
                request.getLote(),
                request.getCentroCosto()
        );
    }

    public void setCantidadTerminada(Number cantidadTerminada) {
        this.cantidadTerminada = cantidadTerminada != null ? cantidadTerminada.doubleValue() : null;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateProduccionTerminadaMessage(id, stockAnterior, stockNuevo);
    }
}
