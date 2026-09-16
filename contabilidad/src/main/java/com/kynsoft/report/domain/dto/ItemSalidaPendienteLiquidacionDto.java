package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemSalidaPendienteLiquidacionDto {
    private UUID itemSalidaId;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private String productoNombre;
    private Double cantidad;
    private Double precio;
    private Double importeTotal;
    private Double importeCobrado;
    private Double saldoPendiente;
}
