package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.FormaPago;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "liquidacion_item_salida")
public class LiquidacionItemSalida {
    @Id
    private UUID id;
    @Column(name = "liquidacion_salida_id", nullable = false)
    private UUID liquidacionSalidaId;
    @Column(name = "item_salida_id", nullable = false)
    private UUID itemSalidaId;
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pago", nullable = false)
    private FormaPago formaPago;
    @Column(nullable = false)
    private Double importe;
    @Column(name = "referencia_bancaria")
    private String referenciaBancaria;
}
