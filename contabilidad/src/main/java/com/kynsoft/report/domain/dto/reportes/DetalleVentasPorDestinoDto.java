package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DetalleVentasPorDestinoDto {
    private String destino;
    private Integer cantidadSalidas;
    private Integer cantidadItems;
    private Double valor;
    private Double porcentaje;
}
