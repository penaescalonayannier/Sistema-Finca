package com.kynsoft.report.applications.command.almacen.create;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.AlmacenDto;
import com.kynsoft.report.domain.services.IAlmacenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class CreateAlmacenCommandHandler implements ICommandHandler<CreateAlmacenCommand> {

    private final IAlmacenService serviceImpl;

    @Override
    public void handle(CreateAlmacenCommand command) {
        UUID id = serviceImpl.create(AlmacenDto.builder()
                .nombre(command.getNombre())
                .descripcion(command.getDescripcion())
                .fincaId(command.getFincaId())
                .build());
        command.setId(id);

        // Get the generated inventario code
        AlmacenDto created = serviceImpl.findById(id);
        command.setInventario(created.getInventario());
    }
}
