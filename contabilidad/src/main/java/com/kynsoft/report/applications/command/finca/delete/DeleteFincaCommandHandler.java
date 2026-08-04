package com.kynsoft.report.applications.command.finca.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IFincaService;
import org.springframework.stereotype.Component;

@Component
public class DeleteFincaCommandHandler implements ICommandHandler<DeleteFincaCommand> {

    private final IFincaService serviceImpl;

    public DeleteFincaCommandHandler(IFincaService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteFincaCommand command) {
        serviceImpl.delete(command.getId());
    }
}