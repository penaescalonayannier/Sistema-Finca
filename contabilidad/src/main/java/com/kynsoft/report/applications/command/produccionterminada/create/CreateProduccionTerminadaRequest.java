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
    private Integer cantidadTerminada;
    private UUID trabajadorEntregaId;
    private UUID trabajadorRecibeId;
    private String observaciones;
}
