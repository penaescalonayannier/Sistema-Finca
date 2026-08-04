package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class HombreActividadAgricolaImporteDto {

    private UUID id;
    private TrabajadorDto trabajador;
    private LaborDto labor;
    private InstrumentoTrabajoDto instrumento;
    private BloqueDto bloque;
    private CampoDto campo;
    private BigDecimal dias;
    private BigDecimal horas;
    private BigDecimal norma;
    private BigDecimal tasa;
    private BigDecimal importe;
}