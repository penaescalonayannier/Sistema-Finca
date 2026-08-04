package com.kynsoft.report.applications.command.cargo.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.ICargoService;
import org.springframework.stereotype.Component;

@Component
public class DeleteCargoCommandHandler implements ICommandHandler<DeleteCargoCommand> {

    private final ICargoService serviceImpl;

    public DeleteCargoCommandHandler(ICargoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteCargoCommand command) {
        serviceImpl.delete(command.getId());
    }
}
