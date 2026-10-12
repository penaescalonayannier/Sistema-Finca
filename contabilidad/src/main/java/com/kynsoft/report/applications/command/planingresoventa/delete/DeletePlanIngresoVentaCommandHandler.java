package com.kynsoft.report.applications.command.planingresoventa.delete;

import com.kynsoft.report.domain.services.IPlanIngresoVentaService;
import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeletePlanIngresoVentaCommandHandler implements ICommandHandler<DeletePlanIngresoVentaCommand> {
    private final IPlanIngresoVentaService service;
    @Override public void handle(DeletePlanIngresoVentaCommand command) { service.delete(command.getId()); }
}
