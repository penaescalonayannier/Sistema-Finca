package com.kynsoft.report.applications.query.responseObject;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.ProductoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse implements IResponse {
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

    public ProductoResponse(ProductoDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.price = dto.getPrice();
        this.price = dto.getPriceTrabajador();
        this.price = dto.getPriceComedor();
        this.stock = dto.getStock();
        this.active = dto.getActive();
        this.unidadMedida = dto.getUnidadMedida();
    }
}