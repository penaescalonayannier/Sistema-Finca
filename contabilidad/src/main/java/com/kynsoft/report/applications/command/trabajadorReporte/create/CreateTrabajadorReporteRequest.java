package com.kynsoft.report.applications.command.trabajadorReporte.create;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTrabajadorReporteRequest {
    private UUID trabajador;
    private UUID reporte;
    private String norma;
    private String horas;
}