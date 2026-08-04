package com.kynsoft.report.applications.command.reporte.delete;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteReporteCommand implements ICommand {
    private UUID id;

    public DeleteReporteCommand(UUID id) {
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteReporteMessage(id);
    }
}