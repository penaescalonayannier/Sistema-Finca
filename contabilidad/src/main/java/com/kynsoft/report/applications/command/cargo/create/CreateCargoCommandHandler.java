package com.kynsoft.report.applications.command.cargo.create;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.CargoDto;
import com.kynsoft.report.domain.services.ICargoService;
import org.springframework.stereotype.Component;

@Component
public class CreateCargoCommandHandler implements ICommandHandler<CreateCargoCommand> {

    private final ICargoService serviceImpl;

    public CreateCargoCommandHandler(ICargoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(CreateCargoCommand command) {
        serviceImpl.create(CargoDto
                .builder()
                .id(command.getId())
                .name(command.getName())
                .description(command.getDescription())
                .salarioEscala(command.getSalarioEscala())
                .build());
    }
}
