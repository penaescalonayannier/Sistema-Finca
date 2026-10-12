package com.kynsoft.report.applications.command.planingresoventa.update;

import com.kynsoft.report.applications.command.planingresoventa.PlanIngresoVentaRequest;
import com.kynsoft.report.domain.dto.PlanIngresoVentaDto;
import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UpdatePlanIngresoVentaCommand implements ICommand {
    private final PlanIngresoVentaDto plan;
    private UpdatePlanIngresoVentaCommand(PlanIngresoVentaDto plan) { this.plan = plan; }
    public static UpdatePlanIngresoVentaCommand fromRequest(PlanIngresoVentaRequest request, UUID id) {
        return new UpdatePlanIngresoVentaCommand(request.toDto(id));
    }
    @Override public ICommandMessage getMessage() { return new UpdatePlanIngresoVentaMessage(plan.getId()); }
}
