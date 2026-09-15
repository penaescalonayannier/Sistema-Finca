package com.kynsoft.report.applications.command.producto.create;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.domain.dto.TipoProducto;
import com.kynsoft.report.domain.dto.UnidadMedida;
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
    private Double priceTrabajador;
    private Double priceComedor;
    private Integer stock;
    private Boolean active;
    private UnidadMedida unidadMedida;
    private TipoProducto tipoProducto;

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
                request.getActive() != null ? request.getActive() : true,
                request.getUnidadMedida() != null ? request.getUnidadMedida() : UnidadMedida.UND,
                request.getTipoProducto() != null ? request.getTipoProducto() : TipoProducto.OTROS
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateProductoMessage(id);
    }
}