package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PlazaDto {
    private UUID id;
    private UUID fincaId;
    private UUID areaId;
    private UUID grupoId;
    private UUID cargoId;
    private UUID responsableId;
    private String codigo;
    private String nombre;
    private Boolean activo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String observaciones;
    private UUID trabajadorId;
    private String trabajadorNombre;

    /** Estado calculado para no persistir datos derivados. */
    public String getEstado() {
        if (!Boolean.TRUE.equals(activo)) return "INACTIVA";
        return trabajadorId == null ? "VACANTE" : "OCUPADA";
    }
}
