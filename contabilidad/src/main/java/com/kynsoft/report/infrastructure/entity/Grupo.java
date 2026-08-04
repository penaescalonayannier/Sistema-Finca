package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.GrupoDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Grupo {

    @Id
    @Column(name = "id")
    private UUID id;

    private String nombre;

    private String descripcion;

    @Column(name = "jefe_id")
    private UUID jefeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "jefe_id", insertable = false, updatable = false)
    private Trabajador jefe;

    @OneToMany(mappedBy = "grupo", fetch = FetchType.EAGER)
    private List<Trabajador> trabajadores;

    public Grupo(GrupoDto dto) {
        this.id = dto.getId();
        this.nombre = dto.getNombre();
        this.descripcion = dto.getDescripcion();
        this.jefeId = dto.getJefeId();
    }

    public GrupoDto toAggregate() {
        List<com.kynsoft.report.domain.dto.TrabajadorDto> trabajadorDtos =
            trabajadores != null ? trabajadores.stream()
                .map(Trabajador::toAggregate)
                .toList() : java.util.Collections.emptyList();

        com.kynsoft.report.domain.dto.TrabajadorDto jefeDto = jefe != null ? jefe.toAggregate() : null;

        return GrupoDto
                .builder()
                .id(id)
                .nombre(nombre)
                .descripcion(descripcion)
                .jefeId(jefeId)
                .jefe(jefeDto)
                .trabajadores(trabajadorDtos)
                .build();
    }
}
