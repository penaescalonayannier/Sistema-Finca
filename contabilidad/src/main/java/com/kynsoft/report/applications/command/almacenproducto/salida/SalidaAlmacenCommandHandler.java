package com.kynsoft.report.applications.command.almacenproducto.salida;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.services.IAlmacenFincaProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SalidaAlmacenCommandHandler implements ICommandHandler<SalidaAlmacenCommand> {

    private final IAlmacenFincaProductoService service;

    @Override
    public void handle(SalidaAlmacenCommand command) {
        String destino = command.getDestino();

        if ("TRABAJADOR".equalsIgnoreCase(destino) && command.getTrabajadorId() != null) {
            service.salidaTrabajador(command.getAlmacenFincaProductoId(),
                    command.getCantidad(), command.getTrabajadorId(), command.getDescripcion());
        } else if ("COMEDOR".equalsIgnoreCase(destino)) {
            service.salidaComedor(command.getAlmacenFincaProductoId(),
                    command.getCantidad(), command.getDescripcion());
        } else {
            service.salida(command.getAlmacenFincaProductoId(),
                    command.getCantidad(), command.getDescripcion());
        }

        AlmacenFincaProductoDto updated = service.findById(command.getAlmacenFincaProductoId());
        command.setStockNuevo(updated.getStock());
    }
}
