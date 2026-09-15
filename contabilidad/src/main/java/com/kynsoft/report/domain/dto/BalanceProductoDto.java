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
    private Integer stockInicial;
    private Long totalEntradas;
    private Long totalSalidas;
    private Integer stockFinal;
    private Long cantidadMovimientos;
}
