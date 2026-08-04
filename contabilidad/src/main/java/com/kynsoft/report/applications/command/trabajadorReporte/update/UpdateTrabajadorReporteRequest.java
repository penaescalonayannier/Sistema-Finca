package com.kynsoft.report.applications.command.trabajadorReporte.update;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTrabajadorReporteRequest {
    private UUID id;
    private String norma;
    private String horas;
}