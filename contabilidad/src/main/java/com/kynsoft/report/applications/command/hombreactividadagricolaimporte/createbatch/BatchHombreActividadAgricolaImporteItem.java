package com.kynsoft.report.applications.command.hombreactividadagricolaimporte.createbatch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchHombreActividadAgricolaImporteItem {
    private UUID id;
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