package com.kynsoft.report.applications.command.produccionterminada.create;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.CreateProduccionTerminadaResult;
import com.kynsoft.report.domain.dto.ProduccionTerminadaDto;
import com.kynsoft.report.domain.services.IProduccionTerminadaService;
import org.springframework.stereotype.Component;

@Component
public class CreateProduccionTerminadaCommandHandler implements ICommandHandler<CreateProduccionTerminadaCommand> {

    private final IProduccionTerminadaService service;

    public CreateProduccionTerminadaCommandHandler(IProduccionTerminadaService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateProduccionTerminadaCommand command) {
        ProduccionTerminadaDto dto = ProduccionTerminadaDto.builder()
                .id(command.getId())
                .fincaId(command.getFincaId())
                .productoId(command.getProductoId())
                .fecha(command.getFecha())
                .cantidadTerminada(command.getCantidadTerminada())
                .trabajadorEntregaId(command.getTrabajadorEntregaId())
                .trabajadorRecibeId(command.getTrabajadorRecibeId())
                .observaciones(command.getObservaciones())
                .build();

        // El servicio maneja todas las validaciones y actualización de stock
        CreateProduccionTerminadaResult result = service.create(dto);

        // Guardar resultados en el command para el mensaje
        command.setId(result.getId());
        command.setStockAnterior(result.getStockAnterior());
        command.setStockNuevo(result.getStockNuevo());
    }
}
