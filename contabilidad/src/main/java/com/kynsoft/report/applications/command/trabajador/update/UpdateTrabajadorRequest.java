package com.kynsoft.report.applications.command.trabajador.update;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateTrabajadorRequest {
    // RUC no se puede modificar (RN-09)
    private String nombre;
    private String cuenta;
    private UUID fincaId;
    private UUID grupoId;
    private UUID cargoId;
    private UUID plazaId;
    private Boolean activo;
}
