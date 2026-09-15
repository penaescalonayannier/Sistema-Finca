package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class SerieGraficoDto {
    private String nombre;
    private String color;
    private List<Double> datos;
}
