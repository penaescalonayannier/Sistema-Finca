package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.report.domain.dto.ElementoPlanIngresoVentaDto;
import com.kynsoft.report.domain.dto.EstadoPlanIngresoVenta;
import com.kynsoft.report.domain.dto.PlanIngresoVentaDto;
import com.kynsoft.report.domain.dto.ActividadPlanMensualDto;
import com.kynsoft.share.core.domain.bus.query.IResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlanIngresoVentaResponse implements IResponse {
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

    public PlanIngresoVentaResponse(PlanIngresoVentaDto dto) {
        id = dto.getId(); fincaId = dto.getFincaId(); periodo = dto.getPeriodo(); estado = dto.getEstado();
        pagosSalario = dto.getPagosSalario(); porcentajeAportePresupuesto = dto.getPorcentajeAportePresupuesto();
        aportePresupuesto = dto.getAportePresupuesto(); pagosCompras = dto.getPagosCompras();
        comprasServicios = dto.getComprasServicios(); totalIngresosPlanificados = dto.getTotalIngresosPlanificados();
        totalEgresos = dto.getTotalEgresos(); disponible = dto.getDisponible(); observaciones = dto.getObservaciones();
        elementos = dto.getElementos();
        actividades = dto.getActividades();
    }
}
