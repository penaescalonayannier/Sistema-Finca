package com.kynsoft.report.applications.command.trabajadorReporte.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.ITrabajadorReporteService;
import org.springframework.stereotype.Component;

@Component
public class DeleteTrabajadorReporteCommandHandler implements ICommandHandler<DeleteTrabajadorReporteCommand> {

    private final ITrabajadorReporteService reportService;

    public DeleteTrabajadorReporteCommandHandler(ITrabajadorReporteService reportService) {
        this.reportService = reportService;
    }

    @Override
    public void handle(DeleteTrabajadorReporteCommand command) {
        reportService.remover(command.getId());
    }
}