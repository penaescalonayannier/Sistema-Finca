package com.kynsoft.report.applications.command.finca.create;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFincaRequest {
    private String code;
    private String name;
    private String description;
}