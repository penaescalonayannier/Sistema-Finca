package com.kynsoft.report.applications.command.report.estadoCuenta.create;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateEstadoCuentaRequest {

    private String fecha;
    private String refOrigen;
    private String refCorriente;
    private String observaciones;
    private String type;
    private Double importe;
    private UUID clienteId;
}
