package com.kynsoft.report.applications.command.cargo.update;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.CargoDto;
import com.kynsoft.report.domain.services.ICargoService;
import org.springframework.stereotype.Component;

@Component
public class UpdateCargoCommandHandler implements ICommandHandler<UpdateCargoCommand> {

    private final ICargoService serviceImpl;

    public UpdateCargoCommandHandler(ICargoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(UpdateCargoCommand command) {
        serviceImpl.update(CargoDto
                .builder()
                .id(command.getId())
                .name(command.getName())
                .description(command.getDescription())
                .salarioEscala(command.getSalarioEscala())
                .build());
    }
}
