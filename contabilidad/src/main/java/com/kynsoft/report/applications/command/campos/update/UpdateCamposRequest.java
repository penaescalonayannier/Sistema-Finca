package com.kynsoft.report.applications.command.campos.update;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCamposRequest {
    private UUID bloque;
    private UUID cepa;
    private UUID variedad;
    private String campo;
    private Double area;
    private Double poblacion;
    private String destino;
    private Double rendimiento;
}