package com.kynsoft.report.applications.command.produccionterminada.update;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateProduccionTerminadaRequest {
    private Double cantidadTerminada;

    public void setCantidadTerminada(Number cantidadTerminada) {
        this.cantidadTerminada = cantidadTerminada != null ? cantidadTerminada.doubleValue() : null;
    }
    private UUID trabajadorEntregaId;
    private UUID trabajadorRecibeId;
    private String observaciones;
    private Double costoUnitario;
    private String lote;
    private String centroCosto;
}
