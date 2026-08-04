package com.kynsoft.report.applications.command.producto.update;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductoRequest {
    private String code;
    private String name;
    private String description;
    private Double price;
    private Double priceTrabajador;//Trabajador
    private Double priceComedor;//Comedor
    private Integer stock;
    private Boolean active;
    private String unidadMedida;
}