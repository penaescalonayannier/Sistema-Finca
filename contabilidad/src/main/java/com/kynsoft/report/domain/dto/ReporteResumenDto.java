package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ReporteResumenDto {
    private UUID id;
    private String codigo;
    private String bloque;
    private String campo;
    private String area;
    private String norma;
    private String fecha;
    private String year;
    private String mes;
    private Integer totalTrabajadores;
}
