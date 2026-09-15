package com.kynsoft.report.applications.command.fincaproducto.ajuste;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import com.kynsoft.report.domain.services.IFincaProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AjusteStockCommandHandler implements ICommandHandler<AjusteStockCommand> {

    private final IFincaProductoService service;

    @Override
    public void handle(AjusteStockCommand command) {
        // Get current stock before operation
        FincaProductoDto dto = service.getById(command.getId());
        Integer stockAnterior = dto.getStock();

        // Perform the adjustment
        service.ajusteManual(
                command.getId(),
                command.getCantidad(),
                command.getObservaciones()
        );

        // Set response values
        command.setStockAnterior(stockAnterior);
        command.setStockNuevo(stockAnterior + command.getCantidad());
    }
}
