package com.kynsoft.report.applications.command.unidadmedida.delete;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteUnidadMedidaCommand implements ICommand {
    private UUID id;

    public DeleteUnidadMedidaCommand(UUID id) {
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteUnidadMedidaMessage(id);
    }
}