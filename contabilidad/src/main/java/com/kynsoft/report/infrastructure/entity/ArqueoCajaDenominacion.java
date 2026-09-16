package com.kynsoft.report.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "arqueo_caja_denominacion")
public class ArqueoCajaDenominacion {
    @Id
    private UUID id;
    @Column(name = "arqueo_caja_id", nullable = false)
    private UUID arqueoCajaId;
    @Column(nullable = false)
    private Integer denominacion;
    @Column(name = "cantidad_esperada", nullable = false)
    private Integer cantidadEsperada;
    @Column(name = "cantidad_fisica")
    private Integer cantidadFisica;
}
