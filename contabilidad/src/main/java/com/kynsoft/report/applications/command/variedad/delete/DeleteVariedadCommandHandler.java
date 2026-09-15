package com.kynsoft.report.applications.command.variedad.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IVariedadService;
import org.springframework.stereotype.Component;

@Component
public class DeleteVariedadCommandHandler implements ICommandHandler<DeleteVariedadCommand> {

    private final IVariedadService serviceImpl;

    public DeleteVariedadCommandHandler(IVariedadService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteVariedadCommand command) {
        serviceImpl.delete(command.getId());
    }
}