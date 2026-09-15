package com.kynsoft.report.applications.command.salida.create;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.SalidaDto;
import com.kynsoft.report.domain.services.ISalidaService;
import org.springframework.stereotype.Component;

@Component
public class CreateSalidaCommandHandler implements ICommandHandler<CreateSalidaCommand> {

    private final ISalidaService service;

    public CreateSalidaCommandHandler(ISalidaService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateSalidaCommand command) {
        // RN-09: tipo se determina automáticamente según destino en el servicio
        SalidaDto dto = SalidaDto.builder()
                .id(command.getId())
                .destino(command.getDestino())
                .fincaProductoId(command.getFincaProductoId())
                .almacenFincaProductoId(command.getAlmacenFincaProductoId())
                .observaciones(command.getObservaciones())
                .build();

        service.create(dto, command.getItems());
    }
}
