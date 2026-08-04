package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.TrabajadorReporteDto;
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
@Table(name = "trabajador_reporte")
public class TrabajadorReporte {
    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_id", nullable = false)
    private Trabajador trabajador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporte_id", nullable = false)
    private Reporte reporte;

    @Column(name = "norma", nullable = false)
    private String norma;

    @Column(name = "horas", nullable = false)
    private String horas;

    public TrabajadorReporte(TrabajadorReporteDto dto) {
        this.id = dto.getId();
        this.norma = dto.getNorma();
        this.horas = dto.getHoras();
    }

    public TrabajadorReporteDto toAggregate() {
        return TrabajadorReporteDto.builder()
                .id(id)
                .trabajador(trabajador != null ? trabajador.getId() : null)
                .reporte(reporte != null ? reporte.getId() : null)
                .norma(norma)
                .horas(horas)
                .build();
    }
}