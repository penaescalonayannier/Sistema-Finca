package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ReporteConsolidadoPdfDto {
    private String year;
    private String mes;
    private List<TrabajadorConsolidadoDto> trabajadores;
    private int diasDelMes;
}