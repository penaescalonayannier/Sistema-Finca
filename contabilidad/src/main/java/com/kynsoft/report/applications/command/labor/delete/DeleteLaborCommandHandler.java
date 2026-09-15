package com.kynsoft.report.applications.command.labor.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.ILaborService;
import org.springframework.stereotype.Component;

@Component
public class DeleteLaborCommandHandler implements ICommandHandler<DeleteLaborCommand> {

    private final ILaborService serviceImpl;

    public DeleteLaborCommandHandler(ILaborService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteLaborCommand command) {
        serviceImpl.delete(command.getId());
    }
}