package com.kynsoft.report.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import lombok.Builder;

@Getter
@Setter
@Builder
public class CampoDto {

    private UUID id;
    private BloqueDto bloque;
    private CepaDto cepa;
    private VariedadDto variedad;
    private String campo;
    private Double area;
    private Double poblacion;
    private String destino;
    private Double rendimiento;
}
