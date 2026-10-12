package com.kynsoft.report.applications.command.planingresoventa.delete;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;
import lombok.Getter;

@Getter
public class DeletePlanIngresoVentaCommand implements ICommand {
    private final UUID id;
    public DeletePlanIngresoVentaCommand(UUID id) { this.id = id; }
    @Override public ICommandMessage getMessage() { return new DeletePlanIngresoVentaMessage(id); }
}
