package com.kynsoft.report.applications.command.instrumentoTrabajo.delete;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteInstrumentoTrabajoCommand implements ICommand {
    private UUID id;

    public DeleteInstrumentoTrabajoCommand(UUID id) {
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteInstrumentoTrabajoMessage(id);
    }
}