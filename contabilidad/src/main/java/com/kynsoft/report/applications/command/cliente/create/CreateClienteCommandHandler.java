package com.kynsoft.report.applications.command.cliente.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.ClienteDto;
import com.kynsoft.report.domain.services.IClienteService;
import org.springframework.stereotype.Component;

@Component
public class CreateClienteCommandHandler implements ICommandHandler<CreateClienteCommand> {

    private final IClienteService serviceImpl;

    public CreateClienteCommandHandler(IClienteService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(CreateClienteCommand command) {
        serviceImpl.create(ClienteDto
                .builder()
                .id(command.getId())
                .cuenta(command.getCuenta())
                .nombre(command.getNombre())
                .ruc(command.getRuc())
                .direccion(command.getDireccion())
                .build());
    }
}
