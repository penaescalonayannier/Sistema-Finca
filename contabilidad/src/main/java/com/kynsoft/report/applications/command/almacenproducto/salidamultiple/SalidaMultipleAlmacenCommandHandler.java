package com.kynsoft.report.applications.command.almacenproducto.salidamultiple;

import com.kynsoft.report.domain.services.ISalidaService;
import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SalidaMultipleAlmacenCommandHandler implements ICommandHandler<SalidaMultipleAlmacenCommand> {

    private final ISalidaService salidaService;

    @Override
    public void handle(SalidaMultipleAlmacenCommand command) {
        command.setSalidaIds(salidaService.createMultipleFromAlmacen(
                command.getAlmacenId(), command.getDestino(), command.getObservaciones(), command.getLineas()));
    }
}
