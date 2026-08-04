package com.kynsoft.report.applications.command.cliente.update;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateClienteRequest {

    private String cuenta;
    private String nombre;
    private String ruc;
    private String direccion;
}
