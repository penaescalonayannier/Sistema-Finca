package com.kynsoft.report.applications.command.instrumentoTrabajo.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IInstrumentoTrabajoService;
import org.springframework.stereotype.Component;

@Component
public class DeleteInstrumentoTrabajoCommandHandler implements ICommandHandler<DeleteInstrumentoTrabajoCommand> {

    private final IInstrumentoTrabajoService serviceImpl;

    public DeleteInstrumentoTrabajoCommandHandler(IInstrumentoTrabajoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteInstrumentoTrabajoCommand command) {
        serviceImpl.delete(command.getId());
    }
}