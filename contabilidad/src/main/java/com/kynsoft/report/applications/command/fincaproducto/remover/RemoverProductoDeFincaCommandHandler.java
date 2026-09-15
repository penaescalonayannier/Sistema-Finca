package com.kynsoft.report.applications.command.fincaproducto.remover;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IFincaProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RemoverProductoDeFincaCommandHandler 
    implements ICommandHandler<RemoverProductoDeFincaCommand> {

    private final IFincaProductoService service;

    @Override
    public void handle(RemoverProductoDeFincaCommand command) {
        service.removerProductoDeFinca(
            command.getFincaId(),
            command.getProductoId()
        );
    }
}