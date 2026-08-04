package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.ClienteDto;
import com.kynsoft.report.domain.dto.EstadoCuentaDto;
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
public class Cliente {

    @Id
    @Column(name = "id")
    private UUID id;
    private String cuenta;
    private String nombre;
    private String ruc;
    private String direccion;

    public Cliente(ClienteDto dto) {
        this.id = dto.getId();
        this.cuenta = dto.getCuenta();
        this.nombre = dto.getNombre();
        this.ruc = dto.getRuc();
        this.direccion = dto.getDireccion();
    }

    public ClienteDto toAggregate() {
        return ClienteDto
                .builder()
                .id(id)
                .cuenta(cuenta)
                .nombre(nombre)
                .ruc(ruc)
                .direccion(direccion)
                .build();
    }
}
