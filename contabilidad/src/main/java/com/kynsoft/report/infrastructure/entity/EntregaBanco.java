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
@Table(name = "entrega_banco")
public class EntregaBanco {
    @Id
    private UUID id;
    @Column(name = "finca_id", nullable = false)
    private UUID fincaId;
    @Column(nullable = false)
    private LocalDateTime fecha;
    @Column(nullable = false)
    private Double importe;
    @Column(name = "referencia_bancaria")
    private String referenciaBancaria;
    @Column(name = "entregado_por")
    private String entregadoPor;
    @Column(name = "recibido_por")
    private String recibidoPor;
    @Column(name = "usuario_id")
    private UUID usuarioId;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    private String observaciones;
    private Boolean activo = true;
}
