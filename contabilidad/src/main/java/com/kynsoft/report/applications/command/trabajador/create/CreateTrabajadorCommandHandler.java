package com.kynsoft.report.applications.command.trabajador.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.ITrabajadorService;
import org.springframework.stereotype.Component;

@Component
public class CreateTrabajadorCommandHandler implements ICommandHandler<CreateTrabajadorCommand> {

    private final ITrabajadorService serviceImpl;

    public CreateTrabajadorCommandHandler(ITrabajadorService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(CreateTrabajadorCommand command) {
        serviceImpl.create(TrabajadorDto
                .builder()
                .id(command.getId())
                .ruc(command.getRuc())
                .nombre(command.getNombre())
                .cuenta(command.getCuenta())
                .activo(command.getActivo())
                .build());
    }
}
