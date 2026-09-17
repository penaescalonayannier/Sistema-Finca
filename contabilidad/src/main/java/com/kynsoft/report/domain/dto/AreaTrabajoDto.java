package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AreaTrabajoDto {
    private UUID id;
    private UUID fincaId;
    private UUID areaPadreId;
    private UUID responsableId;
    private String codigo;
    private String nombre;
    private String descripcion;
    private TipoAreaTrabajo tipo;
    private Boolean activo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
