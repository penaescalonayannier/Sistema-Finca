package com.kynsoft.report.applications.command.cepa.update;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.CepaDto;
import com.kynsoft.report.domain.services.ICepaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateCepaCommandHandler implements ICommandHandler<UpdateCepaCommand> {

    private final ICepaService serviceImpl;

    @Override
    public void handle(UpdateCepaCommand command) {
        // 1. Buscar la entidad existente por ID
        CepaDto dto = serviceImpl.findById(command.getId());

        // 2. Crear un nuevo DTO con las propiedades actualizadas
        CepaDto updatedDto = CepaDto.builder()
                .id(dto.getId())
                .code(command.getCode())
                .name(command.getName())
                .build();

        // 3. Llamar al servicio para persistir la actualización
        serviceImpl.update(updatedDto);
    }
}