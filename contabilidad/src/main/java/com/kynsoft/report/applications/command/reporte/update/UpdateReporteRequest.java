package com.kynsoft.report.applications.command.reporte.update;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class UpdateReporteRequest {
    private String bloque;
    private String campo;
    private String area;
    private String norma;
    private String fecha;
    private String codigo;
    private String year;
    private String mes;
    private UUID trabajadorResponsableId;
}