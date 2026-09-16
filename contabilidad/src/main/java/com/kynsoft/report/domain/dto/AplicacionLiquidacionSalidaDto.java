package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AplicacionLiquidacionSalidaDto {
    private UUID itemSalidaId;
    private Double importe;
    private FormaPago formaPago;
    private String referenciaBancaria;
    /** Obligatorio cuando la forma de pago sea EFECTIVO. */
    private List<DenominacionCajaDto> denominaciones;
}
