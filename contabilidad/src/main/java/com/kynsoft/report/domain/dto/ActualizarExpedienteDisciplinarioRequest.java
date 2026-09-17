package com.kynsoft.report.domain.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class ActualizarExpedienteDisciplinarioRequest {
    private UUID aprobadorId;
    private TipoIncidenciaDisciplinaria tipo;
    private LocalDate fecha;
    private String descripcion;
    private String evidencia;
    private String observaciones;
}
