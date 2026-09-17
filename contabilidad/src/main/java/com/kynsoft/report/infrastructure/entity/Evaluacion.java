package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.EvaluacionDto;
import com.kynsoft.report.domain.dto.EstadoEvaluacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Evaluacion {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "grupo_id")
    private UUID grupoId;

    @Column(name = "trabajador_id", nullable = false)
    private UUID trabajadorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_id", insertable = false, updatable = false)
    private Trabajador trabajador;

    @Column(name = "jefe_id", nullable = false)
    private UUID jefeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jefe_id", insertable = false, updatable = false)
    private Trabajador jefe;

    private String mes;

    private Integer year;

    private Integer calificacion;

    private String comentarios;

    @Column(name = "fecha_evaluacion")
    private LocalDateTime fechaEvaluacion;

    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoEvaluacion estado = EstadoEvaluacion.BORRADOR;

    @Column(name = "evidencia", length = 4000)
    private String evidencia;

    @Column(name = "criterios_aplicados", length = 8000)
    private String criteriosAplicados;

    @Column(name = "constancia_jefe", length = 500)
    private String constanciaJefe;

    @Column(name = "constancia_trabajador", length = 500)
    private String constanciaTrabajador;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "observaciones_cierre", length = 2000)
    private String observacionesCierre;

    public Evaluacion(EvaluacionDto dto) {
        this.id = dto.getId();
        this.grupoId = dto.getGrupoId();
        this.trabajadorId = dto.getTrabajadorId();
        this.jefeId = dto.getJefeId();
        this.mes = dto.getMes();
        this.year = dto.getYear();
        this.calificacion = dto.getCalificacion();
        this.comentarios = dto.getComentarios();
        this.fechaEvaluacion = dto.getFechaEvaluacion();
        this.estado = dto.getEstado() == null ? EstadoEvaluacion.BORRADOR : dto.getEstado();
        this.evidencia = dto.getEvidencia();
        this.criteriosAplicados = dto.getCriteriosAplicados();
        this.constanciaJefe = dto.getConstanciaJefe();
        this.constanciaTrabajador = dto.getConstanciaTrabajador();
        this.fechaEnvio = dto.getFechaEnvio();
        this.fechaCierre = dto.getFechaCierre();
        this.observacionesCierre = dto.getObservacionesCierre();
    }

    public EvaluacionDto toAggregate() {
        return EvaluacionDto
                .builder()
                .id(id)
                .grupoId(grupoId)
                .trabajadorId(trabajadorId)
                .jefeId(jefeId)
                .mes(mes)
                .year(year)
                .calificacion(calificacion)
                .comentarios(comentarios)
                .fechaEvaluacion(fechaEvaluacion)
                .estado(estado == null ? EstadoEvaluacion.BORRADOR : estado)
                .evidencia(evidencia)
                .criteriosAplicados(criteriosAplicados)
                .constanciaJefe(constanciaJefe)
                .constanciaTrabajador(constanciaTrabajador)
                .fechaEnvio(fechaEnvio)
                .fechaCierre(fechaCierre)
                .observacionesCierre(observacionesCierre)
                .build();
    }
}
