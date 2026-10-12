package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.EstadoPlanIngresoVenta;
import com.kynsoft.report.domain.dto.PlanIngresoVentaDto;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plan_ingreso_venta")
@Getter
@Setter
@NoArgsConstructor
public class PlanIngresoVenta {
    @Id private UUID id;
    @Column(name = "finca_id", nullable = false) private UUID fincaId;
    @Column(nullable = false) private LocalDate periodo;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private EstadoPlanIngresoVenta estado;
    @Column(name = "pagos_salario", nullable = false, precision = 19, scale = 4) private BigDecimal pagosSalario;
    @Column(name = "porcentaje_aporte_presupuesto", nullable = false, precision = 7, scale = 4) private BigDecimal porcentajeAportePresupuesto;
    @Column(name = "aporte_presupuesto", nullable = false, precision = 19, scale = 4) private BigDecimal aportePresupuesto;
    @Column(name = "pagos_compras", nullable = false, precision = 19, scale = 4) private BigDecimal pagosCompras;
    @Column(name = "compras_servicios", nullable = false, precision = 19, scale = 4) private BigDecimal comprasServicios;
    @Column(name = "total_ingresos_planificados", nullable = false, precision = 19, scale = 4) private BigDecimal totalIngresosPlanificados;
    @Column(name = "total_egresos", nullable = false, precision = 19, scale = 4) private BigDecimal totalEgresos;
    @Column(nullable = false, precision = 19, scale = 4) private BigDecimal disponible;
    @Column(length = 1000) private String observaciones;
    @Column(name = "creado_por_id") private UUID creadoPorId;
    @Column(name = "creado_en", nullable = false) private OffsetDateTime creadoEn;
    @Column(name = "actualizado_en", nullable = false) private OffsetDateTime actualizadoEn;
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC") private List<ElementoPlanIngresoVenta> elementos = new ArrayList<>();
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC") private Set<ActividadPlanMensual> actividades = new LinkedHashSet<>();

    public PlanIngresoVentaDto toAggregate() {
        return PlanIngresoVentaDto.builder().id(id).fincaId(fincaId).periodo(periodo).estado(estado)
                .pagosSalario(pagosSalario).porcentajeAportePresupuesto(porcentajeAportePresupuesto)
                .aportePresupuesto(aportePresupuesto).pagosCompras(pagosCompras).comprasServicios(comprasServicios)
                .totalIngresosPlanificados(totalIngresosPlanificados).totalEgresos(totalEgresos).disponible(disponible)
                .observaciones(observaciones).elementos(elementos.stream()
                        .collect(Collectors.toMap(ElementoPlanIngresoVenta::getId, Function.identity(),
                                (primero, repetido) -> primero, LinkedHashMap::new))
                        .values().stream().map(ElementoPlanIngresoVenta::toAggregate).toList())
                .actividades(actividades.stream().map(ActividadPlanMensual::toAggregate).toList()).build();
    }
}
