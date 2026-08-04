package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.CargoDto;
import com.kynsoft.report.domain.dto.enumerativos.TipoCargo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Cargo {

    @Id
    @Column(name = "id")
    private UUID id;

    private String name;

    private String description;

    @Column(name = "tipo_cargo")
    private String tipoCargo;

    @Column(name = "anticipo_diario")
    private BigDecimal anticipoDiario;

    @Column(name = "salario_escala")
    private BigDecimal salarioEscala;

    @Column(name = "taza")
    private BigDecimal taza;

    public Cargo(CargoDto dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.anticipoDiario = dto.getAnticipoDiario();
        this.salarioEscala = dto.getSalarioEscala();
        this.taza = dto.getTaza();
        this.tipoCargo = dto.getTipoCargo() != null ? dto.getTipoCargo().name() : null;
    }

    public CargoDto toAggregate() {
        return CargoDto
                .builder()
                .id(id)
                .name(name)
                .description(description)
                .anticipoDiario(anticipoDiario)
                .salarioEscala(salarioEscala)
                .taza(taza)
                .tipoCargo(this.tipoCargo != null ? TipoCargo.valueOf(this.tipoCargo) : null)
                .build();
    }
}
