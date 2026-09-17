package com.kynsoft.report.applications.command.trabajador.create;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.ICargoService;
import com.kynsoft.report.domain.services.IFincaService;
import com.kynsoft.report.domain.services.IGrupoService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateTrabajadorCommandHandler implements ICommandHandler<CreateTrabajadorCommand> {

    private final ITrabajadorService serviceImpl;
    private final IFincaService fincaService;
    private final IGrupoService grupoService;
    private final ICargoService cargoService;

    @Override
    public void handle(CreateTrabajadorCommand command) {
        // Validar formato de RUC (11 dígitos numéricos)
        validateRuc(command.getRuc());

        // Validar que la finca exista
        if (command.getFincaId() == null) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("fincaId", "La finca es obligatoria.")));
        }
        fincaService.findById(command.getFincaId());

        // Validar que el grupo exista
        if (command.getGrupoId() == null) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("grupoId", "El grupo es obligatorio.")));
        }
        grupoService.findById(command.getGrupoId());

        // Validar que el cargo exista
        if (command.getCargoId() == null) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cargoId", "El cargo es obligatorio.")));
        }
        cargoService.findById(command.getCargoId());

        serviceImpl.create(TrabajadorDto
                .builder()
                .id(command.getId())
                .ruc(command.getRuc())
                .nombre(command.getNombre())
                .cuenta(command.getCuenta())
                .fincaId(command.getFincaId())
                .grupoId(command.getGrupoId())
                .cargoId(command.getCargoId())
                .plazaId(command.getPlazaId())
                .activo(true)
                .build());
    }

    private void validateRuc(String ruc) {
        if (ruc == null || ruc.isEmpty()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("ruc", "El RUC es obligatorio.")));
        }
        if (!ruc.matches("^\\d{11}$")) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("ruc", "El RUC debe tener exactamente 11 dígitos numéricos.")));
        }
    }
}
