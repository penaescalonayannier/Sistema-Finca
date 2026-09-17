package com.kynsoft.report.domain.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class ResolverExpedienteDisciplinarioRequest {
    private UUID aprobadorId;
    private String medida;
    private String resolucion;
    private String observaciones;
}
