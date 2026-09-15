package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ResumenFacturacionDto {
    private Integer totalDocumentos;
    private Integer totalVales;
    private Integer totalFacturas;
    private Integer cantidadProductos;
    private Double valorTotal;
}
