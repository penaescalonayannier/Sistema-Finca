package com.kynsoft.report.applications.command.deudaTrabajador.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IDeudaTrabajadorService;
import org.springframework.stereotype.Component;

@Component
public class DeleteDeudaTrabajadorCommandHandler implements ICommandHandler<DeleteDeudaTrabajadorCommand> {

    private final IDeudaTrabajadorService service;

    public DeleteDeudaTrabajadorCommandHandler(IDeudaTrabajadorService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteDeudaTrabajadorCommand command) {
        service.delete(command.getId());
    }
}
