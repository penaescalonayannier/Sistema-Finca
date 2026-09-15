package com.kynsoft.report.applications.command.deudaTrabajador.create;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateDeudaTrabajadorRequest {
    private UUID trabajadorId;
    private Double importe;
}
