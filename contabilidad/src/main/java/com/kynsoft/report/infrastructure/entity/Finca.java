package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.FincaDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "fincas")
public class Finca {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "code", unique = true, nullable = false, length = 11)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "direccion", length = 200)
    private String direccion;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "responsable_id")
    private UUID responsableId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id", insertable = false, updatable = false)
    private Trabajador responsable;

    @Column(name = "area", nullable = false)
    private Double area;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public Finca(FincaDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.direccion = dto.getDireccion();
        this.telefono = dto.getTelefono();
        this.responsableId = dto.getResponsableId();
        this.area = dto.getArea();
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
    }

    public FincaDto toAggregate() {
        return FincaDto
                .builder()
                .id(id)
                .code(code)
                .name(name)
                .description(description)
                .direccion(direccion)
                .telefono(telefono)
                .responsableId(responsableId)
                .responsableNombre(responsable != null ? responsable.getNombre() : null)
                .area(area)
                .activo(activo)
                .build();
    }
}