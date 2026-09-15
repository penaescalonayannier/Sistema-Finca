package com.kynsoft.report.applications.command.salida.update;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.SalidaDto;
import com.kynsoft.report.domain.services.ISalidaService;
import org.springframework.stereotype.Component;

@Component
public class UpdateSalidaCommandHandler implements ICommandHandler<UpdateSalidaCommand> {

    private final ISalidaService service;

    public UpdateSalidaCommandHandler(ISalidaService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateSalidaCommand command) {
        SalidaDto dto = SalidaDto.builder()
                .id(command.getId())
                .tipo(command.getTipo())
                .fincaProductoId(command.getFincaProductoId())
                .observaciones(command.getObservaciones())
                .build();

        service.update(dto, command.getItems());
    }
}
