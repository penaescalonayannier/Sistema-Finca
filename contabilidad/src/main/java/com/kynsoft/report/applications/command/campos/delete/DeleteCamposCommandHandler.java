package com.kynsoft.report.applications.command.campos.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.ICamposService;
import org.springframework.stereotype.Component;

@Component
public class DeleteCamposCommandHandler implements ICommandHandler<DeleteCamposCommand> {

    private final ICamposService serviceImpl;

    public DeleteCamposCommandHandler(ICamposService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteCamposCommand command) {
        serviceImpl.delete(command.getId());
    }
}