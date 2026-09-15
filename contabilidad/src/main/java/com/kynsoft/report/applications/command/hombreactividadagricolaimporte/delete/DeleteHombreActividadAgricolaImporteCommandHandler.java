package com.kynsoft.report.applications.command.hombreactividadagricolaimporte.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IHombreActividadAgricolaImporteService;
import org.springframework.stereotype.Component;

@Component
public class DeleteHombreActividadAgricolaImporteCommandHandler implements ICommandHandler<DeleteHombreActividadAgricolaImporteCommand> {

    private final IHombreActividadAgricolaImporteService serviceImpl;

    public DeleteHombreActividadAgricolaImporteCommandHandler(IHombreActividadAgricolaImporteService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteHombreActividadAgricolaImporteCommand command) {
        serviceImpl.delete(command.getId());
    }
}