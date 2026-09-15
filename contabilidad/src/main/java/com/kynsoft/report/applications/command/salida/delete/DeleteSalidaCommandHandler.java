package com.kynsoft.report.applications.command.salida.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.ISalidaService;
import org.springframework.stereotype.Component;

@Component
public class DeleteSalidaCommandHandler implements ICommandHandler<DeleteSalidaCommand> {

    private final ISalidaService service;

    public DeleteSalidaCommandHandler(ISalidaService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteSalidaCommand command) {
        service.delete(command.getId());
    }
}
