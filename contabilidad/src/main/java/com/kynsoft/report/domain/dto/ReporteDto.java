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
public class ReporteDto {
    private UUID id;
    private String bloque;
    private String campo;
    private String area;
    private String norma;
    private String codigo;
    private String year;
    private String mes;
    private String fecha; // Campo opcional
    private UUID trabajadorResponsableId;
    private String trabajadorResponsableNombre;
}