package com.kynsoft.report.applications.command.hombreactividadagricolaimporte.update;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class UpdateHombreActividadAgricolaImporteRequest {
    private UUID trabajador;
    private UUID labor;
    private UUID instrumento;
    private UUID bloque;
    private UUID campo;
    private BigDecimal dias;
    private BigDecimal horas;
    private BigDecimal norma;
    private BigDecimal tasa;
    private BigDecimal importe;
}