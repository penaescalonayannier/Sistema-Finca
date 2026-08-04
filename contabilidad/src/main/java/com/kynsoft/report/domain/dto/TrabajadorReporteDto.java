package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import lombok.Builder;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TrabajadorReporteDto {
    private UUID id;
    private UUID trabajador;
    private UUID reporte;
    private String norma;
    private String horas;
}
