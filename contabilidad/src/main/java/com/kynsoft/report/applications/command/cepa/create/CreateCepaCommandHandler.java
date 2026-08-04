package com.kynsoft.report.applications.command.cepa.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.CepaDto;
import com.kynsoft.report.domain.services.ICepaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateCepaCommandHandler implements ICommandHandler<CreateCepaCommand> {

    private final ICepaService serviceImpl;

    @Override
    public void handle(CreateCepaCommand command) {
        serviceImpl.create(CepaDto.builder()
                .id(command.getId())
                .code(command.getCode())
                .name(command.getName())
                .build());
    }
}