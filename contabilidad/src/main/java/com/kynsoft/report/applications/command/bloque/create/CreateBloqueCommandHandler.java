package com.kynsoft.report.applications.command.bloque.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.BloqueDto;
import com.kynsoft.report.domain.services.IBloqueService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateBloqueCommandHandler implements ICommandHandler<CreateBloqueCommand> {

    private final IBloqueService serviceImpl;

    @Override
    public void handle(CreateBloqueCommand command) {
        serviceImpl.create(BloqueDto.builder()
                .id(command.getId())
                .code(command.getCode())
                .name(command.getName())
                .build());
    }
}