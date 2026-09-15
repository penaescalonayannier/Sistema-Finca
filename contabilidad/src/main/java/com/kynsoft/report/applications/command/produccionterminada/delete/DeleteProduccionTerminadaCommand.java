package com.kynsoft.report.applications.command.produccionterminada.delete;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteProduccionTerminadaCommand implements ICommand {
    private final UUID id;

    // Resultado del servicio
    private Integer stockAnterior;
    private Integer stockNuevo;
    private Integer cantidadRevertida;

    public DeleteProduccionTerminadaCommand(UUID id) {
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteProduccionTerminadaMessage(id, stockAnterior, stockNuevo, cantidadRevertida);
    }
}
