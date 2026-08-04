package com.kynsoft.report.applications.command.tomaprestamo.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.ITomaPrestamoService;
import org.springframework.stereotype.Component;

@Component
public class DeleteTomaPrestamoCommandHandler implements ICommandHandler<DeleteTomaPrestamoCommand> {

    private final ITomaPrestamoService serviceImpl;

    public DeleteTomaPrestamoCommandHandler(ITomaPrestamoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteTomaPrestamoCommand command) {
        serviceImpl.delete(command.getId());
    }
}