package com.kynsoft.report.applications.command.trabajador.create;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTrabajadorRequest {

    private String ruc;
    private String nombre;
    private String cuenta;
    private Boolean activo; // Default: true
}
