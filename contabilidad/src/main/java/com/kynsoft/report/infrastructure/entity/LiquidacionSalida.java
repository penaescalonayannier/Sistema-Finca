package com.kynsoft.report.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "liquidacion_salida")
public class LiquidacionSalida {
    @Id
    private UUID id;
    @Column(name = "salida_id", nullable = false)
    private UUID salidaId;
    @Column(name = "finca_id", nullable = false)
    private UUID fincaId;
    @Column(nullable = false)
    private LocalDateTime fecha;
    @Column(name = "entregado_por")
    private String entregadoPor;
    @Column(name = "recibido_por")
    private String recibidoPor;
    private String observaciones;
    private Boolean activo = true;
}
