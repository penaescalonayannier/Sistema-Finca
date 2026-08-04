package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.TrabajadorDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Trabajador {

    @Id
    @Column(name = "id")
    private UUID id;
    private String ruc; // CI
    private String nombre; // NOMBRE
    private String cuenta; // CUENTA_E
    @Column(name = "activo")
    private Boolean activo = true; // Default: activo

    @Column(name = "grupo_id")
    private UUID grupoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", insertable = false, updatable = false)
    private Grupo grupo;

    @Column(name = "cargo_id")
    private UUID cargoId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cargo_id", insertable = false, updatable = false)
    private Cargo cargo;

    public Trabajador(TrabajadorDto dto) {
        this.id = dto.getId();
        this.nombre = dto.getNombre();
        this.ruc = dto.getRuc();
        this.cuenta = dto.getCuenta();
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
        this.grupoId = dto.getGrupoId();
        this.cargoId = dto.getCargoId();
    }

    public TrabajadorDto toAggregate() {
        return TrabajadorDto
                .builder()
                .id(id)
                .nombre(nombre)
                .ruc(ruc)
                .cuenta(cuenta)
                .cargoId(cargoId)
                .cargoName(cargo != null ? cargo.getName() : null)
                .activo(activo)
                .grupoId(grupoId)
                .build();
    }
}
