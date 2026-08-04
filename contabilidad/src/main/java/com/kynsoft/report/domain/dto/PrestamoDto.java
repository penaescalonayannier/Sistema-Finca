package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import lombok.Builder;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class PrestamoDto {

    private UUID id;
    private Double importeAprobado;
    private Double importeAprobadoEfectivo;
    private Double importeUtilizadoEfectivo;
    private Double importeAprobadoSuministros;
    private Double importeUtilizadoSuministros;
    private Double importeAprobadoSeguro;
    private Double importeUtilizadoSeguro;
    private String numeroContrato;
    private String cuenta;
    private String toneladasMolibles;
    private String observaciones;
}
