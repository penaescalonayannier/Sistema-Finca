package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.EstadoExpedienteDisciplinario;
import com.kynsoft.report.domain.dto.TipoIncidenciaDisciplinaria;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ExpedienteDisciplinario {
    @Id private UUID id;
    @Column(name = "finca_id", nullable = false) private UUID fincaId;
    @Column(name = "trabajador_id", nullable = false) private UUID trabajadorId;
    @Column(name = "aprobador_id") private UUID aprobadorId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private TipoIncidenciaDisciplinaria tipo;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private EstadoExpedienteDisciplinario estado;
    @Column(nullable = false) private LocalDate fecha;
    @Column(nullable = false, length = 3000) private String descripcion;
    @Column(length = 3000) private String evidencia;
    @Column(length = 3000) private String observaciones;
    @Column(length = 1000) private String medida;
    @Column(length = 3000) private String resolucion;
    @Column(name = "fecha_notificacion") private LocalDateTime fechaNotificacion;
    @Column(name = "fecha_resolucion") private LocalDateTime fechaResolucion;
    @Column(name = "fecha_anulacion") private LocalDateTime fechaAnulacion;
    @Column(name = "motivo_anulacion", length = 3000) private String motivoAnulacion;
    @Column(name = "creado_en", nullable = false) private LocalDateTime creadoEn;
    @Column(name = "actualizado_en", nullable = false) private LocalDateTime actualizadoEn;
}
