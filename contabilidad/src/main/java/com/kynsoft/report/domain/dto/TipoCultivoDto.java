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
public class TipoCultivoDto {
    private UUID id;
    private String codigo;
    private String nombre;
    private String descripcion;
    /**
     * Categoría del cultivo: CANNA, VIANDA, OTRO
     */
    private CategoriaTipoCultivo categoria;
    private Boolean requiereCampo;
    private Boolean activo;
    private Integer orden;
}
