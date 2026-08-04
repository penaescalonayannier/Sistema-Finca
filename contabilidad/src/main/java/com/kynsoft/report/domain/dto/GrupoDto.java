package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class GrupoDto {

    private UUID id;
    private String nombre;
    private String descripcion;
    private UUID jefeId;
    private TrabajadorDto jefe;
    private List<TrabajadorDto> trabajadores;
}
