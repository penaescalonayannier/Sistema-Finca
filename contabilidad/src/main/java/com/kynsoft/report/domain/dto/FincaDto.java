package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class FincaDto {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private String direccion;
    private String telefono;
    private UUID responsableId;
    private Double area;
    private Boolean activo;

    // Campos derivados
    private String responsableNombre;
    private Long cantidadTrabajadores;
    private Long cantidadProductos;
}