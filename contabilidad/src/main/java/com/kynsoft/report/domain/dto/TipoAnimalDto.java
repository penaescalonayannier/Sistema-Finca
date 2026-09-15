package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO para Tipo de Animal.
 * Nomenclador para subclasificación de reportes de Vaquería.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TipoAnimalDto {
    private UUID id;

    /**
     * Código único: VACA, OVEJA, CONEJO, CHIVO, CERDO
     */
    private String codigo;

    private String nombre;

    private String descripcion;

    private Boolean activo;

    private Integer orden;
}
