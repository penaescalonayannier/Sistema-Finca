package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.ElementoPlanIngresoVentaDto;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "elemento_plan_ingreso_venta")
@Getter
@Setter
@NoArgsConstructor
public class ElementoPlanIngresoVenta {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "plan_ingreso_venta_id", nullable = false) private PlanIngresoVenta plan;
    @Column(nullable = false) private Integer orden;
    @Column(name = "producto_codigo_snapshot", length = 50) private String productoCodigoSnapshot;
    @Column(name = "producto_nombre_snapshot", nullable = false, length = 150) private String productoNombreSnapshot;
    @Column(name = "unidad_medida_snapshot", nullable = false, length = 50) private String unidadMedidaSnapshot;
    @Column(name = "tipo_precio_snapshot", length = 30) private String tipoPrecioSnapshot;
    @Column(name = "cantidad_planificada", nullable = false, precision = 19, scale = 4) private BigDecimal cantidadPlanificada;
    @Column(name = "precio_unitario_snapshot", nullable = false, precision = 19, scale = 4) private BigDecimal precioUnitarioSnapshot;
    @Column(name = "importe_planificado", nullable = false, precision = 19, scale = 4) private BigDecimal importePlanificado;
    public ElementoPlanIngresoVentaDto toAggregate() { return ElementoPlanIngresoVentaDto.builder().id(id).orden(orden)
            .productoCodigoSnapshot(productoCodigoSnapshot).productoNombreSnapshot(productoNombreSnapshot)
            .unidadMedidaSnapshot(unidadMedidaSnapshot).tipoPrecioSnapshot(tipoPrecioSnapshot)
            .cantidadPlanificada(cantidadPlanificada).precioUnitarioSnapshot(precioUnitarioSnapshot)
            .importePlanificado(importePlanificado).build(); }
}
