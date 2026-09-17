package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.EstadoTransferenciaAlmacen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

/** Cabecera SC-2-09: el despacho y la recepción son actos independientes. */
@Getter @Setter @Entity @Table(name = "transferencia_almacen")
public class TransferenciaAlmacen {
    @Id private UUID id;
    @Column(name = "finca_id", nullable = false) private UUID fincaId;
    @Column(name = "numero_documento", nullable = false, length = 40) private String numeroDocumento;
    @Column(name = "origen_almacen_id", nullable = false) private UUID origenAlmacenId;
    @Column(name = "destino_almacen_id", nullable = false) private UUID destinoAlmacenId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private EstadoTransferenciaAlmacen estado;
    @Column(name = "fecha_despacho", nullable = false) private LocalDateTime fechaDespacho;
    @Column(name = "fecha_recepcion") private LocalDateTime fechaRecepcion;
    @Column(length = 1000) private String observaciones;
    @Column(name = "despachado_por_id") private UUID despachadoPorId;
    @Column(name = "recibido_por_id") private UUID recibidoPorId;
    @Column(name = "motivo_cierre", length = 1000) private String motivoCierre;
    @PrePersist void prePersist() { if (id == null) id = UUID.randomUUID(); if (fechaDespacho == null) fechaDespacho = LocalDateTime.now(); }
}
