package com.kynsoft.report.applications.command.reporte.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IReporteService;
import org.springframework.stereotype.Component;

@Component
public class DeleteReporteCommandHandler implements ICommandHandler<DeleteReporteCommand> {

    private final IReporteService reportService;

    public DeleteReporteCommandHandler(IReporteService reportService) {
        this.reportService = reportService;
    }

    @Override
    public void handle(DeleteReporteCommand command) {
        reportService.delete(command.getId());
    }
}