package com.kynsoft.report.applications.command.trabajadordia.create;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateTrabajadorDiaRequest {
    private UUID trabajadorId;
    private String horas;
    private String norma;
}