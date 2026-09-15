package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EntradasSalidasDto {
    private Double total;
    private Map<String, Double> porTipo;
}
