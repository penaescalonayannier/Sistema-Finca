package com.kynsoft.report.applications.command.report.estadoCuenta.create;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.EstadoCuentaDto;
import com.kynsoft.report.domain.services.IEstadoCuentaService;
import org.springframework.stereotype.Component;

@Component
public class CreateEstadoCuentaCommandHandler implements ICommandHandler<CreateEstadoCuentaCommand> {

    private final IEstadoCuentaService serviceImpl;

    public CreateEstadoCuentaCommandHandler(IEstadoCuentaService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(CreateEstadoCuentaCommand command) {
        serviceImpl.create(EstadoCuentaDto
                .builder()
                .id(command.getId())
                .refOrigen(command.getRefOrigen())
                .refCorriente(command.getRefCorriente())
                .observaciones(command.getObservaciones())
                .importe(command.getImporte())
                .build());
        command.setId(command.getId());
    }
}
