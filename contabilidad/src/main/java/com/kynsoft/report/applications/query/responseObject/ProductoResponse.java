package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.ProductoDto;
import com.kynsoft.report.domain.dto.TipoProducto;
import com.kynsoft.report.domain.dto.UnidadMedida;
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
    private Double priceTrabajador;
    private Double priceComedor;
    private Integer stock;
    private Boolean active;
    private UnidadMedida unidadMedida;
    private TipoProducto tipoProducto;

    public ProductoResponse(ProductoDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.price = dto.getPrice();
        this.priceTrabajador = dto.getPriceTrabajador();
        this.priceComedor = dto.getPriceComedor();
        this.stock = dto.getStock();
        this.active = dto.getActive();
        this.unidadMedida = dto.getUnidadMedida();
        this.tipoProducto = dto.getTipoProducto();
    }
}