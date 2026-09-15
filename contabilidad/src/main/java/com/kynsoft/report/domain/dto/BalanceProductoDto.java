package com.kynsoft.report.domain.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceProductoDto {

    private UUID fincaProductoId;
    private String productoCode;
    private String productoName;
    private Double stockInicial;
    private Double totalEntradas;
    private Double totalSalidas;
    private Double stockFinal;
    private Long cantidadMovimientos;
}
