package com.kynsoft.report.applications.command.deudaTrabajador.update;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateDeudaTrabajadorCommand implements ICommand {

    private UUID id;
    private UUID trabajadorId;
    private Double importe;

    public UpdateDeudaTrabajadorCommand(UUID id, UUID trabajadorId, Double importe) {
        this.id = id;
        this.trabajadorId = trabajadorId;
        this.importe = importe;
    }

    public static UpdateDeudaTrabajadorCommand fromRequest(UpdateDeudaTrabajadorRequest request) {
        return new UpdateDeudaTrabajadorCommand(
                request.getId(),
                request.getTrabajadorId(),
                request.getImporte()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateDeudaTrabajadorMessage(id);
    }
}
