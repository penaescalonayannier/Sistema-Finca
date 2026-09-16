package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArqueoCajaDenominacionDto {
    private Integer denominacion;
    private Integer cantidadEsperada;
    private Integer cantidadFisica;
    private Integer diferenciaCantidad;
    private Double importeEsperado;
    private Double importeFisico;
    private Double diferenciaImporte;
}
