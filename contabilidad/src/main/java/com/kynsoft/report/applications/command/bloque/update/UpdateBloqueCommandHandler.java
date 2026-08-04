package com.kynsoft.report.applications.command.bloque.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.BloqueDto;
import com.kynsoft.report.domain.services.IBloqueService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateBloqueCommandHandler implements ICommandHandler<UpdateBloqueCommand> {

    private final IBloqueService serviceImpl;

    @Override
    public void handle(UpdateBloqueCommand command) {
        // 1. Buscar la entidad existente por ID
        BloqueDto dto = serviceImpl.findById(command.getId());

        // 2. Crear un nuevo DTO con las propiedades actualizadas
        BloqueDto updatedDto = BloqueDto.builder()
                .id(dto.getId())
                .code(command.getCode())
                .name(command.getName())
                .build();

        // 3. Llamar al servicio para persistir la actualización
        serviceImpl.update(updatedDto);
    }
}