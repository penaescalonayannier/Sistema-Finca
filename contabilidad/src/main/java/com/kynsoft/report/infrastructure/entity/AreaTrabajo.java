package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.AreaTrabajoDto;
import com.kynsoft.report.domain.dto.TipoAreaTrabajo;
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
@Table(name = "area_trabajo")
@Getter @Setter @NoArgsConstructor
public class AreaTrabajo {
    @Id private UUID id;
    @Column(name = "finca_id", nullable = false) private UUID fincaId;
    @Column(name = "area_padre_id") private UUID areaPadreId;
    @Column(name = "responsable_id") private UUID responsableId;
    @Column(nullable = false) private String codigo;
    @Column(nullable = false) private String nombre;
    private String descripcion;
    @Column(nullable = false) private String tipo;
    @Column(nullable = false) private Boolean activo = true;
    @Column(name = "fecha_inicio") private LocalDate fechaInicio;
    @Column(name = "fecha_fin") private LocalDate fechaFin;

    public AreaTrabajo(AreaTrabajoDto dto) { aplicar(dto); }
    public void aplicar(AreaTrabajoDto dto) {
        this.id = dto.getId(); this.fincaId = dto.getFincaId(); this.areaPadreId = dto.getAreaPadreId();
        this.responsableId = dto.getResponsableId(); this.codigo = dto.getCodigo(); this.nombre = dto.getNombre();
        this.descripcion = dto.getDescripcion(); this.tipo = dto.getTipo().name();
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
        this.fechaInicio = dto.getFechaInicio(); this.fechaFin = dto.getFechaFin();
    }
    public AreaTrabajoDto toAggregate() {
        return AreaTrabajoDto.builder().id(id).fincaId(fincaId).areaPadreId(areaPadreId).responsableId(responsableId)
                .codigo(codigo).nombre(nombre).descripcion(descripcion).tipo(TipoAreaTrabajo.valueOf(tipo))
                .activo(activo).fechaInicio(fechaInicio).fechaFin(fechaFin).build();
    }
}
