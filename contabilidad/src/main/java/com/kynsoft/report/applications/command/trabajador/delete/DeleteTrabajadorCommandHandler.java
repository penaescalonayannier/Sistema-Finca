package com.kynsoft.report.applications.command.trabajador.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.ITrabajadorService;
import org.springframework.stereotype.Component;

@Component
public class DeleteTrabajadorCommandHandler implements ICommandHandler<DeleteTrabajadorCommand> {

    private final ITrabajadorService serviceImpl;

    public DeleteTrabajadorCommandHandler(ITrabajadorService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteTrabajadorCommand command) {
        serviceImpl.delete(command.getId());
    }
}