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
public class TrabajadorDiaDto {
    private UUID id;
    private UUID diaTrabajoId;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private String trabajadorRuc;
    private String horas;
    private String norma;
}