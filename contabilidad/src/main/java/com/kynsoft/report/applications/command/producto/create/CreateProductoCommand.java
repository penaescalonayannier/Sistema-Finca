package com.kynsoft.report.applications.command.producto.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateProductoCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private Double price;
    private Double priceTrabajador;//Trabajador
    private Double priceComedor;//Comedor
    private Integer stock;
    private Boolean active;
    private String unidadMedida;

    public static CreateProductoCommand fromRequest(CreateProductoRequest request) {
        return new CreateProductoCommand(
                UUID.randomUUID(),
                request.getCode(),
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getPriceTrabajador(),
                request.getPriceComedor(),
                request.getStock(),
                request.getActive() != null ? request.getActive() : true, // default active = true
                request.getUnidadMedida()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateProductoMessage(id);
    }
}