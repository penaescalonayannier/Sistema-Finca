package com.kynsoft.report.applications.command.produccionterminada.create;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateProduccionTerminadaRequest {
    private UUID fincaId;
    private UUID productoId;
    private LocalDateTime fecha;
    private Double cantidadTerminada;
    private UUID almacenFincaProductoId;

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
