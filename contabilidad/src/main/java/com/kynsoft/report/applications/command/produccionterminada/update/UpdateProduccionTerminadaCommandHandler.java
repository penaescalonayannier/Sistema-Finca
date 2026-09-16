package com.kynsoft.report.applications.command.produccionterminada.update;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.ProduccionTerminadaDto;
import com.kynsoft.report.domain.dto.UpdateProduccionTerminadaResult;
import com.kynsoft.report.domain.services.IProduccionTerminadaService;
import org.springframework.stereotype.Component;

@Component
public class UpdateProduccionTerminadaCommandHandler implements ICommandHandler<UpdateProduccionTerminadaCommand> {

    private final IProduccionTerminadaService service;

    public UpdateProduccionTerminadaCommandHandler(IProduccionTerminadaService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateProduccionTerminadaCommand command) {
        ProduccionTerminadaDto dto = ProduccionTerminadaDto.builder()
                .id(command.getId())
                .cantidadTerminada(command.getCantidadTerminada())
                .trabajadorEntregaId(command.getTrabajadorEntregaId())
                .trabajadorRecibeId(command.getTrabajadorRecibeId())
                .observaciones(command.getObservaciones())
                .costoUnitario(command.getCostoUnitario())
                .lote(command.getLote())
                .centroCosto(command.getCentroCosto())
                .build();

        // El servicio maneja todas las validaciones y ajuste de stock
        UpdateProduccionTerminadaResult result = service.update(dto);

        // Guardar resultados en el command para el mensaje
        command.setStockAnterior(result.getStockAnterior());
        command.setStockNuevo(result.getStockNuevo());
        command.setAjuste(result.getAjuste());
    }
}
