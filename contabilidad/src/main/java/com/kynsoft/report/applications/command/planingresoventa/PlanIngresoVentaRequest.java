package com.kynsoft.report.applications.command.planingresoventa;

import com.kynsoft.report.domain.dto.ElementoPlanIngresoVentaDto;
import com.kynsoft.report.domain.dto.PlanIngresoVentaDto;
import com.kynsoft.report.domain.dto.ActividadPlanMensualDto;
import com.kynsoft.report.domain.dto.DotacionPlanMensualDto;
import com.kynsoft.report.domain.dto.GastoPlanMensualDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/** Datos editables del plan. Los totales se calculan en el dominio, nunca desde el cliente. */
@Getter
@Setter
public class PlanIngresoVentaRequest {
    private UUID fincaId;
    private LocalDate periodo;
    private BigDecimal pagosSalario;
    private BigDecimal porcentajeAportePresupuesto;
    private BigDecimal pagosCompras;
    private BigDecimal comprasServicios;
    private String observaciones;
    private List<ElementoPlanIngresoVentaRequest> elementos;
    private List<ActividadPlanMensualRequest> actividades;

    public PlanIngresoVentaDto toDto(UUID id) {
        return PlanIngresoVentaDto.builder()
                .id(id)
                .fincaId(fincaId)
                .periodo(periodo)
                .pagosSalario(pagosSalario)
                .porcentajeAportePresupuesto(porcentajeAportePresupuesto)
                .pagosCompras(pagosCompras)
                .comprasServicios(comprasServicios)
                .observaciones(observaciones)
                .elementos(elementos == null ? List.of() : elementos.stream()
                        .map(ElementoPlanIngresoVentaRequest::toDto).toList())
                .actividades(actividades == null ? List.of() : actividades.stream()
                        .map(ActividadPlanMensualRequest::toDto).toList())
                .build();
    }

    @Getter
    @Setter
    public static class ElementoPlanIngresoVentaRequest {
        private String productoCodigoSnapshot;
        private String productoNombreSnapshot;
        private String unidadMedidaSnapshot;
        private String tipoPrecioSnapshot;
        private BigDecimal cantidadPlanificada;
        private BigDecimal precioUnitarioSnapshot;

        public ElementoPlanIngresoVentaDto toDto() {
            return ElementoPlanIngresoVentaDto.builder()
                    .productoCodigoSnapshot(productoCodigoSnapshot)
                    .productoNombreSnapshot(productoNombreSnapshot)
                    .unidadMedidaSnapshot(unidadMedidaSnapshot)
                    .tipoPrecioSnapshot(tipoPrecioSnapshot)
                    .cantidadPlanificada(cantidadPlanificada)
                    .precioUnitarioSnapshot(precioUnitarioSnapshot)
                    .build();
        }
    }

    @Getter @Setter
    public static class ActividadPlanMensualRequest {
        private UUID tipoCultivoId;
        private String tipoCultivoNombreSnapshot;
        private String bloqueSnapshot;
        private String campoSnapshot;
        private UUID laborAgricolaId;
        private String laborAgricolaSnapshot;
        private String codigoCentroCosto;
        private String centroCostoSnapshot;
        private String actividad;
        private String unidadMedidaSnapshot;
        private BigDecimal areaPlanificada;
        private BigDecimal cantidadMeta;
        private BigDecimal pagoSalario;
        private BigDecimal compras;
        private BigDecimal servicios;
        private List<DotacionPlanMensualRequest> dotaciones;
        private List<GastoPlanMensualRequest> gastos;
        public ActividadPlanMensualDto toDto() { return ActividadPlanMensualDto.builder()
                .tipoCultivoId(tipoCultivoId).tipoCultivoNombreSnapshot(tipoCultivoNombreSnapshot).bloqueSnapshot(bloqueSnapshot).campoSnapshot(campoSnapshot)
                .laborAgricolaId(laborAgricolaId).laborAgricolaSnapshot(laborAgricolaSnapshot)
                .codigoCentroCosto(codigoCentroCosto).centroCostoSnapshot(centroCostoSnapshot)
                .actividad(actividad).unidadMedidaSnapshot(unidadMedidaSnapshot)
                .areaPlanificada(areaPlanificada).cantidadMeta(cantidadMeta)
                .pagoSalario(pagoSalario).compras(compras).servicios(servicios)
                .dotaciones(dotaciones == null ? List.of() : dotaciones.stream().map(DotacionPlanMensualRequest::toDto).toList())
                .gastos(gastos == null ? List.of() : gastos.stream().map(GastoPlanMensualRequest::toDto).toList()).build(); }
    }

    @Getter @Setter
    public static class GastoPlanMensualRequest {
        private String categoria;
        private String subcategoria;
        private String descripcion;
        private String unidadMedidaSnapshot;
        private BigDecimal cantidad;
        private BigDecimal precioUnitarioSnapshot;
        private String observacion;
        public GastoPlanMensualDto toDto() { return GastoPlanMensualDto.builder().categoria(categoria)
                .subcategoria(subcategoria).descripcion(descripcion).unidadMedidaSnapshot(unidadMedidaSnapshot)
                .cantidad(cantidad).precioUnitarioSnapshot(precioUnitarioSnapshot).observacion(observacion).build(); }
    }

    @Getter @Setter
    public static class DotacionPlanMensualRequest {
        private UUID cargoId;
        private Integer cantidadTrabajadores;
        private BigDecimal diasPlanificados;
        private BigDecimal horasPorDia;
        public DotacionPlanMensualDto toDto() { return DotacionPlanMensualDto.builder().cargoId(cargoId)
                .cantidadTrabajadores(cantidadTrabajadores).diasPlanificados(diasPlanificados).horasPorDia(horasPorDia).build(); }
    }
}
