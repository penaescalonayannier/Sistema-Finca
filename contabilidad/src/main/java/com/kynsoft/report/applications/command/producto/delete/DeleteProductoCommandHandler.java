package com.kynsoft.report.applications.command.producto.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IProductoService;
import org.springframework.stereotype.Component;

@Component
public class DeleteProductoCommandHandler implements ICommandHandler<DeleteProductoCommand> {

    private final IProductoService serviceImpl;

    public DeleteProductoCommandHandler(IProductoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteProductoCommand command) {
        serviceImpl.delete(command.getId());
    }
}