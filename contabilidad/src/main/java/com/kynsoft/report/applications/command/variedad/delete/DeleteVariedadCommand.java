package com.kynsoft.report.applications.command.variedad.delete;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteVariedadCommand implements ICommand {
    private UUID id;

    public DeleteVariedadCommand(UUID id) {
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteVariedadMessage(id);
    }
}