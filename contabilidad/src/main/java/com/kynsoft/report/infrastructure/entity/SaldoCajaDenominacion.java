package com.kynsoft.report.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/** Saldo físico actual por finca y denominación; se actualiza dentro de la misma transacción. */
@Getter
@Setter
@Entity
@Table(name = "saldo_caja_denominacion")
public class SaldoCajaDenominacion {
    @Id
    private UUID id;
    @Column(name = "finca_id", nullable = false)
    private UUID fincaId;
    @Column(nullable = false)
    private Integer denominacion;
    @Column(nullable = false)
    private Integer cantidad;
}
