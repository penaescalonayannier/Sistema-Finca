package com.kynsoft.report.applications.command.deudaTrabajador.update;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDto;
import com.kynsoft.report.domain.services.IDeudaTrabajadorService;
import org.springframework.stereotype.Component;

@Component
public class UpdateDeudaTrabajadorCommandHandler implements ICommandHandler<UpdateDeudaTrabajadorCommand> {

    private final IDeudaTrabajadorService service;

    public UpdateDeudaTrabajadorCommandHandler(IDeudaTrabajadorService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateDeudaTrabajadorCommand command) {
        // Verificar que existe
        service.findById(command.getId());

        DeudaTrabajadorDto dto = DeudaTrabajadorDto.builder()
                .id(command.getId())
                .trabajadorId(command.getTrabajadorId())
                .importe(command.getImporte())
                .build();

        service.update(dto);
    }
}
