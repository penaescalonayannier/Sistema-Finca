package com.kynsoft.report.applications.command.fincaproducto.entradaconduce;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import com.kynsoft.report.domain.services.IFincaProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EntradaConduceCommandHandler implements ICommandHandler<EntradaConduceCommand> {

    private final IFincaProductoService service;

    @Override
    public void handle(EntradaConduceCommand command) {
        // Get current stock before operation
        FincaProductoDto dto = service.getById(command.getId());
        Double stockAnterior = dto.getStock();

        // Perform the entry
        service.entradaConduce(
                command.getId(),
                command.getCantidad(),
                command.getObservaciones()
        );

        // Set response values
        command.setStockAnterior(stockAnterior);
        command.setStockNuevo(stockAnterior + command.getCantidad());
    }
}
