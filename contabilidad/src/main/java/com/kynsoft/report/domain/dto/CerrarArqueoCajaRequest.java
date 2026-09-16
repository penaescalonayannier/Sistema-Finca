package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CerrarArqueoCajaRequest {
    private String observaciones;
    private List<DenominacionCajaDto> conteoFisico;
}
