package com.kynsoft.report.applications.command.producto.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.ProductoDto;
import com.kynsoft.report.domain.services.IProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateProductoCommandHandler implements ICommandHandler<UpdateProductoCommand> {

    private final IProductoService serviceImpl;

    @Override
    public void handle(UpdateProductoCommand command) {
        // 1. Buscar la entidad existente por ID
        ProductoDto dto = serviceImpl.findById(command.getId());

        // 2. Crear un nuevo DTO con las propiedades actualizadas
        // Se mantienen los valores existentes si los nuevos son null
        ProductoDto updatedDto = ProductoDto.builder()
                .id(dto.getId())
                .code(command.getCode() != null ? command.getCode() : dto.getCode())
                .name(command.getName() != null ? command.getName() : dto.getName())
                .description(command.getDescription() != null ? command.getDescription() : dto.getDescription())
                .price(command.getPrice() != null ? command.getPrice() : dto.getPrice())
                .stock(command.getStock() != null ? command.getStock() : dto.getStock())
                .active(command.getActive() != null ? command.getActive() : dto.getActive())
                .unidadMedida(command.getUnidadMedida() != null ? command.getUnidadMedida() : dto.getUnidadMedida())
                .build();

        // 3. Llamar al servicio para persistir la actualización
        serviceImpl.update(updatedDto);
    }
}