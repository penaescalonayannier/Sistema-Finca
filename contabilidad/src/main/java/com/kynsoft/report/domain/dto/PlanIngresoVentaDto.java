package com.kynsoft.report.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlanIngresoVentaDto {
    private UUID id;
    private UUID fincaId;
    private LocalDate periodo;
    private EstadoPlanIngresoVenta estado;
    private BigDecimal pagosSalario;
    private BigDecimal porcentajeAportePresupuesto;
    private BigDecimal aportePresupuesto;
    private BigDecimal pagosCompras;
    private BigDecimal comprasServicios;
    private BigDecimal totalIngresosPlanificados;
    private BigDecimal totalEgresos;
    private BigDecimal disponible;
    private String observaciones;
    private List<ElementoPlanIngresoVentaDto> elementos;
    private List<ActividadPlanMensualDto> actividades;
}
