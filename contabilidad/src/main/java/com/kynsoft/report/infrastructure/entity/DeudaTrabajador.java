package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.DeudaTrabajadorDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "deuda_trabajador")
public class DeudaTrabajador {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "trabajador_id")
    private UUID trabajadorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trabajador_id", insertable = false, updatable = false)
    private Trabajador trabajador;

    @Column(name = "importe")
    private Double importe;

    public DeudaTrabajador(DeudaTrabajadorDto dto) {
        this.id = dto.getId();
        this.trabajadorId = dto.getTrabajadorId();
        this.importe = dto.getImporte();
    }

    public DeudaTrabajadorDto toAggregate() {
        return DeudaTrabajadorDto.builder()
                .id(id)
                .trabajadorId(trabajadorId)
                .trabajadorNombre(trabajador != null ? trabajador.getNombre() : null)
                .trabajadorRuc(trabajador != null ? trabajador.getRuc() : null)
                .importe(importe)
                .build();
    }
}
