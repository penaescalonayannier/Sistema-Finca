package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.dto.EstadoStock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "almacen_finca_producto")
public class AlmacenFincaProducto {
    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finca_producto_id", nullable = false)
    private FincaProducto fincaProducto;

    @Column(name = "stock", nullable = false)
    private Double stock = 0.0;

    @Column(name = "stock_minimo")
    private Double stockMinimo = 0.0;

    @Column(name = "stock_maximo")
    private Double stockMaximo;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void setStock(Number stock) {
        this.stock = stock != null ? stock.doubleValue() : null;
    }

    public void setStockMinimo(Number stockMinimo) {
        this.stockMinimo = stockMinimo != null ? stockMinimo.doubleValue() : null;
    }

    public void setStockMaximo(Number stockMaximo) {
        this.stockMaximo = stockMaximo != null ? stockMaximo.doubleValue() : null;
    }

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public AlmacenFincaProductoDto toAggregate() {
        return AlmacenFincaProductoDto.builder()
                .id(id)
                .almacenId(almacen != null ? almacen.getId() : null)
                .almacenNombre(almacen != null ? almacen.getNombre() : null)
                .almacenInventario(almacen != null ? almacen.getInventario() : null)
                .fincaProductoId(fincaProducto != null ? fincaProducto.getId() : null)
                .fincaId(fincaProducto != null && fincaProducto.getFinca() != null ? fincaProducto.getFinca().getId() : null)
                .productoId(fincaProducto != null && fincaProducto.getProducto() != null
                        ? fincaProducto.getProducto().getId() : null)
                .productoCode(fincaProducto != null && fincaProducto.getProducto() != null
                        ? fincaProducto.getProducto().getCode() : null)
                .productoName(fincaProducto != null && fincaProducto.getProducto() != null
                        ? fincaProducto.getProducto().getName() : null)
                .productoPrice(fincaProducto != null && fincaProducto.getProducto() != null
                        ? fincaProducto.getProducto().getPrice() : null)
                .unidadMedida(fincaProducto != null && fincaProducto.getProducto() != null
                        ? fincaProducto.getProducto().getUnidadMedida() : null)
                .stock(stock)
                .stockMinimo(stockMinimo)
                .stockMaximo(stockMaximo)
                .estadoStock(getEstadoStock())
                .activo(activo)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public EstadoStock getEstadoStock() {
        if (stock == null || stock == 0.0) return EstadoStock.CRITICO;
        if (stockMinimo != null && stockMinimo > 0 && stock < stockMinimo) return EstadoStock.BAJO;
        if (stockMaximo != null && stock > stockMaximo) return EstadoStock.EXCESO;
        return EstadoStock.NORMAL;
    }
}
