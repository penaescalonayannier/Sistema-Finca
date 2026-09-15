package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TotalesGraficoDto {
    private Double entradas;
    private Double salidas;
    private Double balance;
}
