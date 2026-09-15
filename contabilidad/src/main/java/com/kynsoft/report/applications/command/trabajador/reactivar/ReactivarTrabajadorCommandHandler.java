package com.kynsoft.report.applications.command.trabajador.reactivar;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.ITrabajadorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReactivarTrabajadorCommandHandler implements ICommandHandler<ReactivarTrabajadorCommand> {

    private final ITrabajadorService serviceImpl;

    @Override
    public void handle(ReactivarTrabajadorCommand command) {
        serviceImpl.reactivar(command.getId());
    }
}
