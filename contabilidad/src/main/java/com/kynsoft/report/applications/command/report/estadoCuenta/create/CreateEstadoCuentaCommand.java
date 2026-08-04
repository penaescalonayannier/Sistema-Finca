package com.kynsoft.report.applications.command.report.estadoCuenta.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateEstadoCuentaCommand implements ICommand {

    private UUID id;
    private String fecha;
    private String refOrigen;
    private String refCorriente;
    private String observaciones;
    private String tipo;
    private Double importe;
    private UUID clienteId;

    public CreateEstadoCuentaCommand(String fecha, String refOrigen, String refCorriente, String observaciones, String tipo, Double importe, UUID clienteId) {
        this.id = UUID.randomUUID();
        this.fecha = fecha;
        this.refOrigen = refOrigen;
        this.refCorriente = refCorriente;
        this.observaciones = observaciones;
        this.tipo = tipo;
        this.importe = importe;
        this.clienteId = clienteId;
    }

    public static CreateEstadoCuentaCommand fromRequest(CreateEstadoCuentaRequest request) {
        return new CreateEstadoCuentaCommand(
                request.getFecha(),
                request.getRefOrigen(),
                request.getRefCorriente(),
                request.getObservaciones(),
                request.getType(),
                request.getImporte(),
                request.getClienteId()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateEstadoCuentaMessage(id);
    }
}
