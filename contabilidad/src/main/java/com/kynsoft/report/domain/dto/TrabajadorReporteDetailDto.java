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
public class TrabajadorReporteDetailDto {
    private UUID id;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private String trabajadorRuc;
    private String trabajadorCuenta;
    private String trabajadorCargo;
    private UUID reporteId;
    private String reporteCodigo;
    private String reporteBloque;
    private String reporteCampo;
    private String reporteArea;
    private String reporteNorma;
    private String reporteFecha;
    private String reporteYear;
    private String reporteMes;
    private String norma;
    private String horas;
}