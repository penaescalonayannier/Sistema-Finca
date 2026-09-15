package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ResumenDeudasDto {
    private Integer totalTrabajadores;
    private Integer trabajadoresConDeuda;
    private Integer trabajadoresSinDeuda;
    private Double montoTotalDeuda;
    private Double promedioDeuda;
    private Double deudaMaxima;
    private Double deudaMinima;
}
