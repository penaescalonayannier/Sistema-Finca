package com.kynsoft.report.applications.command.almacen.update;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.AlmacenDto;
import com.kynsoft.report.domain.services.IAlmacenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateAlmacenCommandHandler implements ICommandHandler<UpdateAlmacenCommand> {

    private final IAlmacenService serviceImpl;

    @Override
    public void handle(UpdateAlmacenCommand command) {
        serviceImpl.update(AlmacenDto.builder()
                .id(command.getId())
                .nombre(command.getNombre())
                .inventario(command.getInventario())
                .fincaId(command.getFincaId())
                .build());
    }
}
