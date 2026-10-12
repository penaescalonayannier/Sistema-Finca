package com.kynsoft.report.applications.command.planingresoventa.create;

import com.kynsoft.report.applications.command.planingresoventa.PlanIngresoVentaRequest;
import com.kynsoft.report.domain.dto.PlanIngresoVentaDto;
import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;
import lombok.Getter;

@Getter
public class CreatePlanIngresoVentaCommand implements ICommand {
    private final PlanIngresoVentaDto plan;

    private CreatePlanIngresoVentaCommand(PlanIngresoVentaDto plan) {
        this.plan = plan;
    }

    public static CreatePlanIngresoVentaCommand fromRequest(PlanIngresoVentaRequest request) {
        return new CreatePlanIngresoVentaCommand(request.toDto(UUID.randomUUID()));
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePlanIngresoVentaMessage(plan.getId());
    }
}
