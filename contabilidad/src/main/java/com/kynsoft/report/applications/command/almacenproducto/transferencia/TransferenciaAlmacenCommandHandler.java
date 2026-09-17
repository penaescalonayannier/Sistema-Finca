package com.kynsoft.report.applications.command.almacenproducto.transferencia;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.services.IAlmacenFincaProductoService;
import com.kynsoft.report.infrastructure.services.TransferenciaAlmacenControlService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransferenciaAlmacenCommandHandler implements ICommandHandler<TransferenciaAlmacenCommand> {

    private final IAlmacenFincaProductoService service;
    private final TransferenciaAlmacenControlService transferenciaService;

    @Override
    public void handle(TransferenciaAlmacenCommand command) {
        command.setTransferenciaId(transferenciaService.despachar(command.getAlmacenFincaProductoId(),
                command.getDestinoAlmacenId(),
                command.getCantidad(),
                command.getObservaciones()));

        AlmacenFincaProductoDto origenActualizado = service.findById(command.getAlmacenFincaProductoId());
        command.setStockOrigenNuevo(origenActualizado.getStock());

        // El destino permanece sin cambios hasta que su responsable confirme
        // físicamente la recepción del SC-2-09.
        command.setStockDestinoNuevo(null);
    }
}
