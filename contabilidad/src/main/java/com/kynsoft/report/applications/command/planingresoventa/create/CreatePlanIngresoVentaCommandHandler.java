package com.kynsoft.report.applications.command.planingresoventa.create;

import com.kynsoft.report.domain.services.IPlanIngresoVentaService;
import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreatePlanIngresoVentaCommandHandler implements ICommandHandler<CreatePlanIngresoVentaCommand> {
    private final IPlanIngresoVentaService service;
    @Override public void handle(CreatePlanIngresoVentaCommand command) { service.create(command.getPlan()); }
}
