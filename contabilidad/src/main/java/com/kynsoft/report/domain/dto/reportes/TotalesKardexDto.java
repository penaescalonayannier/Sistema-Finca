package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TotalesKardexDto {
    private Double stockInicialTotal;
    private Double entradasTotal;
    private Double salidasTotal;
    private Double stockFinalTotal;
}
