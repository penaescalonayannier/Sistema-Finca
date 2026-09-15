package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.TrabajadorDto;
import org.hibernate.Hibernate;
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

    @Column(name = "ruc", unique = true, nullable = false, length = 11)
    private String ruc; // CI - 11 dígitos

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "cuenta", length = 50)
    private String cuenta;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "finca_id", nullable = false)
    private UUID fincaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finca_id", insertable = false, updatable = false)
    private Finca finca;

    @Column(name = "grupo_id", nullable = false)
    private UUID grupoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", insertable = false, updatable = false)
    private Grupo grupo;

    @Column(name = "cargo_id", nullable = false)
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
        this.fincaId = dto.getFincaId();
        this.grupoId = dto.getGrupoId();
        this.cargoId = dto.getCargoId();
    }

    public TrabajadorDto toAggregate() {
        String fCode = null;
        String fName = null;
        String gNombre = null;
        String cName = null;

        // Check if lazy-loaded entities are initialized before accessing
        if (finca != null && Hibernate.isInitialized(finca)) {
            fCode = finca.getCode();
            fName = finca.getName();
        }
        if (grupo != null && Hibernate.isInitialized(grupo)) {
            gNombre = grupo.getNombre();
        }
        if (cargo != null && Hibernate.isInitialized(cargo)) {
            cName = cargo.getName();
        }

        return TrabajadorDto
                .builder()
                .id(id)
                .nombre(nombre)
                .ruc(ruc)
                .cuenta(cuenta)
                .activo(activo)
                .fincaId(fincaId)
                .fincaCode(fCode)
                .fincaName(fName)
                .grupoId(grupoId)
                .grupoNombre(gNombre)
                .cargoId(cargoId)
                .cargoName(cName)
                .build();
    }
}
