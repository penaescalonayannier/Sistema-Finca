package com.kynsoft.report.applications.command.almacen.reactivar;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IAlmacenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReactivarAlmacenCommandHandler implements ICommandHandler<ReactivarAlmacenCommand> {

    private final IAlmacenService service;

    @Override
    public void handle(ReactivarAlmacenCommand command) {
        service.reactivar(command.getId());
    }
}
