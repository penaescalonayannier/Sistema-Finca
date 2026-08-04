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
public class ReporteConsolidadoPorResponsablePdfDto {
    private String year;
    private String mes;
    private List<ResponsableConsolidadoDto> responsables;
    private int diasDelMes;
}
