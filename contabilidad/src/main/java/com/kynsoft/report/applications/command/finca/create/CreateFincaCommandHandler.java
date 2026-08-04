package com.kynsoft.report.applications.command.finca.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.FincaDto;
import com.kynsoft.report.domain.services.IFincaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateFincaCommandHandler implements ICommandHandler<CreateFincaCommand> {

    private final IFincaService serviceImpl;

    @Override
    public void handle(CreateFincaCommand command) {
        serviceImpl.create(FincaDto.builder()
                .id(command.getId())
                .code(command.getCode())
                .name(command.getName())
                .description(command.getDescription())
                .build());
    }
}