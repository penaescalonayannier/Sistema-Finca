package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TrabajadorConsolidadoDto {
    private String trabajadorId;
    private String nombre;
    private String ruc;
    private String cargo;
    private String cuenta;
    private Map<Integer, String> horasPorDia; // Día -> Horas (ej: "1" -> "8:00")
    private Double totalHoras;
}