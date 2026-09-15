package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.GrupoActivoFijoDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Grupos de Activos Fijos Tangibles según clasificación contable cubana.
 *
 * Referencia normativa:
 * - Resolución No. 1038/2017 del MFP: Aprueba NCC No. 7 "Activos Fijos Tangibles"
 * - Resolución No. 51/2021 del MFP: Tasas máximas anuales de depreciación
 *
 * Grupos estándar:
 * 01 - Edificios
 * 02 - Otras Construcciones
 * 04 - Máquinas y Equipos Productivos
 * 05 - Aparatos
 * 07 - Muebles y Otros Objetos
 * 08 - Animales
 * 12 - Plantaciones de Caña
 * 13 - Plantaciones Permanentes (frutales)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "grupo_activo_fijo")
public class GrupoActivoFijo {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "codigo", nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /**
     * Tasa de depreciación anual según Resolución 51/2021 MFP.
     * Puede ser null para grupos con depreciación variable (animales, plantaciones).
     */
    @Column(name = "tasa_depreciacion", precision = 5, scale = 2)
    private BigDecimal tasaDepreciacion;

    /**
     * Vida útil en años según NCC No. 7.
     * Puede ser null para grupos con vida útil variable.
     */
    @Column(name = "vida_util_anios")
    private Integer vidaUtilAnios;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "activo")
    private Boolean activo = true;

    public GrupoActivoFijo(GrupoActivoFijoDto dto) {
        this.id = dto.getId();
        this.codigo = dto.getCodigo();
        this.nombre = dto.getNombre();
        this.tasaDepreciacion = dto.getTasaDepreciacion();
        this.vidaUtilAnios = dto.getVidaUtilAnios();
        this.descripcion = dto.getDescripcion();
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
    }

    public GrupoActivoFijoDto toAggregate() {
        return GrupoActivoFijoDto.builder()
                .id(id)
                .codigo(codigo)
                .nombre(nombre)
                .tasaDepreciacion(tasaDepreciacion)
                .vidaUtilAnios(vidaUtilAnios)
                .descripcion(descripcion)
                .activo(activo)
                .build();
    }
}
