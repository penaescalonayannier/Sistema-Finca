package com.kynsoft.report.applications.command.cliente.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IClienteService;
import org.springframework.stereotype.Component;

@Component
public class DeleteClienteCommandHandler implements ICommandHandler<DeleteClienteCommand> {

    private final IClienteService serviceImpl;

    public DeleteClienteCommandHandler(IClienteService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteClienteCommand command) {
        serviceImpl.delete(command.getId());
    }
}
