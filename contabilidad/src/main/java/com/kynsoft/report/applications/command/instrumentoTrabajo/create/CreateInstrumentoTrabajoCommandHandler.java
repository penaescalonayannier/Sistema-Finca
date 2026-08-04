package com.kynsoft.report.applications.command.instrumentoTrabajo.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.InstrumentoTrabajoDto;
import com.kynsoft.report.domain.services.IInstrumentoTrabajoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateInstrumentoTrabajoCommandHandler implements ICommandHandler<CreateInstrumentoTrabajoCommand> {

    private final IInstrumentoTrabajoService serviceImpl;

    @Override
    public void handle(CreateInstrumentoTrabajoCommand command) {
        serviceImpl.create(InstrumentoTrabajoDto.builder()
                .id(command.getId())
                .code(command.getCode())
                .name(command.getName())
                .build());
    }
}