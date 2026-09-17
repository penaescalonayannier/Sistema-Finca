package com.kynsoft.report.applications.command.trabajador.update;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.ICargoService;
import com.kynsoft.report.domain.services.IFincaService;
import com.kynsoft.report.domain.services.IGrupoService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateTrabajadorCommandHandler implements ICommandHandler<UpdateTrabajadorCommand> {

    private final ITrabajadorService serviceImpl;
    private final IFincaService fincaService;
    private final IGrupoService grupoService;
    private final ICargoService cargoService;

    @Override
    public void handle(UpdateTrabajadorCommand command) {
        // 1. Buscar la entidad existente por ID
        TrabajadorDto dto = serviceImpl.findById(command.getId());

        // 2. Validar relaciones si se proporcionan
        if (command.getFincaId() != null) {
            fincaService.findById(command.getFincaId());
        }
        if (command.getGrupoId() != null) {
            grupoService.findById(command.getGrupoId());
        }
        if (command.getCargoId() != null) {
            cargoService.findById(command.getCargoId());
        }

        // 3. Crear un nuevo DTO con las propiedades actualizadas
        // RUC NO se puede modificar (RN-09) - se mantiene el original
        TrabajadorDto updatedDto = TrabajadorDto.builder()
                .id(dto.getId())
                .ruc(dto.getRuc()) // RUC no se puede modificar
                .nombre(command.getNombre() != null ? command.getNombre() : dto.getNombre())
                .cuenta(command.getCuenta() != null ? command.getCuenta() : dto.getCuenta())
                .fincaId(command.getFincaId() != null ? command.getFincaId() : dto.getFincaId())
                .grupoId(command.getGrupoId() != null ? command.getGrupoId() : dto.getGrupoId())
                .cargoId(command.getCargoId() != null ? command.getCargoId() : dto.getCargoId())
                .plazaId(command.getPlazaId() != null ? command.getPlazaId() : dto.getPlazaId())
                .activo(command.getActivo() != null ? command.getActivo() : dto.getActivo())
                .build();

        // 4. Llamar al servicio para persistir la actualización
        serviceImpl.update(updatedDto);
    }
}
