package com.kynsoft.report.applications.command.cliente.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.ClienteDto;
import com.kynsoft.report.domain.services.IClienteService;
import org.springframework.stereotype.Component;

@Component
public class UpdateClienteCommandHandler implements ICommandHandler<UpdateClienteCommand> {

    private final IClienteService serviceImpl;

    public UpdateClienteCommandHandler(IClienteService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(UpdateClienteCommand command) {
        serviceImpl.update(ClienteDto
                .builder()
                .id(command.getId())
                .cuenta(command.getCuenta())
                .nombre(command.getNombre())
                .ruc(command.getRuc())
                .direccion(command.getDireccion())
                .build());
    }
}
