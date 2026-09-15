package com.kynsoft.report.applications.command.finca.update;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateFincaRequest {
    private String name;
    private String description;
    private String direccion;
    private String telefono;
    private UUID responsableId;
    private Double area;
}