package com.kynsoft.report.applications.command.prestamo.update;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePrestamoRequest {

    private Double importeAprobado;
    private Double importeAprobadoEfectivo;
    private Double importeUtilizadoEfectivo;
    private Double importeAprobadoSuministros;
    private Double importeUtilizadoSuministros;
    private Double importeAprobadoSeguro;
    private Double importeUtilizadoSeguro;
    private String numeroContrato;
    private String cuenta;
    private String observaciones;
    private String toneladasMolibles;
}
