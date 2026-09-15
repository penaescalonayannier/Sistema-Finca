package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ResumenPagosTotalesDto {
    private Integer totalPagos;
    private Double montoTotal;
    private Double montoEfectivo;
    private Double montoTransferencia;
    private Double porcentajeEfectivo;
    private Double porcentajeTransferencia;
}
