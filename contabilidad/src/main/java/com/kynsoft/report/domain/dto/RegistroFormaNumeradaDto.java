package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroFormaNumeradaDto {
    /** Alias compatible para reportes previos que esperan el tipo documental. */
    private String tipo;
    private String prefijo;
    private Integer anio;
    private Integer ultimoNumero;
    private Integer proximoNumero;
    private Integer cantidadDocumentos;
    private Boolean integridad;
    private String formaCodigo;
    private String formaNombre;
    /** Identificador legible de la serie (prefijo y período cuando aplique). */
    private String serie;
    private String alcanceDescripcion;
    private String estado;
}
