package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.ActividadPlanMensualDto;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "actividad_plan_mensual")
@Getter @Setter @NoArgsConstructor
public class ActividadPlanMensual {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "plan_ingreso_venta_id", nullable = false) private PlanIngresoVenta plan;
    @Column(nullable = false) private Integer orden;
    @Column(name = "tipo_cultivo_id") private UUID tipoCultivoId;
    @Column(name = "cultivo_snapshot", length = 150) private String cultivoSnapshot;
    @Column(name = "bloque_snapshot", length = 100) private String bloqueSnapshot;
    @Column(name = "campo_snapshot", length = 100) private String campoSnapshot;
    @Column(name = "labor_agricola_id") private UUID laborAgricolaId;
    @Column(name = "labor_agricola_snapshot", length = 200) private String laborAgricolaSnapshot;
    @Column(name = "codigo_centro_costo", length = 30) private String codigoCentroCosto;
    @Column(name = "centro_costo_snapshot", length = 150) private String centroCostoSnapshot;
    @Column(name = "actividad_snapshot", nullable = false, length = 200) private String actividadSnapshot;
    @Column(name = "unidad_medida_snapshot", length = 50) private String unidadMedidaSnapshot;
    @Column(name = "area_planificada", precision = 19, scale = 4) private BigDecimal areaPlanificada;
    @Column(name = "cantidad_planificada", precision = 19, scale = 4) private BigDecimal cantidadPlanificada;
    @Column(name = "costo_salario", nullable = false, precision = 19, scale = 4) private BigDecimal costoSalario;
    @Column(nullable = false, precision = 19, scale = 4) private BigDecimal compras;
    @Column(nullable = false, precision = 19, scale = 4) private BigDecimal servicios;
    @Column(nullable = false, precision = 19, scale = 4) private BigDecimal total;
    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("cargoNombreSnapshot ASC") private Set<DotacionPlanMensual> dotaciones = new LinkedHashSet<>();
    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC") private Set<GastoPlanMensual> gastos = new LinkedHashSet<>();
    public ActividadPlanMensualDto toAggregate() { return ActividadPlanMensualDto.builder().id(id).orden(orden)
            .tipoCultivoId(tipoCultivoId).tipoCultivoNombreSnapshot(cultivoSnapshot).bloqueSnapshot(bloqueSnapshot).campoSnapshot(campoSnapshot)
            .laborAgricolaId(laborAgricolaId).laborAgricolaSnapshot(laborAgricolaSnapshot).codigoCentroCosto(codigoCentroCosto).centroCostoSnapshot(centroCostoSnapshot)
            .actividad(actividadSnapshot).unidadMedidaSnapshot(unidadMedidaSnapshot)
            .areaPlanificada(areaPlanificada).cantidadMeta(cantidadPlanificada).pagoSalario(costoSalario)
            .compras(compras).servicios(servicios).totalPlanificado(total)
            .dotaciones(dotaciones.stream().map(DotacionPlanMensual::toAggregate).toList())
            .gastos(gastos.stream().map(GastoPlanMensual::toAggregate).toList()).build(); }
}
