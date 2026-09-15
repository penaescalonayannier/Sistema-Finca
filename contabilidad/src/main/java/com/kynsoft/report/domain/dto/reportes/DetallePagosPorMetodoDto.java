package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DetallePagosPorMetodoDto {
    private String formaPago;
    private Integer cantidad;
    private Double monto;
    private Double porcentaje;
}
