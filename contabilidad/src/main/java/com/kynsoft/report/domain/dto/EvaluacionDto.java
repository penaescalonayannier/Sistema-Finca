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
}
