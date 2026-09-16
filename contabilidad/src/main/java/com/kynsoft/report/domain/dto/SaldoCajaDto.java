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
public class SaldoCajaDto {
    private UUID fincaId;
    private Double efectivoCobrado;
    private Double entregadoBanco;
    private Double saldoDisponible;
    /** Billetes físicos cuya denominación ya fue declarada. */
    private List<SaldoDenominacionCajaDto> denominaciones;
    /** Efectivo histórico sin billetes, que debe declararse mediante apertura. */
    private Double pendienteSinDesglose;
}
