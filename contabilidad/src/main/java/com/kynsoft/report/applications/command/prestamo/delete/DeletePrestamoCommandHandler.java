package com.kynsoft.report.applications.command.prestamo.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IPrestamoService;
import org.springframework.stereotype.Component;

@Component
public class DeletePrestamoCommandHandler implements ICommandHandler<DeletePrestamoCommand> {

    private final IPrestamoService serviceImpl;

    public DeletePrestamoCommandHandler(IPrestamoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeletePrestamoCommand command) {
        serviceImpl.delete(command.getId());
    }
}
