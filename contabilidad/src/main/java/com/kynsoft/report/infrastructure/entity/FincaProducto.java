package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.FincaProductoDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "finca_producto")
public class FincaProducto {
    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finca_id", nullable = false)
    private Finca finca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    public FincaProducto(FincaProductoDto dto) {
        this.id = dto.getId();
        this.stock = dto.getStock();
    }

    public FincaProductoDto toAggregate() {
        return FincaProductoDto.builder()
                .id(id)
                .fincaId(finca != null ? finca.getId() : null)
                .productoId(producto != null ? producto.getId() : null)
                .stock(stock)
                .build();
    }
}