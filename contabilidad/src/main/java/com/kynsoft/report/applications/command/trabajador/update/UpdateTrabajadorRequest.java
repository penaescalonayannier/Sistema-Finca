package com.kynsoft.report.applications.command.trabajador.update;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTrabajadorRequest {

    private String ruc;
    private String nombre;
    private String cuenta;
    private Boolean activo;
}