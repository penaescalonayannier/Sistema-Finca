package com.kynsoft.report.applications.command.finca.update;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.report.domain.dto.FincaDto;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.IFincaService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateFincaCommandHandler implements ICommandHandler<UpdateFincaCommand> {

    private final IFincaService serviceImpl;
    private final ITrabajadorService trabajadorService;

    @Override
    public void handle(UpdateFincaCommand command) {
        // 1. Buscar la entidad existente por ID
        FincaDto dto = serviceImpl.findById(command.getId());

        // 2. Validar área > 0 si se proporciona
        Double area = command.getArea() != null ? command.getArea() : dto.getArea();
        if (area != null && area <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("area", "El área debe ser mayor a 0.")));
        }

        // 3. Validar responsable si se proporciona
        if (command.getResponsableId() != null) {
            TrabajadorDto trabajador = trabajadorService.findById(command.getResponsableId());
            // Validar que el responsable pertenezca a la misma finca (RN-05)
            if (trabajador.getFincaId() != null && !trabajador.getFincaId().equals(command.getId())) {
                throw new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("responsableId", "El responsable debe pertenecer a la finca.")));
            }
        }

        // 4. Validar que si hay trabajadores asignados, el responsable es obligatorio (RN-04)
        Long cantidadTrabajadores = serviceImpl.countTrabajadoresByFincaId(command.getId());
        if (cantidadTrabajadores > 0 && command.getResponsableId() == null && dto.getResponsableId() == null) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("responsableId", "Responsable requerido: la finca tiene trabajadores asignados.")));
        }

        // 5. Crear un nuevo DTO con las propiedades actualizadas (código NO se puede modificar)
        FincaDto updatedDto = FincaDto.builder()
                .id(dto.getId())
                .code(dto.getCode()) // El código NO se puede modificar
                .name(command.getName() != null ? command.getName() : dto.getName())
                .description(command.getDescription() != null ? command.getDescription() : dto.getDescription())
                .direccion(command.getDireccion() != null ? command.getDireccion() : dto.getDireccion())
                .telefono(command.getTelefono() != null ? command.getTelefono() : dto.getTelefono())
                .responsableId(command.getResponsableId() != null ? command.getResponsableId() : dto.getResponsableId())
                .area(area)
                .activo(dto.getActivo())
                .build();

        // 6. Llamar al servicio para persistir la actualización
        serviceImpl.update(updatedDto);
    }
}