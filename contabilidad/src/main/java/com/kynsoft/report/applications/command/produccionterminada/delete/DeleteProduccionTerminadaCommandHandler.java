package com.kynsoft.report.applications.command.produccionterminada.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.DeleteProduccionTerminadaResult;
import com.kynsoft.report.domain.services.IProduccionTerminadaService;
import org.springframework.stereotype.Component;

@Component
public class DeleteProduccionTerminadaCommandHandler implements ICommandHandler<DeleteProduccionTerminadaCommand> {

    private final IProduccionTerminadaService service;

    public DeleteProduccionTerminadaCommandHandler(IProduccionTerminadaService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteProduccionTerminadaCommand command) {
        // El servicio maneja todas las validaciones y reversión de stock
        DeleteProduccionTerminadaResult result = service.delete(command.getId());

        // Guardar resultados en el command para el mensaje
        command.setStockAnterior(result.getStockAnterior());
        command.setStockNuevo(result.getStockNuevo());
        command.setCantidadRevertida(result.getCantidadRevertida());
    }
}
