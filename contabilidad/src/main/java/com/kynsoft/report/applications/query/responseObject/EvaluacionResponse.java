package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.EvaluacionDto;
import com.kynsoft.report.domain.dto.EstadoEvaluacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class EvaluacionResponse implements IResponse {
    private UUID id;
    private UUID grupoId;
    private UUID trabajadorId;
    private UUID jefeId;
    private String mes;
    private Integer year;
    private Integer calificacion;
    private String comentarios;
    private LocalDateTime fechaEvaluacion;
    private EstadoEvaluacion estado;
    private String evidencia;
    private String criteriosAplicados;
    private String constanciaJefe;
    private String constanciaTrabajador;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaCierre;
    private String observacionesCierre;

    public EvaluacionResponse(EvaluacionDto evaluacion) {
        this.id = evaluacion.getId();
        this.grupoId = evaluacion.getGrupoId();
        this.trabajadorId = evaluacion.getTrabajadorId();
        this.jefeId = evaluacion.getJefeId();
        this.mes = evaluacion.getMes();
        this.year = evaluacion.getYear();
        this.calificacion = evaluacion.getCalificacion();
        this.comentarios = evaluacion.getComentarios();
        this.fechaEvaluacion = evaluacion.getFechaEvaluacion();
        this.estado = evaluacion.getEstado();
        this.evidencia = evaluacion.getEvidencia();
        this.criteriosAplicados = evaluacion.getCriteriosAplicados();
        this.constanciaJefe = evaluacion.getConstanciaJefe();
        this.constanciaTrabajador = evaluacion.getConstanciaTrabajador();
        this.fechaEnvio = evaluacion.getFechaEnvio();
        this.fechaCierre = evaluacion.getFechaCierre();
        this.observacionesCierre = evaluacion.getObservacionesCierre();
    }
}
