package com.kynsoft.report.applications.command.deudaTrabajador.create;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDto;
import com.kynsoft.report.domain.services.IDeudaTrabajadorService;
import org.springframework.stereotype.Component;

@Component
public class CreateDeudaTrabajadorCommandHandler implements ICommandHandler<CreateDeudaTrabajadorCommand> {

    private final IDeudaTrabajadorService service;

    public CreateDeudaTrabajadorCommandHandler(IDeudaTrabajadorService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateDeudaTrabajadorCommand command) {
        DeudaTrabajadorDto dto = DeudaTrabajadorDto.builder()
                .id(command.getId())
                .trabajadorId(command.getTrabajadorId())
                .importe(command.getImporte())
                .build();

        service.create(dto);
    }
}
