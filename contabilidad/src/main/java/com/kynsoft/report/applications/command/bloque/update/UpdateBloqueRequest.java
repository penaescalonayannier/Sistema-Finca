package com.kynsoft.report.applications.command.bloque.update;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBloqueRequest {
    private String code;
    private String name;
    private UUID fincaId;
}