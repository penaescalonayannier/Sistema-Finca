package com.kynsoft.report.applications.command.cepa.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.ICepaService;
import org.springframework.stereotype.Component;

@Component
public class DeleteCepaCommandHandler implements ICommandHandler<DeleteCepaCommand> {

    private final ICepaService serviceImpl;

    public DeleteCepaCommandHandler(ICepaService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteCepaCommand command) {
        serviceImpl.delete(command.getId());
    }
}