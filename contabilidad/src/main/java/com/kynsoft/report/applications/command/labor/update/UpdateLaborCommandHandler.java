package com.kynsoft.report.applications.command.labor.update;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.LaborDto;
import com.kynsoft.report.domain.services.ILaborService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateLaborCommandHandler implements ICommandHandler<UpdateLaborCommand> {

    private final ILaborService serviceImpl;

    @Override
    public void handle(UpdateLaborCommand command) {
        // 1. Buscar la entidad existente por ID
        LaborDto dto = serviceImpl.findById(command.getId());

        // 2. Crear un nuevo DTO con las propiedades actualizadas
        LaborDto updatedDto = LaborDto.builder()
                .id(dto.getId())
                .code(command.getCode())
                .name(command.getName())
                .activo(command.getActivo())
                .tipoCultivoId(command.getTipoCultivoId())
                .codigoCentroCosto(command.getCodigoCentroCosto())
                .unidadMedida(command.getUnidadMedida())
                .normaRendimiento(command.getNormaRendimiento())
                .indiceConsumo(command.getIndiceConsumo())
                .nivelActividad(command.getNivelActividad())
                .dotacionEstandar(command.getDotacionEstandar())
                .diasEstimados(command.getDiasEstimados())
                .horasEstimadas(command.getHorasEstimadas())
                .cargosRequeridos(command.getCargosRequeridos())
                .build();

        // 3. Llamar al servicio para persistir la actualización
        serviceImpl.update(updatedDto);
    }
}
