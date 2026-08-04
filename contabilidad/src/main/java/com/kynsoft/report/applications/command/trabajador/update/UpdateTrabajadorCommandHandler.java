package com.kynsoft.report.applications.command.trabajador.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.ITrabajadorService;
import org.springframework.stereotype.Component;

@Component
public class UpdateTrabajadorCommandHandler implements ICommandHandler<UpdateTrabajadorCommand> {

    private final ITrabajadorService serviceImpl;

    public UpdateTrabajadorCommandHandler(ITrabajadorService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(UpdateTrabajadorCommand command) {

        // 1. Buscar la entidad existente por ID
        TrabajadorDto dto = serviceImpl.findById(command.getId());

        // 2. Crear un nuevo DTO con las propiedades actualizadas
        // Se construye un nuevo DTO con la data actual, sobrescribiendo los campos
        // que vienen en el Command. Se preserva cargoId si no se actualiza.
        TrabajadorDto updatedDto = TrabajadorDto.builder()
                .id(dto.getId())
                .ruc(command.getRuc())
                .nombre(command.getNombre())
                .cuenta(command.getCuenta())
                .cargoId(dto.getCargoId())
                .cargoName(dto.getCargoName())
                .activo(command.getActivo() != null ? command.getActivo() : dto.getActivo())
                .build();

        // 3. Llamar al servicio para persistir la actualización
        serviceImpl.update(updatedDto);
    }
}
