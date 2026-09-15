package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.ItemSalidaDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "item_salida")
public class ItemSalida {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "salida_id", nullable = false)
    private UUID salidaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salida_id", insertable = false, updatable = false)
    private Salida salida;

    @Column(name = "finca_producto_id")
    private UUID fincaProductoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finca_producto_id", insertable = false, updatable = false)
    private FincaProducto fincaProducto;

    @Column(name = "almacen_finca_producto_id")
    private UUID almacenFincaProductoId;

    @Column(name = "trabajador_id", nullable = true)
    private UUID trabajadorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_id", insertable = false, updatable = false)
    private Trabajador trabajador;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio", nullable = false)
    private Double precio;

    @Column(name = "pagado")
    private Boolean pagado;

    public ItemSalida(ItemSalidaDto dto) {
        this.id = dto.getId();
        this.salidaId = dto.getSalidaId();
        this.fincaProductoId = dto.getFincaProductoId();
        this.almacenFincaProductoId = dto.getAlmacenFincaProductoId();
        this.trabajadorId = dto.getTrabajadorId();
        this.cantidad = dto.getCantidad();
        this.precio = dto.getPrecio();
        this.pagado = dto.getPagado() != null ? dto.getPagado() : false;
    }

    public ItemSalidaDto toAggregate() {
        return ItemSalidaDto.builder()
                .id(id)
                .salidaId(salidaId)
                .fincaProductoId(fincaProductoId)
                .almacenFincaProductoId(almacenFincaProductoId)
                .productoCode(fincaProducto != null && fincaProducto.getProducto() != null ? fincaProducto.getProducto().getCode() : null)
                .productoName(fincaProducto != null && fincaProducto.getProducto() != null ? fincaProducto.getProducto().getName() : null)
                .unidadMedida(fincaProducto != null && fincaProducto.getProducto() != null && fincaProducto.getProducto().getUnidadMedida() != null
                        ? fincaProducto.getProducto().getUnidadMedida().name() : null)
                .trabajadorId(trabajadorId)
                .trabajadorNombre(trabajador != null ? trabajador.getNombre() : null)
                .cantidad(cantidad)
                .precio(precio)
                .pagado(pagado)
                .build();
    }
}
