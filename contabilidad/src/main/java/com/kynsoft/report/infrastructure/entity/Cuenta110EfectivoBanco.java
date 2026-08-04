package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.Cuenta110EfectivoBancoDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class Cuenta110EfectivoBanco {

    @Id
    @Column(name = "id")
    private UUID id;
    private String observaciones;
    private Double importe;

    public Cuenta110EfectivoBanco(Cuenta110EfectivoBancoDto cuenta) {
        this.id = cuenta.getId();
        this.observaciones = cuenta.getObservaciones();
        this.importe = cuenta.getImporte();
    }

    public Cuenta110EfectivoBancoDto toAggregate() {
        return Cuenta110EfectivoBancoDto
                .builder()
                .id(id)
                .observaciones(observaciones)
                .importe(importe)
                .build();
    }
}
