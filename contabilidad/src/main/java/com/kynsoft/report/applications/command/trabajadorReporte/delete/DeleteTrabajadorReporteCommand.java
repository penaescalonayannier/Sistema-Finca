package com.kynsoft.report.applications.command.trabajadorReporte.delete;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteTrabajadorReporteCommand implements ICommand {
    private UUID id;

    public DeleteTrabajadorReporteCommand(UUID id) {
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteTrabajadorReporteMessage(id);
    }
}