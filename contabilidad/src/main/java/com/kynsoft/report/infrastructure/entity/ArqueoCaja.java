package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.EstadoArqueoCaja;
import com.kynsoft.report.domain.dto.TipoArqueoCaja;
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
@Table(name = "arqueo_caja")
public class ArqueoCaja {
    @Id
    private UUID id;
    @Column(nullable = false, unique = true)
    private Long numero;
    @Column(name = "finca_id", nullable = false)
    private UUID fincaId;
    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoArqueoCaja estado;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoArqueoCaja tipo;
    @Column(name = "contador_responsable", nullable = false)
    private String contadorResponsable;
    @Column(name = "contador_usuario_id")
    private UUID contadorUsuarioId;
    private String custodio;
    @Column(name = "recibido_por")
    private String recibidoPor;
    private String observaciones;
    @Column(name = "observaciones_apertura")
    private String observacionesApertura;
    @Column(name = "observaciones_cierre")
    private String observacionesCierre;
    @Column(name = "total_esperado", nullable = false)
    private Double totalEsperado;
    @Column(name = "total_fisico")
    private Double totalFisico;
    private Double diferencia;
}
