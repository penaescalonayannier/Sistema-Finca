package com.kynsoft.report.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EvaluacionDto {
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
    /** Instantánea JSON de los criterios vigentes cuando se emitió la evaluación. */
    private String criteriosAplicados;
    private String constanciaJefe;
    private String constanciaTrabajador;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaCierre;
    private String observacionesCierre;
}
