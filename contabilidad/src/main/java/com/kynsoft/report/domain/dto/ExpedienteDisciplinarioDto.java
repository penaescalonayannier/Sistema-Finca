package com.kynsoft.report.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpedienteDisciplinarioDto {
    private UUID id;
    private UUID fincaId;
    private String fincaNombre;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private UUID aprobadorId;
    private String aprobadorNombre;
    private TipoIncidenciaDisciplinaria tipo;
    private EstadoExpedienteDisciplinario estado;
    private LocalDate fecha;
    private String descripcion;
    private String evidencia;
    private String observaciones;
    private String medida;
    private String resolucion;
    private LocalDateTime fechaNotificacion;
    private LocalDateTime fechaResolucion;
    private LocalDateTime fechaAnulacion;
    private String motivoAnulacion;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
