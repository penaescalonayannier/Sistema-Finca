package com.kynsoft.report.applications.command.almacenproducto.produccionterminada;

import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.dto.CreateProduccionTerminadaResult;
import com.kynsoft.report.domain.dto.ProduccionTerminadaDto;
import com.kynsoft.report.domain.services.IAlmacenFincaProductoService;
import com.kynsoft.report.domain.services.IProduccionTerminadaService;
import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EntradaProduccionTerminadaAlmacenCommandHandler
        implements ICommandHandler<EntradaProduccionTerminadaAlmacenCommand> {

    private final IProduccionTerminadaService produccionTerminadaService;
    private final IAlmacenFincaProductoService almacenFincaProductoService;

    @Override
    public void handle(EntradaProduccionTerminadaAlmacenCommand command) {
        CreateProduccionTerminadaResult result = produccionTerminadaService.createEnAlmacen(
                command.getAlmacenId(),
                command.getAlmacenFincaProductoId(),
                ProduccionTerminadaDto.builder()
                        .cantidadTerminada(command.getCantidadTerminada())
                        .trabajadorEntregaId(command.getTrabajadorEntregaId())
                        .trabajadorRecibeId(command.getTrabajadorRecibeId())
                        .observaciones(command.getObservaciones())
                        .build());

        AlmacenFincaProductoDto almacenProducto = almacenFincaProductoService
                .findById(command.getAlmacenFincaProductoId());
        command.setProduccionTerminadaId(result.getId());
        command.setStockNuevo(almacenProducto.getStock());
    }
}
