package com.kynsoft.report.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/** Renglón auditable de billetes usados en un movimiento de caja. */
@Getter
@Setter
@Entity
@Table(name = "movimiento_caja_denominacion")
public class MovimientoCajaDenominacion {
    @Id
    private UUID id;
    @Column(name = "movimiento_caja_id", nullable = false)
    private UUID movimientoCajaId;
    @Column(nullable = false)
    private Integer denominacion;
    /** Positiva al entrar y negativa al salir hacia banco. */
    @Column(nullable = false)
    private Integer cantidad;
}
