package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.PlazaDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "plaza")
@Getter @Setter @NoArgsConstructor
public class Plaza {
    @Id private UUID id;
    @Column(name = "finca_id", nullable = false) private UUID fincaId;
    @Column(name = "area_id") private UUID areaId;
    @Column(name = "grupo_id") private UUID grupoId;
    @Column(name = "cargo_id", nullable = false) private UUID cargoId;
    @Column(name = "responsable_id") private UUID responsableId;
    @Column(nullable = false) private String codigo;
    private String nombre;
    @Column(nullable = false) private Boolean activo = true;
    @Column(name = "fecha_inicio") private LocalDate fechaInicio;
    @Column(name = "fecha_fin") private LocalDate fechaFin;
    private String observaciones;
    public Plaza(PlazaDto dto) { aplicar(dto); }
    public void aplicar(PlazaDto dto) {
        this.id = dto.getId(); this.fincaId = dto.getFincaId(); this.areaId = dto.getAreaId(); this.grupoId = dto.getGrupoId();
        this.cargoId = dto.getCargoId(); this.responsableId = dto.getResponsableId(); this.codigo = dto.getCodigo();
        this.nombre = dto.getNombre(); this.activo = dto.getActivo() != null ? dto.getActivo() : true;
        this.fechaInicio = dto.getFechaInicio(); this.fechaFin = dto.getFechaFin(); this.observaciones = dto.getObservaciones();
    }
    public PlazaDto toAggregate() {
        return PlazaDto.builder().id(id).fincaId(fincaId).areaId(areaId).grupoId(grupoId).cargoId(cargoId)
                .responsableId(responsableId).codigo(codigo).nombre(nombre).activo(activo).fechaInicio(fechaInicio)
                .fechaFin(fechaFin).observaciones(observaciones).build();
    }
}
