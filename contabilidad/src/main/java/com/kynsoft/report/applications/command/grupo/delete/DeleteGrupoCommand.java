package com.kynsoft.report.applications.command.grupo.delete;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteGrupoCommand implements ICommand {

    private UUID id;

    public DeleteGrupoCommand(UUID id) {
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteGrupoMessage(id);
    }
}
