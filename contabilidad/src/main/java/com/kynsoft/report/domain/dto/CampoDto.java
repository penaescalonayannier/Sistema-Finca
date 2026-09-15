package com.kynsoft.report.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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
    private Double valorAdquisicion;
    private Double depreciacionAcumulada;
    private Double valorResidual;
    private Double valorActual;
    private Integer anosCepa;
    private Double tasaDepreciacionAnual;
    private Integer vidaUtilAnios;
    private LocalDate fechaUltimaDepreciacion;
    private LocalDate fechaInicioDepreciacion;
}
