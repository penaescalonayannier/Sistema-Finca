package com.kynsoft.report.domain.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriterioEvaluacionDto {
    private UUID id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private Integer orden;
}
