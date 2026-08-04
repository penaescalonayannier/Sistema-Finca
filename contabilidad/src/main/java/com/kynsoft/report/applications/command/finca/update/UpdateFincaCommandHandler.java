package com.kynsoft.report.applications.command.finca.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.FincaDto;
import com.kynsoft.report.domain.services.IFincaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateFincaCommandHandler implements ICommandHandler<UpdateFincaCommand> {

    private final IFincaService serviceImpl;

    @Override
    public void handle(UpdateFincaCommand command) {
        // 1. Buscar la entidad existente por ID
        FincaDto dto = serviceImpl.findById(command.getId());

        // 2. Crear un nuevo DTO con las propiedades actualizadas
        FincaDto updatedDto = FincaDto.builder()
                .id(dto.getId())
                .code(command.getCode() != null ? command.getCode() : dto.getCode())
                .name(command.getName() != null ? command.getName() : dto.getName())
                .description(command.getDescription() != null ? command.getDescription() : dto.getDescription())
                .build();

        // 3. Llamar al servicio para persistir la actualización
        serviceImpl.update(updatedDto);
    }
}