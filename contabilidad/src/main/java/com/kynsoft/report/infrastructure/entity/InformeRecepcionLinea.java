package com.kynsoft.report.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "informe_recepcion_linea")
public class InformeRecepcionLinea {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "informe_recepcion_id", nullable = false)
    private InformeRecepcion informeRecepcion;
    @Column(name = "almacen_finca_producto_id", nullable = false) private UUID almacenFincaProductoId;
    @Column(name = "finca_producto_id", nullable = false) private UUID fincaProductoId;
    @Column(name = "producto_id", nullable = false) private UUID productoId;
    @Column(name = "producto_codigo", nullable = false, length = 60) private String productoCodigo;
    @Column(name = "producto_nombre", nullable = false, length = 180) private String productoNombre;
    @Column(name = "unidad_medida", length = 30) private String unidadMedida;
    @Column(name = "cantidad", nullable = false) private Double cantidad;
    @Column(name = "costo_unitario", nullable = false) private Double costoUnitario;
    @Column(name = "importe", nullable = false) private Double importe;
    @Column(name = "saldo_posterior", nullable = false) private Double saldoPosterior;
    @PrePersist void prePersist() { if (id == null) id = UUID.randomUUID(); }
}
