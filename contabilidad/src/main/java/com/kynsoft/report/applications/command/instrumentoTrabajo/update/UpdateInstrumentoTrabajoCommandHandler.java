package com.kynsoft.report.applications.command.instrumentoTrabajo.update;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.InstrumentoTrabajoDto;
import com.kynsoft.report.domain.services.IInstrumentoTrabajoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateInstrumentoTrabajoCommandHandler implements ICommandHandler<UpdateInstrumentoTrabajoCommand> {

    private final IInstrumentoTrabajoService serviceImpl;

    @Override
    public void handle(UpdateInstrumentoTrabajoCommand command) {
        // 1. Buscar la entidad existente por ID
        InstrumentoTrabajoDto dto = serviceImpl.findById(command.getId());

        // 2. Crear un nuevo DTO con las propiedades actualizadas
        InstrumentoTrabajoDto updatedDto = InstrumentoTrabajoDto.builder()
                .id(dto.getId())
                .code(command.getCode())
                .name(command.getName())
                .build();

        // 3. Llamar al servicio para persistir la actualización
        serviceImpl.update(updatedDto);
    }
}