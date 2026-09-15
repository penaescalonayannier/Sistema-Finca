package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EntradasSalidasDto {
    private Integer total;
    private Map<String, Integer> porTipo;
}
