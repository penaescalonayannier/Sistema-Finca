package com.kynsoft.report.applications.command.almacenproducto.transferencia;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.services.IAlmacenFincaProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransferenciaAlmacenCommandHandler implements ICommandHandler<TransferenciaAlmacenCommand> {

    private final IAlmacenFincaProductoService service;

    @Override
    public void handle(TransferenciaAlmacenCommand command) {
        AlmacenFincaProductoDto origen = service.findById(command.getAlmacenFincaProductoId());

        service.transferir(command.getAlmacenFincaProductoId(),
                command.getDestinoAlmacenId(),
                command.getCantidad(),
                command.getObservaciones());

        AlmacenFincaProductoDto origenActualizado = service.findById(command.getAlmacenFincaProductoId());
        command.setStockOrigenNuevo(origenActualizado.getStock());

        AlmacenFincaProductoDto destino = service.findByAlmacenIdAndFincaProductoId(
                command.getDestinoAlmacenId(), origen.getFincaProductoId());
        command.setStockDestinoNuevo(destino.getStock());
    }
}
