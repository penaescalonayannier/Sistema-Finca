package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.ProduccionTerminadaDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
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
@Table(name = "produccion_terminada")
public class ProduccionTerminada {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "finca_id", nullable = false)
    private UUID fincaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finca_id", insertable = false, updatable = false)
    private Finca finca;

    @Column(name = "producto_id", nullable = false)
    private UUID productoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", insertable = false, updatable = false)
    private Producto producto;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "cantidad_terminada", nullable = false)
    private Double cantidadTerminada;

    @Column(name = "almacen_finca_producto_id")
    private UUID almacenFincaProductoId;

    @Column(name = "trabajador_entrega_id", nullable = false)
    private UUID trabajadorEntregaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_entrega_id", insertable = false, updatable = false)
    private Trabajador trabajadorEntrega;

    @Column(name = "trabajador_recibe_id", nullable = false)
    private UUID trabajadorRecibeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_recibe_id", insertable = false, updatable = false)
    private Trabajador trabajadorRecibe;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "numero_documento", length = 30)
    private String numeroDocumento;
    @Column(name = "producto_codigo_snapshot", length = 50)
    private String productoCodigoSnapshot;
    @Column(name = "producto_nombre_snapshot", length = 150)
    private String productoNombreSnapshot;
    @Column(name = "unidad_medida_snapshot", length = 30)
    private String unidadMedidaSnapshot;
    @Column(name = "almacen_nombre_snapshot", length = 150)
    private String almacenNombreSnapshot;
    @Column(name = "almacen_inventario_snapshot", length = 50)
    private String almacenInventarioSnapshot;
    @Column(name = "costo_unitario", precision = 19, scale = 4)
    private Double costoUnitario;
    @Column(name = "importe", precision = 19, scale = 4)
    private Double importe;
    @Column(name = "saldo_posterior", precision = 19, scale = 4)
    private Double saldoPosterior;
    @Column(name = "lote", length = 100)
    private String lote;
    @Column(name = "centro_costo", length = 50)
    private String centroCosto;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    public ProduccionTerminada(ProduccionTerminadaDto dto) {
        this.id = dto.getId();
        this.fincaId = dto.getFincaId();
        this.productoId = dto.getProductoId();
        this.fecha = dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now();
        this.cantidadTerminada = dto.getCantidadTerminada();
        this.almacenFincaProductoId = dto.getAlmacenFincaProductoId();
        this.trabajadorEntregaId = dto.getTrabajadorEntregaId();
        this.trabajadorRecibeId = dto.getTrabajadorRecibeId();
        this.observaciones = dto.getObservaciones();
        this.numeroDocumento = dto.getNumeroDocumento();
        this.productoCodigoSnapshot = dto.getProductoCodigoSnapshot();
        this.productoNombreSnapshot = dto.getProductoNombreSnapshot();
        this.unidadMedidaSnapshot = dto.getUnidadMedidaSnapshot();
        this.almacenNombreSnapshot = dto.getAlmacenNombreSnapshot();
        this.almacenInventarioSnapshot = dto.getAlmacenInventarioSnapshot();
        this.costoUnitario = dto.getCostoUnitario();
        this.importe = dto.getImporte();
        this.saldoPosterior = dto.getSaldoPosterior();
        this.lote = dto.getLote();
        this.centroCosto = dto.getCentroCosto();
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
    }

    public ProduccionTerminadaDto toAggregate() {
        return ProduccionTerminadaDto.builder()
                .id(id)
                .fincaId(fincaId)
                .fincaCode(finca != null ? finca.getCode() : null)
                .fincaName(finca != null ? finca.getName() : null)
                .productoId(productoId)
                .productoCode(producto != null ? producto.getCode() : null)
                .productoName(producto != null ? producto.getName() : null)
                .fecha(fecha)
                .cantidadTerminada(cantidadTerminada)
                .almacenFincaProductoId(almacenFincaProductoId)
                .trabajadorEntregaId(trabajadorEntregaId)
                .trabajadorEntregaNombre(trabajadorEntrega != null ? trabajadorEntrega.getNombre() : null)
                .trabajadorRecibeId(trabajadorRecibeId)
                .trabajadorRecibeNombre(trabajadorRecibe != null ? trabajadorRecibe.getNombre() : null)
                .observaciones(observaciones)
                .numeroDocumento(numeroDocumento)
                .productoCodigoSnapshot(productoCodigoSnapshot)
                .productoNombreSnapshot(productoNombreSnapshot)
                .unidadMedidaSnapshot(unidadMedidaSnapshot)
                .almacenNombreSnapshot(almacenNombreSnapshot)
                .almacenInventarioSnapshot(almacenInventarioSnapshot)
                .costoUnitario(costoUnitario)
                .importe(importe)
                .saldoPosterior(saldoPosterior)
                .lote(lote)
                .centroCosto(centroCosto)
                .activo(activo)
                .build();
    }

    public void setCantidadTerminada(Number cantidadTerminada) {
        this.cantidadTerminada = cantidadTerminada != null ? cantidadTerminada.doubleValue() : null;
    }
}
