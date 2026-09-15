package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DetalleFacturacionDto {
    private String finca;
    private String tipo;
    private String destino;
    private Integer documentos;
    private Integer cantidad;
    private Double valor;
    private Double porcentaje;
}
