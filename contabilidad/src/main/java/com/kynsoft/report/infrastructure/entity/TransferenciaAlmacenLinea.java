package com.kynsoft.report.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

/** Renglón despachado, recibido o reintegrado del documento SC-2-09. */
@Getter @Setter @Entity @Table(name = "transferencia_almacen_linea")
public class TransferenciaAlmacenLinea {
    @Id private UUID id;
    @Column(name = "transferencia_id", nullable = false) private UUID transferenciaId;
    @Column(name = "finca_producto_id", nullable = false) private UUID fincaProductoId;
    @Column(name = "origen_almacen_finca_producto_id", nullable = false) private UUID origenAlmacenFincaProductoId;
    @Column(name = "destino_almacen_finca_producto_id") private UUID destinoAlmacenFincaProductoId;
    @Column(name = "cantidad_despachada", nullable = false) private Double cantidadDespachada;
    @Column(name = "cantidad_recibida") private Double cantidadRecibida;
    @Column(name = "cantidad_rechazada") private Double cantidadRechazada;
    @Column(length = 1000) private String observaciones;
    @PrePersist void prePersist() { if (id == null) id = UUID.randomUUID(); }
}
