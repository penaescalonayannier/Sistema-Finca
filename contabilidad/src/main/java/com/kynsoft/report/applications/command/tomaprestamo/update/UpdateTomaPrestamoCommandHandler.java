package com.kynsoft.report.applications.command.tomaprestamo.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.TomaPrestamoDto;
import com.kynsoft.report.domain.services.ITomaPrestamoService;
import org.springframework.stereotype.Component;

@Component
public class UpdateTomaPrestamoCommandHandler implements ICommandHandler<UpdateTomaPrestamoCommand> {

    private final ITomaPrestamoService serviceImpl;

    public UpdateTomaPrestamoCommandHandler(ITomaPrestamoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(UpdateTomaPrestamoCommand command) {
        // 1. Buscar la entidad existente por ID
        TomaPrestamoDto dto = serviceImpl.findById(command.getId());

        // 2. Crear un nuevo DTO con las propiedades actualizadas
        // Se construye un nuevo DTO con la data actual, sobrescribiendo los campos
        // que vienen en el Command.
        TomaPrestamoDto updatedDto = TomaPrestamoDto.builder()
                .id(dto.getId())
                .importe(command.getImporte())
                .fecha(command.getFecha())
                .cuentaDestino(command.getCuentaDestino())
                .tipo(command.getTipo())
                .observaciones(command.getObservaciones())
                .build();

        // 3. Llamar al servicio para persistir la actualización
        serviceImpl.update(updatedDto);
    }
}