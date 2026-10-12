package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.GastoPlanMensualDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "gasto_plan_mensual")
@Getter @Setter @NoArgsConstructor
public class GastoPlanMensual {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "actividad_plan_mensual_id", nullable = false)
    private ActividadPlanMensual actividad;
    @Column(nullable = false) private Integer orden;
    @Column(nullable = false, length = 30) private String categoria;
    @Column(length = 30) private String subcategoria;
    @Column(nullable = false, length = 250) private String descripcion;
    @Column(name = "unidad_medida_snapshot", length = 50) private String unidadMedidaSnapshot;
    @Column(nullable = false, precision = 19, scale = 4) private BigDecimal cantidad;
    @Column(name = "precio_unitario_snapshot", nullable = false, precision = 19, scale = 4)
    private BigDecimal precioUnitarioSnapshot;
    @Column(name = "importe_planificado", nullable = false, precision = 19, scale = 4)
    private BigDecimal importePlanificado;
    @Column(length = 500) private String observacion;

    public GastoPlanMensualDto toAggregate() {
        return GastoPlanMensualDto.builder().id(id).orden(orden).categoria(categoria).subcategoria(subcategoria)
                .descripcion(descripcion).unidadMedidaSnapshot(unidadMedidaSnapshot).cantidad(cantidad)
                .precioUnitarioSnapshot(precioUnitarioSnapshot).importePlanificado(importePlanificado)
                .observacion(observacion).build();
    }
}
