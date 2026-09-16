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
public class SaldoCajaDto {
    private UUID fincaId;
    private Double efectivoCobrado;
    private Double entregadoBanco;
    private Double saldoDisponible;
}
