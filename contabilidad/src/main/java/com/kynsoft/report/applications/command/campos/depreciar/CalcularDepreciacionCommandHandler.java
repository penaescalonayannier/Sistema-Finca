package com.kynsoft.report.applications.command.campos.depreciar;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.ICamposService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CalcularDepreciacionCommandHandler implements ICommandHandler<CalcularDepreciacionCommand> {

    private final ICamposService camposService;

    @Override
    public void handle(CalcularDepreciacionCommand command) {
        camposService.calcularDepreciacion(command.getCampoIds(), command.getMeses());
    }
}
