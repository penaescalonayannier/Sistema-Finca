package com.kynsoft.report.applications.command.fincaproducto.asignar;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IFincaProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AsignarProductoAFincaCommandHandler 
    implements ICommandHandler<AsignarProductoAFincaCommand> {

    private final IFincaProductoService service;

    @Override
    public void handle(AsignarProductoAFincaCommand command) {
        service.asignarProductoAFinca(
            command.getFincaId(),
            command.getProductoId(),
            command.getStock()
        );
    }
}