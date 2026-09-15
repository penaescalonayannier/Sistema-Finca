package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.MovimientoStockDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
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
@Table(name = "movimiento_stock")
public class MovimientoStock {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "finca_producto_id", nullable = false)
    private UUID fincaProductoId;

    @Column(name = "finca_id", nullable = false)
    private UUID fincaId;

    @Column(name = "producto_id", nullable = false)
    private UUID productoId;

    @Column(name = "almacen_id")
    private UUID almacenId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoMovimientoStock tipo;

    @Column(name = "cantidad", nullable = false)
    private Double cantidad;

    @Column(name = "stock_anterior", nullable = false)
    private Double stockAnterior;

    @Column(name = "stock_nuevo", nullable = false)
    private Double stockNuevo;

    @Column(name = "referencia_id")
    private UUID referenciaId;

    @Column(name = "referencia_tabla")
    private String referenciaTabla;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    public MovimientoStock(MovimientoStockDto dto) {
        this.id = dto.getId();
        this.fincaProductoId = dto.getFincaProductoId();
        this.fincaId = dto.getFincaId();
        this.productoId = dto.getProductoId();
        this.almacenId = dto.getAlmacenId();
        this.tipo = dto.getTipo();
        this.cantidad = dto.getCantidad();
        this.stockAnterior = dto.getStockAnterior();
        this.stockNuevo = dto.getStockNuevo();
        this.referenciaId = dto.getReferenciaId();
        this.referenciaTabla = dto.getReferenciaTabla();
        this.descripcion = dto.getDescripcion();
        this.observaciones = dto.getObservaciones();
        this.fecha = dto.getFecha();
    }

    public MovimientoStockDto toAggregate() {
        return MovimientoStockDto.builder()
                .id(id)
                .fincaProductoId(fincaProductoId)
                .fincaId(fincaId)
                .productoId(productoId)
                .almacenId(almacenId)
                .tipo(tipo)
                .cantidad(cantidad)
                .stockAnterior(stockAnterior)
                .stockNuevo(stockNuevo)
                .referenciaId(referenciaId)
                .referenciaTabla(referenciaTabla)
                .descripcion(descripcion)
                .observaciones(observaciones)
                .fecha(fecha)
                .build();
    }
}
