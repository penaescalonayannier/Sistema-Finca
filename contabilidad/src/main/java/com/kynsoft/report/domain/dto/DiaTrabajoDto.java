package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DiaTrabajoDto {
    private UUID id;
    private LocalDate fecha;
    private UUID reporteId;
    private List<TrabajadorDiaDto> trabajadores;
}