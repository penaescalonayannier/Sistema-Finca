package com.kynsoft.report.applications.command.almacenproducto.entrada;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.services.IAlmacenFincaProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EntradaAlmacenCommandHandler implements ICommandHandler<EntradaAlmacenCommand> {

    private final IAlmacenFincaProductoService service;

    @Override
    public void handle(EntradaAlmacenCommand command) {
        TipoMovimientoStock tipo = command.getTipo();
        if (tipo == null) {
            tipo = TipoMovimientoStock.ENTRADA_PRODUCCION;
        }

        switch (tipo) {
            case ENTRADA_PRODUCCION:
                service.entradaProduccion(command.getAlmacenFincaProductoId(),
                        command.getCantidad(), command.getDescripcion(), command.getCentroCosto());
                break;
            case ENTRADA_FACTURA:
                service.entradaFactura(command.getAlmacenFincaProductoId(),
                        command.getCantidad(), command.getNumeroFactura(), command.getDescripcion());
                break;
            case ENTRADA_CONDUCE:
                service.entradaConduce(command.getAlmacenFincaProductoId(),
                        command.getCantidad(), command.getDescripcion());
                break;
            default:
                service.entrada(command.getAlmacenFincaProductoId(),
                        command.getCantidad(), tipo, command.getDescripcion(), command.getCentroCosto());
        }

        AlmacenFincaProductoDto updated = service.findById(command.getAlmacenFincaProductoId());
        command.setStockNuevo(updated.getStock());
    }
}
