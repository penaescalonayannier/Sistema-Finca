package com.kynsoft.report.applications.command.bloque.create;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBloqueRequest {
    private String code;
    private String name;
    private UUID fincaId;
}