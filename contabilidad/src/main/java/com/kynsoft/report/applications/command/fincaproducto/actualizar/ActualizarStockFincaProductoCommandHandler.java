package com.kynsoft.report.applications.command.fincaproducto.actualizar;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IFincaProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ActualizarStockFincaProductoCommandHandler 
    implements ICommandHandler<ActualizarStockFincaProductoCommand> {

    private final IFincaProductoService service;

    @Override
    public void handle(ActualizarStockFincaProductoCommand command) {
        service.actualizarStock(
            command.getFincaId(),
            command.getProductoId(),
            command.getStock()
        );
    }
}