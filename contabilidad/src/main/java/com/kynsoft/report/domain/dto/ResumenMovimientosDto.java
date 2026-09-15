package com.kynsoft.report.domain.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenMovimientosDto {

    private PeriodoDto periodo;
    private Map<String, Long> entradas;
    private Map<String, Long> salidas;
    private Long balance;
    private Long totalMovimientos;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodoDto {
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
    }
}
