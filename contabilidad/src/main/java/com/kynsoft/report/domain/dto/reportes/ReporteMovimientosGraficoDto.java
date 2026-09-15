package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ReporteMovimientosGraficoDto {
    private List<String> etiquetas;
    private List<SerieGraficoDto> series;
    private TotalesGraficoDto totales;
}
