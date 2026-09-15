package com.kynsoft.report.applications.command.bloque.delete;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteBloqueCommand implements ICommand {
    private UUID id;

    public DeleteBloqueCommand(UUID id) {
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteBloqueMessage(id);
    }
}