package com.kynsoft.report.applications.command.producto.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.ProductoDto;
import com.kynsoft.report.domain.services.IProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateProductoCommandHandler implements ICommandHandler<CreateProductoCommand> {

    private final IProductoService serviceImpl;

    @Override
    public void handle(CreateProductoCommand command) {
        serviceImpl.create(ProductoDto.builder()
                .id(command.getId())
                .code(command.getCode())
                .name(command.getName())
                .description(command.getDescription())
                .price(command.getPrice())
                .price(command.getPriceTrabajador())
                .price(command.getPriceComedor())
                .stock(command.getStock())
                .active(command.getActive())
                .unidadMedida(command.getUnidadMedida())
                .build());
    }
}