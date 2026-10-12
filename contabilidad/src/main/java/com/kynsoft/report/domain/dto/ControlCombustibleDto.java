package com.kynsoft.report.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ControlCombustibleDto {
    private UUID fincaId;
    private String fincaNombre;
    private String fechaInicio;
    private String fechaFin;
    private List<Linea> lineas;

    @Getter
    @Setter
    @Builder
    public static class Linea {
        private UUID laborId;
        private String actividad;
        private String unidadMedida;
        private String nivelActividad;
        private Double indicePermisible;
        private Double nivelPeriodo;
        private Double consumoPeriodo;
        private Double indicePeriodo;
        private Double nivelAcumulado;
        private Double consumoAcumulado;
        private Double indiceAcumulado;
        private Double desviacion;
    }
}
