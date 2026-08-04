package com.kynsoft.report.applications.command.labor.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.LaborDto;
import com.kynsoft.report.domain.services.ILaborService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateLaborCommandHandler implements ICommandHandler<CreateLaborCommand> {

    private final ILaborService serviceImpl;

    @Override
    public void handle(CreateLaborCommand command) {
        serviceImpl.create(LaborDto.builder()
                .id(command.getId())
                .code(command.getCode())
                .name(command.getName())
                .build());
    }
}