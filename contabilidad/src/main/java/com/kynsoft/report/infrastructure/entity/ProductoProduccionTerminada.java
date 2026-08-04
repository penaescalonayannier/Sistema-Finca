package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.ProductoProduccionTerminadaDto;
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
@Table(name = "producto_produccion_terminada")
public class ProductoProduccionTerminada {
    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produccion_terminada_id", nullable = false)
    private ProduccionTerminada produccionTerminada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Double cantidad;

    public ProductoProduccionTerminada(ProductoProduccionTerminadaDto dto) {
        this.id = dto.getId();
        this.cantidad = dto.getCantidad();
    }

    public ProductoProduccionTerminadaDto toAggregate() {
        return ProductoProduccionTerminadaDto.builder()
                .id(id)
                .produccionTerminada(produccionTerminada != null ? produccionTerminada.toAggregate() : null)
                .producto(producto != null ? producto.toAggregate() : null)
                .cantidad(cantidad)
                .build();
    }
}