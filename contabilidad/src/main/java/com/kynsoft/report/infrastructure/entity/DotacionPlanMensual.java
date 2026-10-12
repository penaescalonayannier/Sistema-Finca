package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.DotacionPlanMensualDto;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dotacion_plan_mensual")
@Getter @Setter @NoArgsConstructor
public class DotacionPlanMensual {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "actividad_plan_mensual_id", nullable = false)
    private ActividadPlanMensual actividad;
    @Column(name = "cargo_id", nullable = false) private UUID cargoId;
    @Column(name = "cargo_nombre_snapshot", nullable = false, length = 150) private String cargoNombreSnapshot;
    @Column(name = "salario_escala_snapshot", nullable = false, precision = 19, scale = 4)
    private BigDecimal salarioEscalaSnapshot;
    @Column(name = "cantidad_trabajadores", nullable = false) private Integer cantidadTrabajadores;
    @Column(name = "dias_planificados", nullable = false, precision = 19, scale = 4) private BigDecimal diasPlanificados;
    @Column(name = "horas_por_dia", nullable = false, precision = 19, scale = 4) private BigDecimal horasPorDia;
    @Column(name = "salario_planificado", nullable = false, precision = 19, scale = 4) private BigDecimal salarioPlanificado;

    public DotacionPlanMensualDto toAggregate() {
        return DotacionPlanMensualDto.builder().id(id).cargoId(cargoId).cargoNombreSnapshot(cargoNombreSnapshot)
                .salarioEscalaSnapshot(salarioEscalaSnapshot).cantidadTrabajadores(cantidadTrabajadores)
                .diasPlanificados(diasPlanificados).horasPorDia(horasPorDia).salarioPlanificado(salarioPlanificado).build();
    }
}
