package com.kynsoft.report.applications.command.variedad.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.VariedadDto;
import com.kynsoft.report.domain.services.IVariedadService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateVariedadCommandHandler implements ICommandHandler<CreateVariedadCommand> {

    private final IVariedadService serviceImpl;

    @Override
    public void handle(CreateVariedadCommand command) {
        serviceImpl.create(VariedadDto.builder()
                .id(command.getId())
                .code(command.getCode())
                .name(command.getName())
                .build());
    }
}