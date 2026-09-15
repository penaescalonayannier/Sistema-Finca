package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TotalesKardexDto {
    private Integer stockInicialTotal;
    private Integer entradasTotal;
    private Integer salidasTotal;
    private Integer stockFinalTotal;
}
