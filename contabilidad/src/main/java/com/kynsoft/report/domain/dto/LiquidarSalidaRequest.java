package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiquidarSalidaRequest {
    private UUID salidaId;
    private String entregadoPor;
    private String recibidoPor;
    private String observaciones;
    private List<AplicacionLiquidacionSalidaDto> aplicaciones;
}
