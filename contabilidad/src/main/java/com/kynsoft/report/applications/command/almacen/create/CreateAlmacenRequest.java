package com.kynsoft.report.applications.command.almacen.create;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateAlmacenRequest {
    private String nombre;
    private String descripcion;
    private UUID fincaId;
}
