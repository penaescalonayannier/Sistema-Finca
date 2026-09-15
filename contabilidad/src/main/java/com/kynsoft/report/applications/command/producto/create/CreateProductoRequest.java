package com.kynsoft.report.applications.command.producto.create;

import com.kynsoft.report.domain.dto.TipoProducto;
import com.kynsoft.report.domain.dto.UnidadMedida;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductoRequest {
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
}