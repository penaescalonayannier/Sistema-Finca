package com.kynsoft.report.applications.command.unidadmedida.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.UnidadMedidaDto;
import com.kynsoft.report.domain.services.IUnidadMedidaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateUnidadMedidaCommandHandler implements ICommandHandler<UpdateUnidadMedidaCommand> {

    private final IUnidadMedidaService serviceImpl;

    @Override
    public void handle(UpdateUnidadMedidaCommand command) {
        // 1. Buscar la entidad existente por ID
        UnidadMedidaDto dto = serviceImpl.findById(command.getId());

        // 2. Crear un nuevo DTO con las propiedades actualizadas
        UnidadMedidaDto updatedDto = UnidadMedidaDto.builder()
                .id(dto.getId())
                .code(command.getCode())
                .name(command.getName())
                .build();

        // 3. Llamar al servicio para persistir la actualización
        serviceImpl.update(updatedDto);
    }
}