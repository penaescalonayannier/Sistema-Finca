package com.kynsoft.report.applications.command.deudaTrabajador.create;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateDeudaTrabajadorCommand implements ICommand {

    private UUID id;
    private UUID trabajadorId;
    private Double importe;

    public CreateDeudaTrabajadorCommand(UUID trabajadorId, Double importe) {
        this.id = UUID.randomUUID();
        this.trabajadorId = trabajadorId;
        this.importe = importe;
    }

    public static CreateDeudaTrabajadorCommand fromRequest(CreateDeudaTrabajadorRequest request) {
        return new CreateDeudaTrabajadorCommand(
                request.getTrabajadorId(),
                request.getImporte()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateDeudaTrabajadorMessage(id);
    }
}
