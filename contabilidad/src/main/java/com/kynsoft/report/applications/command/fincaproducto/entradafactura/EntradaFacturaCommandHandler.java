package com.kynsoft.report.applications.command.fincaproducto.entradafactura;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import com.kynsoft.report.domain.services.IFincaProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EntradaFacturaCommandHandler implements ICommandHandler<EntradaFacturaCommand> {

    private final IFincaProductoService service;

    @Override
    public void handle(EntradaFacturaCommand command) {
        // Get current stock before operation
        FincaProductoDto dto = service.getById(command.getId());
        Double stockAnterior = dto.getStock();

        // Perform the entry
        service.entradaFactura(
                command.getId(),
                command.getCantidad(),
                command.getNumeroFactura(),
                command.getObservaciones()
        );

        // Set response values
        command.setStockAnterior(stockAnterior);
        command.setStockNuevo(stockAnterior + command.getCantidad());
    }
}
