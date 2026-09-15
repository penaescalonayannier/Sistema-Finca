package com.kynsoft.report.domain.dto.reportes;

import lombok.*;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ResumenVentasTotalesDto {
    private Integer totalSalidas;
    private Integer totalItems;
    private Double valorTotal;
    private Map<String, Double> valorPorDestino;
}
