package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.DeudaTrabajadorDetalleDto;
import com.kynsoft.report.domain.dto.FormaPago;
import com.kynsoft.report.domain.dto.TipoMovimiento;
import com.kynsoft.report.domain.dto.TipoSalida;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "deuda_trabajador_detalle")
public class DeudaTrabajadorDetalle {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "trabajador_id")
    private UUID trabajadorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trabajador_id", insertable = false, updatable = false)
    private Trabajador trabajador;

    @Column(name = "salida_id")
    private UUID salidaId;

    @Column(name = "salida_numero")
    private String salidaNumero;

    @Enumerated(EnumType.STRING)
    @Column(name = "salida_tipo")
    private TipoSalida salidaTipo;

    @Column(name = "producto_id")
    private UUID productoId;

    @Column(name = "producto_codigo")
    private String productoCodigo;

    @Column(name = "producto_nombre")
    private String productoNombre;

    @Column(name = "cantidad")
    private Double cantidad;

    @Column(name = "precio_unitario")
    private Double precioUnitario;

    @Column(name = "importe")
    private Double importe;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "pagado")
    private Boolean pagado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento")
    private TipoMovimiento tipoMovimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pago")
    private FormaPago formaPago;

    @Column(name = "referencia_bancaria")
    private String referenciaBancaria;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    public void setCantidad(Number cantidad) {
        this.cantidad = cantidad != null ? cantidad.doubleValue() : null;
    }

    public DeudaTrabajadorDetalle(DeudaTrabajadorDetalleDto dto) {
        this.id = dto.getId();
        this.trabajadorId = dto.getTrabajadorId();
        this.salidaId = dto.getSalidaId();
        this.salidaNumero = dto.getSalidaNumero();
        this.salidaTipo = dto.getSalidaTipo();
        this.productoId = dto.getProductoId();
        this.productoCodigo = dto.getProductoCodigo();
        this.productoNombre = dto.getProductoNombre();
        this.cantidad = dto.getCantidad();
        this.precioUnitario = dto.getPrecioUnitario();
        this.importe = dto.getImporte();
        this.fecha = dto.getFecha();
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
        this.pagado = dto.getPagado() != null ? dto.getPagado() : false;
        this.tipoMovimiento = dto.getTipoMovimiento() != null ? dto.getTipoMovimiento() : TipoMovimiento.COMPRA;
        this.formaPago = dto.getFormaPago();
        this.referenciaBancaria = dto.getReferenciaBancaria();
        this.observaciones = dto.getObservaciones();
    }

    public DeudaTrabajadorDetalleDto toAggregate() {
        return DeudaTrabajadorDetalleDto.builder()
                .id(id)
                .trabajadorId(trabajadorId)
                .trabajadorNombre(trabajador != null ? trabajador.getNombre() : null)
                .trabajadorRuc(trabajador != null ? trabajador.getRuc() : null)
                .salidaId(salidaId)
                .salidaNumero(salidaNumero)
                .salidaTipo(salidaTipo)
                .productoId(productoId)
                .productoCodigo(productoCodigo)
                .productoNombre(productoNombre)
                .cantidad(cantidad)
                .precioUnitario(precioUnitario)
                .importe(importe)
                .fecha(fecha)
                .activo(activo)
                .pagado(pagado)
                .tipoMovimiento(tipoMovimiento)
                .formaPago(formaPago)
                .referenciaBancaria(referenciaBancaria)
                .observaciones(observaciones)
                .build();
    }
}
