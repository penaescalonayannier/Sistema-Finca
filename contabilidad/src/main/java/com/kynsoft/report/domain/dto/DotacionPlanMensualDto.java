package com.kynsoft.report.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** Dotación prevista para una actividad; el salario se congela al crear el plan. */
@Getter
@Setter
@Builder
public class DotacionPlanMensualDto {
    private UUID id;
    private UUID cargoId;
    private String cargoNombreSnapshot;
    private BigDecimal salarioEscalaSnapshot;
    private Integer cantidadTrabajadores;
    private BigDecimal diasPlanificados;
    private BigDecimal horasPorDia;
    private BigDecimal salarioPlanificado;
}
