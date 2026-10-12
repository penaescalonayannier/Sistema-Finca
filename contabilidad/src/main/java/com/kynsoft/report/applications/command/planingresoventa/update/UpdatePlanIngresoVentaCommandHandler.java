package com.kynsoft.report.applications.command.planingresoventa.update;

import com.kynsoft.report.domain.services.IPlanIngresoVentaService;
import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdatePlanIngresoVentaCommandHandler implements ICommandHandler<UpdatePlanIngresoVentaCommand> {
    private final IPlanIngresoVentaService service;
    @Override public void handle(UpdatePlanIngresoVentaCommand command) { service.update(command.getPlan()); }
}
