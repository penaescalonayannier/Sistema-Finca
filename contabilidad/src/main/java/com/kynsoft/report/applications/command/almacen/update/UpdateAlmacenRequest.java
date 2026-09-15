package com.kynsoft.report.applications.command.almacen.update;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateAlmacenRequest {
    private String nombre;
    private String inventario;
    private UUID fincaId;
}
