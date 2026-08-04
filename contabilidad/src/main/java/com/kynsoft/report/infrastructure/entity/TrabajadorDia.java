package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.TrabajadorDiaDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "trabajador_dia")
public class TrabajadorDia {
    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dia_trabajo_id", nullable = false)
    private DiaTrabajo diaTrabajo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_id", nullable = false)
    private Trabajador trabajador;

    @Column(name = "horas", nullable = false)
    private String horas;

    @Column(name = "norma")
    private String norma;

    public TrabajadorDiaDto toAggregate() {
        return TrabajadorDiaDto.builder()
                .id(id)
                .diaTrabajoId(diaTrabajo != null ? diaTrabajo.getId() : null)
                .trabajadorId(trabajador != null ? trabajador.getId() : null)
                .horas(horas)
                .norma(norma)
                .build();
    }
}