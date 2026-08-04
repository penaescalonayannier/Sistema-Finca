package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.DiaTrabajoDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "dia_trabajo")
public class DiaTrabajo {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporte_id", nullable = false)
    private Reporte reporte;

    @OneToMany(mappedBy = "diaTrabajo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrabajadorDia> trabajadores = new ArrayList<>();

    public DiaTrabajoDto toAggregate() {
        return DiaTrabajoDto.builder()
                .id(id)
                .fecha(fecha)
                .reporteId(reporte != null ? reporte.getId() : null)
                .build();
    }
}