package com.kynsoft.report.applications.command.tomaprestamo.delete;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteTomaPrestamoCommand implements ICommand {
    private UUID id;

    public DeleteTomaPrestamoCommand(UUID id) {
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteTomaPrestamoMessage(id);
    }
}