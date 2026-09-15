package com.kynsoft.report.applications.command.finca.reactivar;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IFincaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReactivarFincaCommandHandler implements ICommandHandler<ReactivarFincaCommand> {

    private final IFincaService serviceImpl;

    @Override
    public void handle(ReactivarFincaCommand command) {
        serviceImpl.reactivar(command.getId());
    }
}
