package com.kynsoft.report.applications.command.almacen.establecerprincipal;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IAlmacenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EstablecerPrincipalCommandHandler implements ICommandHandler<EstablecerPrincipalCommand> {

    private final IAlmacenService service;

    @Override
    public void handle(EstablecerPrincipalCommand command) {
        service.establecerPrincipal(command.getId());
    }
}
