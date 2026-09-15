package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.TipoAnimalDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Nomenclador de tipos de animal.
 * Para subclasificación de reportes de Vaquería.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tipo_animal")
public class TipoAnimal {

    @Id
    @Column(name = "id")
    private UUID id;

    /**
     * Código único: VACA, OVEJA, CONEJO, CHIVO, CERDO
     */
    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "orden")
    private Integer orden;

    public TipoAnimal(TipoAnimalDto dto) {
        this.id = dto.getId();
        this.codigo = dto.getCodigo();
        this.nombre = dto.getNombre();
        this.descripcion = dto.getDescripcion();
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
        this.orden = dto.getOrden();
    }

    public TipoAnimalDto toAggregate() {
        return TipoAnimalDto.builder()
                .id(id)
                .codigo(codigo)
                .nombre(nombre)
                .descripcion(descripcion)
                .activo(activo)
                .orden(orden)
                .build();
    }
}
