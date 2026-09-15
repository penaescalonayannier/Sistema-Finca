package com.kynsoft.report.applications.command.deudaTrabajador.update;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateDeudaTrabajadorRequest {
    private UUID id;
    private UUID trabajadorId;
    private Double importe;
}
