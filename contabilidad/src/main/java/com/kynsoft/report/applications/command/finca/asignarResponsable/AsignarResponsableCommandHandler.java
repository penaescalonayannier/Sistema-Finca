package com.kynsoft.report.applications.command.finca.asignarResponsable;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IFincaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AsignarResponsableCommandHandler implements ICommandHandler<AsignarResponsableCommand> {

    private final IFincaService serviceImpl;

    @Override
    public void handle(AsignarResponsableCommand command) {
        serviceImpl.asignarResponsable(command.getFincaId(), command.getResponsableId());
    }
}
