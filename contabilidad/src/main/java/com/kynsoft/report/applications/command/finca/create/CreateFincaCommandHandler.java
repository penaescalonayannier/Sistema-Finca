package com.kynsoft.report.applications.command.finca.create;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.report.domain.dto.FincaDto;
import com.kynsoft.report.domain.services.IFincaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateFincaCommandHandler implements ICommandHandler<CreateFincaCommand> {

    private final IFincaService serviceImpl;

    @Override
    public void handle(CreateFincaCommand command) {
        // Validar formato de código (5-11 dígitos numéricos)
        validateCode(command.getCode());

        // Validar área > 0
        validateArea(command.getArea());

        serviceImpl.create(FincaDto.builder()
                .id(command.getId())
                .code(command.getCode())
                .name(command.getName())
                .description(command.getDescription())
                .direccion(command.getDireccion())
                .telefono(command.getTelefono())
                .area(command.getArea())
                .build());
    }

    private void validateCode(String code) {
        if (code == null || code.isEmpty()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("code", "El código es obligatorio.")));
        }
        if (!code.matches("^\\d{5,11}$")) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("code", "El código debe tener entre 5 y 11 dígitos numéricos.")));
        }
    }

    private void validateArea(Double area) {
        if (area == null || area <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("area", "El área debe ser mayor a 0.")));
        }
    }
}