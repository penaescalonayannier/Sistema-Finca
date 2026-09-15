package com.kynsoft.report.applications.command.unidadmedida.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IUnidadMedidaService;
import org.springframework.stereotype.Component;

@Component
public class DeleteUnidadMedidaCommandHandler implements ICommandHandler<DeleteUnidadMedidaCommand> {

    private final IUnidadMedidaService serviceImpl;

    public DeleteUnidadMedidaCommandHandler(IUnidadMedidaService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteUnidadMedidaCommand command) {
        serviceImpl.delete(command.getId());
    }
}