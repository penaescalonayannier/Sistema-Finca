package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.TipoMovimientoCaja;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "movimiento_caja")
public class MovimientoCaja {
    @Id
    private UUID id;
    @Column(name = "finca_id", nullable = false)
    private UUID fincaId;
    @Column(nullable = false)
    private LocalDateTime fecha;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimientoCaja tipo;
    /** Positivo para cobro físico; negativo para entrega bancaria. */
    @Column(nullable = false)
    private Double importe;
    @Column(name = "liquidacion_item_salida_id")
    private UUID liquidacionItemSalidaId;
    @Column(name = "entrega_banco_id")
    private UUID entregaBancoId;
    @Column(name = "documento_caja_id")
    private UUID documentoCajaId;
    /** Usuario autenticado que registró el movimiento físico. */
    @Column(name = "usuario_id")
    private UUID usuarioId;
    /** Instante técnico de registro, independiente de la fecha económica. */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    private String observaciones;
}
