package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.ProductoDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "unidad_medida", nullable = false, length = 100)
    private String unidadMedida;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;//Otros

    @Column(name = "price_trabajador", nullable = false)
    private Double priceTrabajador;//Trabajador

    @Column(name = "price_comedor", nullable = false)
    private Double priceComedor;//Comedor

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "active")
    private Boolean active;

    public Producto(ProductoDto dto) {
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

    public ProductoDto toAggregate() {
        return ProductoDto
                .builder()
                .id(id)
                .code(code)
                .name(name)
                .description(description)
                .price(price)
                .priceTrabajador(priceTrabajador)
                .priceComedor(priceComedor)
                .stock(stock)
                .active(active)
                .unidadMedida(unidadMedida)
                .build();
    }
}
