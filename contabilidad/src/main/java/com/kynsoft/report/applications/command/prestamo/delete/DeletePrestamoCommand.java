package com.kynsoft.report.applications.command.prestamo.delete;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeletePrestamoCommand implements ICommand {

    private UUID id;

    public DeletePrestamoCommand(UUID id) {
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeletePrestamoMessage(id);
    }
}
