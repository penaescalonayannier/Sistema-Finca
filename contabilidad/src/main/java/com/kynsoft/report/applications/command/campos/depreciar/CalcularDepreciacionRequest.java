package com.kynsoft.report.applications.command.campos.depreciar;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CalcularDepreciacionRequest {
    private List<UUID> campoIds;
    private Integer meses;
}
