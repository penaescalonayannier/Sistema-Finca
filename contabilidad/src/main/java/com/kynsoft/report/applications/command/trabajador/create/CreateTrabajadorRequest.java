package com.kynsoft.report.applications.command.trabajador.create;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateTrabajadorRequest {

    private String ruc;
    private String nombre;
    private String cuenta;
    private UUID fincaId;
    private UUID grupoId;
    private UUID cargoId;
    private UUID plazaId;
}
