package com.kynsoft.report.applications.command.cliente.create;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClienteRequest {

    private String cuenta;
    private String nombre;
    private String ruc;
    private String direccion;
}
