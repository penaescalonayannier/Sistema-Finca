package com.kynsoft.report.applications.command.variedad.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.VariedadDto;
import com.kynsoft.report.domain.services.IVariedadService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateVariedadCommandHandler implements ICommandHandler<UpdateVariedadCommand> {

    private final IVariedadService serviceImpl;

    @Override
    public void handle(UpdateVariedadCommand command) {
        // 1. Buscar la entidad existente por ID
        VariedadDto dto = serviceImpl.findById(command.getId());

        // 2. Crear un nuevo DTO con las propiedades actualizadas
        VariedadDto updatedDto = VariedadDto.builder()
                .id(dto.getId())
                .code(command.getCode())
                .name(command.getName())
                .build();

        // 3. Llamar al servicio para persistir la actualización
        serviceImpl.update(updatedDto);
    }
}