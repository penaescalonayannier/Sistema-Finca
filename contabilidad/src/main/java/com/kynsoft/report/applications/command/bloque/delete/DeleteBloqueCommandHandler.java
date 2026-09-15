package com.kynsoft.report.applications.command.bloque.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IBloqueService;
import org.springframework.stereotype.Component;

@Component
public class DeleteBloqueCommandHandler implements ICommandHandler<DeleteBloqueCommand> {

    private final IBloqueService serviceImpl;

    public DeleteBloqueCommandHandler(IBloqueService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteBloqueCommand command) {
        serviceImpl.delete(command.getId());
    }
}