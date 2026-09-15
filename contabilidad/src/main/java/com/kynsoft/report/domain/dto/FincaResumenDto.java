package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class FincaResumenDto {
    private UUID id;
    private String code;
    private String name;
    private Double area;
    private String responsableNombre;
    private FincaEstadisticasDto estadisticas;

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    @Builder
    public static class FincaEstadisticasDto {
        private Long totalTrabajadores;
        private Long totalProductos;
        private Long totalProduccionMes;
        private Long totalSalidasMes;
        private Double stockTotalValorizado;
    }
}
