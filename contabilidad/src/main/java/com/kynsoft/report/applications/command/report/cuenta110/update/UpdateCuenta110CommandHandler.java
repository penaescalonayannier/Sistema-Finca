package com.kynsoft.report.applications.command.report.cuenta110.update;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.Cuenta110EfectivoBancoDto;
import com.kynsoft.report.domain.services.ICuenta110EfectivoBancoService;
import org.springframework.stereotype.Component;

@Component
public class UpdateCuenta110CommandHandler implements ICommandHandler<UpdateCuenta110Command> {

    private final ICuenta110EfectivoBancoService serviceImpl;

    public UpdateCuenta110CommandHandler(ICuenta110EfectivoBancoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(UpdateCuenta110Command command) {
        serviceImpl.update(Cuenta110EfectivoBancoDto
                .builder()
                .id(command.getId())
                .observaciones(command.getObservaciones())
                .importe(command.getImporte())
                .build());
        command.setId(command.getId());
    }
}
