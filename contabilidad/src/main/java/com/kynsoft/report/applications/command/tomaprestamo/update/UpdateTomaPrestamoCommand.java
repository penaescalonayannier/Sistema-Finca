package com.kynsoft.report.applications.command.tomaprestamo.update;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.domain.dto.enumerativos.TipoTomaPrestamo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class UpdateTomaPrestamoCommand implements ICommand {
    private final UUID id;
    private final Double importe;
    private final LocalDate fecha;
    private final String cuentaDestino;
    private final TipoTomaPrestamo tipo;
    private final String observaciones;
    private String creditoId;

    public UpdateTomaPrestamoCommand(UUID id, Double importe, LocalDate fecha, String cuentaDestino,
                                   TipoTomaPrestamo tipo, String observaciones, String creditoId) {
        this.id = id;
        this.importe = importe;
        this.fecha = fecha;
        this.cuentaDestino = cuentaDestino;
        this.tipo = tipo;
        this.observaciones = observaciones;
        this.creditoId = creditoId;
    }

    public static UpdateTomaPrestamoCommand fromRequest(UpdateTomaPrestamoRequest request, UUID id) {
        return new UpdateTomaPrestamoCommand(
                id,
                request.getImporte(),
                request.getFecha(),
                request.getCuentaDestino(),
                request.getTipo(),
                request.getObservaciones(),
                request.getCreditoId()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateTomaPrestamoMessage(id);
    }
}