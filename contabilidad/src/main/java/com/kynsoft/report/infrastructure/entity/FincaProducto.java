package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.EstadoStock;
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
    private Double stock;

    @Column(name = "stock_minimo", nullable = false)
    private Double stockMinimo = 0.0;

    @Column(name = "stock_maximo")
    private Double stockMaximo;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public void setStock(Number stock) {
        this.stock = stock != null ? stock.doubleValue() : null;
    }

    public void setStockMinimo(Number stockMinimo) {
        this.stockMinimo = stockMinimo != null ? stockMinimo.doubleValue() : null;
    }

    public void setStockMaximo(Number stockMaximo) {
        this.stockMaximo = stockMaximo != null ? stockMaximo.doubleValue() : null;
    }

    public FincaProducto(FincaProductoDto dto) {
        this.id = dto.getId();
        this.stock = dto.getStock();
        this.stockMinimo = dto.getStockMinimo() != null ? dto.getStockMinimo() : 0;
        this.stockMaximo = dto.getStockMaximo();
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
    }

    public FincaProductoDto toAggregate() {
        return FincaProductoDto.builder()
                .id(id)
                .fincaId(finca != null ? finca.getId() : null)
                .productoId(producto != null ? producto.getId() : null)
                .stock(stock)
                .stockMinimo(stockMinimo)
                .stockMaximo(stockMaximo)
                .estadoStock(getEstadoStock())
                .activo(activo)
                .build();
    }

    public EstadoStock getEstadoStock() {
        if (stock == null || stock == 0.0) return EstadoStock.CRITICO;
        if (stockMinimo != null && stockMinimo > 0 && stock < stockMinimo) return EstadoStock.BAJO;
        if (stockMaximo != null && stock > stockMaximo) return EstadoStock.EXCESO;
        return EstadoStock.NORMAL;
    }
}
