package com.kynsoft.report.applications.command.diatrabajo.create;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateDiaTrabajoRequest {
    private LocalDate fecha;
}