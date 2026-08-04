package com.kynsoft.report.applications.command.unidadmedida.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.UnidadMedidaDto;
import com.kynsoft.report.domain.services.IUnidadMedidaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateUnidadMedidaCommandHandler implements ICommandHandler<CreateUnidadMedidaCommand> {

    private final IUnidadMedidaService serviceImpl;

    @Override
    public void handle(CreateUnidadMedidaCommand command) {
        serviceImpl.create(UnidadMedidaDto.builder()
                .id(command.getId())
                .code(command.getCode())
                .name(command.getName())
                .build());
    }
}