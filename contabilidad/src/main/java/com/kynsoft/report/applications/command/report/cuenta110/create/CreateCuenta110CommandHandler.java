package com.kynsoft.report.applications.command.report.cuenta110.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.Cuenta110EfectivoBancoDto;
import com.kynsoft.report.domain.services.ICuenta110EfectivoBancoService;
import org.springframework.stereotype.Component;

@Component
public class CreateCuenta110CommandHandler implements ICommandHandler<CreateCuenta110Command> {

    private final ICuenta110EfectivoBancoService serviceImpl;

    public CreateCuenta110CommandHandler(ICuenta110EfectivoBancoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(CreateCuenta110Command command) {
        serviceImpl.create(Cuenta110EfectivoBancoDto
                .builder()
                .id(command.getId())
                .observaciones(command.getObservaciones())
                .importe(command.getImporte())
                .build());
        command.setId(command.getId());
    }
}
