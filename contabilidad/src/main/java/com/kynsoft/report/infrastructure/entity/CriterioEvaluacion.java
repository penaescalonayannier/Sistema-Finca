package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.CriterioEvaluacionDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CriterioEvaluacion {

    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false)
    private Integer orden = 0;

    public CriterioEvaluacion(CriterioEvaluacionDto dto) {
        this.id = dto.getId();
        this.nombre = dto.getNombre();
        this.descripcion = dto.getDescripcion();
        this.activo = dto.getActivo() == null || dto.getActivo();
        this.orden = dto.getOrden() == null ? 0 : dto.getOrden();
    }

    public CriterioEvaluacionDto toAggregate() {
        return CriterioEvaluacionDto.builder().id(id).nombre(nombre).descripcion(descripcion)
                .activo(activo).orden(orden).build();
    }
}
