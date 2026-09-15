package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TotalesGraficoDto {
    private Integer entradas;
    private Integer salidas;
    private Integer balance;
}
