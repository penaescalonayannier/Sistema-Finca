package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.applications.query.responseObject.PlanIngresoVentaResponse;
import com.kynsoft.report.domain.dto.ElementoPlanIngresoVentaDto;
import com.kynsoft.report.domain.dto.ActividadPlanMensualDto;
import com.kynsoft.report.domain.dto.DotacionPlanMensualDto;
import com.kynsoft.report.domain.dto.GastoPlanMensualDto;
import com.kynsoft.report.domain.dto.EstadoPlanIngresoVenta;
import com.kynsoft.report.domain.dto.PlanIngresoVentaDto;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.services.IPlanIngresoVentaService;
import com.kynsoft.report.infrastructure.entity.ElementoPlanIngresoVenta;
import com.kynsoft.report.infrastructure.entity.PlanIngresoVenta;
import com.kynsoft.report.infrastructure.entity.Cargo;
import com.kynsoft.report.infrastructure.entity.DotacionPlanMensual;
import com.kynsoft.report.infrastructure.entity.GastoPlanMensual;
import com.kynsoft.report.infrastructure.entity.CuentaContable;
import com.kynsoft.report.infrastructure.entity.Labor;
import com.kynsoft.report.infrastructure.repository.command.PlanIngresoVentaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.PlanIngresoVentaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ReporteReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CargoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CuentaContableReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.LaborReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantValidator;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gestiona la planificación mensual sin crear movimientos de inventario, ventas ni asientos.
 * Cada detalle es una fotografía del producto y precio vigentes al momento de planificar.
 */
@Service
@Transactional
public class PlanIngresoVentaServiceImpl implements IPlanIngresoVentaService {
    private static final BigDecimal CERO = BigDecimal.ZERO.setScale(4);
    private static final BigDecimal CIEN = new BigDecimal("100");

    private final PlanIngresoVentaWriteDataJPARepository write;
    private final PlanIngresoVentaReadDataJPARepository read;
    private final FincaReadDataJPARepository fincas;
    private final AuditoriaTransaccionalService auditoria;
    private final ReporteReadDataJPARepository reportes;
    private final CargoReadDataJPARepository cargos;
    private final CuentaContableReadDataJPARepository cuentasContables;
    private final LaborReadDataJPARepository labores;

    public PlanIngresoVentaServiceImpl(PlanIngresoVentaWriteDataJPARepository write,
            PlanIngresoVentaReadDataJPARepository read, FincaReadDataJPARepository fincas,
            AuditoriaTransaccionalService auditoria) {
        this(write, read, fincas, auditoria, null, null, null, null);
    }
    public PlanIngresoVentaServiceImpl(PlanIngresoVentaWriteDataJPARepository write,
            PlanIngresoVentaReadDataJPARepository read, FincaReadDataJPARepository fincas,
            AuditoriaTransaccionalService auditoria, ReporteReadDataJPARepository reportes) {
        this(write, read, fincas, auditoria, reportes, null, null, null);
    }
    public PlanIngresoVentaServiceImpl(PlanIngresoVentaWriteDataJPARepository write,
            PlanIngresoVentaReadDataJPARepository read, FincaReadDataJPARepository fincas,
            AuditoriaTransaccionalService auditoria, ReporteReadDataJPARepository reportes,
            CargoReadDataJPARepository cargos) {
        this(write, read, fincas, auditoria, reportes, cargos, null, null);
    }
    @Autowired
    public PlanIngresoVentaServiceImpl(PlanIngresoVentaWriteDataJPARepository write,
            PlanIngresoVentaReadDataJPARepository read, FincaReadDataJPARepository fincas,
            AuditoriaTransaccionalService auditoria, ReporteReadDataJPARepository reportes,
            CargoReadDataJPARepository cargos, CuentaContableReadDataJPARepository cuentasContables,
            LaborReadDataJPARepository labores) {
        this.write = write;
        this.read = read;
        this.fincas = fincas;
        this.auditoria = auditoria;
        this.reportes = reportes;
        this.cargos = cargos;
        this.cuentasContables = cuentasContables;
        this.labores = labores;
    }

    @Override
    public void create(PlanIngresoVentaDto solicitud) {
        validarSolicitud(solicitud);
        UUID fincaId = resolverYValidarFinca(solicitud.getFincaId());
        LocalDate periodo = normalizarPeriodo(solicitud.getPeriodo());
        if (read.existsByFincaIdAndPeriodo(fincaId, periodo)) {
            throw new IllegalArgumentException("Ya existe un plan de ingresos por ventas para esa finca y mes.");
        }

        PlanIngresoVenta plan = new PlanIngresoVenta();
        plan.setId(solicitud.getId() == null ? UUID.randomUUID() : solicitud.getId());
        plan.setFincaId(fincaId);
        plan.setPeriodo(periodo);
        plan.setEstado(EstadoPlanIngresoVenta.BORRADOR);
        plan.setCreadoPorId(TenantContext.getUsuarioId());
        plan.setCreadoEn(OffsetDateTime.now());
        poblar(plan, solicitud);
        write.save(plan);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.CREATE, "PLAN_INGRESO_VENTA", plan.getId(),
                "Creado plan mensual de ingresos por ventas", null, resumen(plan));
    }

    @Override
    public void update(PlanIngresoVentaDto solicitud) {
        if (solicitud == null || solicitud.getId() == null) {
            throw new IllegalArgumentException("Debe indicar el plan a actualizar.");
        }
        validarSolicitud(solicitud);
        PlanIngresoVenta plan = write.findById(solicitud.getId())
                .orElseThrow(() -> new IllegalArgumentException("Plan de ingresos por ventas no encontrado."));
        validarEscritura(plan.getFincaId());
        if (plan.getEstado() != EstadoPlanIngresoVenta.BORRADOR) {
            throw new IllegalArgumentException("Solo se pueden modificar planes en borrador.");
        }
        UUID fincaId = resolverYValidarFinca(solicitud.getFincaId());
        LocalDate periodo = normalizarPeriodo(solicitud.getPeriodo());
        if (!plan.getFincaId().equals(fincaId) || !plan.getPeriodo().equals(periodo)) {
            throw new IllegalArgumentException("La finca y el mes de un plan ya creado no pueden modificarse.");
        }
        Map<String, Object> anterior = resumen(plan);
        poblar(plan, solicitud);
        write.save(plan);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.UPDATE, "PLAN_INGRESO_VENTA", plan.getId(),
                "Actualizado plan mensual de ingresos por ventas", anterior, resumen(plan));
    }

    @Override
    public void delete(UUID id) {
        PlanIngresoVenta plan = write.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan de ingresos por ventas no encontrado."));
        validarEscritura(plan.getFincaId());
        if (plan.getEstado() != EstadoPlanIngresoVenta.BORRADOR) {
            throw new IllegalArgumentException("Solo se pueden eliminar planes en borrador.");
        }
        Map<String, Object> anterior = resumen(plan);
        write.delete(plan);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.DELETE, "PLAN_INGRESO_VENTA", id,
                "Eliminado plan mensual de ingresos por ventas", anterior, null);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanIngresoVentaDto findById(UUID id) {
        PlanIngresoVenta plan = read.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan de ingresos por ventas no encontrado."));
        validarLectura(plan.getFincaId());
        return plan.toAggregate();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<PlanIngresoVenta> specifications =
                new GenericSpecificationsBuilder<>(filterCriteria == null ? List.of() : filterCriteria);
        Specification<PlanIngresoVenta> alcance = specifications;
        if (TenantContext.get() != null && TenantContext.shouldFilter()) {
            UUID fincaId = TenantContext.getEffectiveFincaId();
            alcance = alcance.and((root, query, builder) -> builder.equal(root.get("fincaId"), fincaId));
        }
        Page<PlanIngresoVenta> data = read.findAll(alcance, pageable);
        List<PlanIngresoVentaResponse> respuesta = new ArrayList<>();
        for (PlanIngresoVenta plan : data.getContent()) {
            validarLectura(plan.getFincaId());
            respuesta.add(new PlanIngresoVentaResponse(plan.toAggregate()));
        }
        return new PaginatedResponse(respuesta, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public PlanIngresoVentaDto generarSiguiente(LocalDate periodoSolicitado) {
        if (periodoSolicitado == null) throw new IllegalArgumentException("Debe indicar el período a generar.");
        if (TenantContext.get() == null || TenantContext.getEffectiveFincaId() == null) {
            throw new IllegalArgumentException("Debe seleccionar una finca para generar el plan.");
        }
        UUID fincaId = TenantContext.getEffectiveFincaId();
        validarEscritura(fincaId);
        LocalDate periodo = normalizarPeriodo(periodoSolicitado);
        var existente = read.findByFincaIdAndPeriodo(fincaId, periodo);
        if (existente.isPresent()) return existente.get().toAggregate();
        PlanIngresoVenta plan = new PlanIngresoVenta();
        plan.setId(UUID.randomUUID()); plan.setFincaId(fincaId); plan.setPeriodo(periodo);
        plan.setEstado(EstadoPlanIngresoVenta.BORRADOR); plan.setCreadoPorId(TenantContext.getUsuarioId());
        plan.setCreadoEn(OffsetDateTime.now());
        PlanIngresoVenta anterior = read.findByFincaIdAndPeriodo(fincaId, periodo.minusMonths(1)).orElse(null);
        PlanIngresoVentaDto base = anterior == null ? PlanIngresoVentaDto.builder().fincaId(fincaId).periodo(periodo)
                .pagosSalario(CERO).pagosCompras(CERO).comprasServicios(CERO).elementos(List.of()).actividades(List.of()).build()
                : anterior.toAggregate();
        poblar(plan, base);
        write.save(plan);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.CREATE, "PLAN_INGRESO_VENTA", plan.getId(),
                "Generado borrador de plan mensual", null, resumen(plan));
        return plan.toAggregate();
    }

    @Override
    public PlanIngresoVentaDto cambiarEstado(UUID id, EstadoPlanIngresoVenta destino) {
        if (destino == null) throw new IllegalArgumentException("Debe indicar el estado destino.");
        PlanIngresoVenta plan = write.findById(id).orElseThrow(() -> new IllegalArgumentException("Plan de ingresos por ventas no encontrado."));
        validarEscritura(plan.getFincaId());
        EstadoPlanIngresoVenta actual = plan.getEstado();
        boolean permitido = (actual == EstadoPlanIngresoVenta.BORRADOR && (destino == EstadoPlanIngresoVenta.EN_REVISION || destino == EstadoPlanIngresoVenta.ANULADO))
                || (actual == EstadoPlanIngresoVenta.EN_REVISION && (destino == EstadoPlanIngresoVenta.APROBADO || destino == EstadoPlanIngresoVenta.BORRADOR || destino == EstadoPlanIngresoVenta.ANULADO))
                || (actual == EstadoPlanIngresoVenta.APROBADO && (destino == EstadoPlanIngresoVenta.CERRADO || destino == EstadoPlanIngresoVenta.ANULADO));
        if (!permitido) throw new IllegalArgumentException("La transición de " + actual + " a " + destino + " no está permitida.");
        Map<String, Object> anterior = resumen(plan); plan.setEstado(destino); plan.setActualizadoEn(OffsetDateTime.now());
        write.save(plan);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.UPDATE, "PLAN_INGRESO_VENTA", plan.getId(),
                "Actualizado estado del plan mensual", anterior, resumen(plan));
        return plan.toAggregate();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> ejecucion(UUID id) {
        PlanIngresoVenta plan = read.findById(id).orElseThrow(() -> new IllegalArgumentException("Plan de ingresos por ventas no encontrado."));
        validarLectura(plan.getFincaId());
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("planId", plan.getId()); resultado.put("periodo", plan.getPeriodo());
        resultado.put("totalIngresosPlanificados", plan.getTotalIngresosPlanificados());
        resultado.put("totalEgresosPlanificados", plan.getTotalEgresos());
        resultado.put("disponiblePlanificado", plan.getDisponible());
        Map<String, Object[]> realesPorCultivo = new LinkedHashMap<>();
        if (reportes != null) {
            String year = String.valueOf(plan.getPeriodo().getYear());
            String mes = nombreMes(plan.getPeriodo().getMonthValue());
            for (Object[] fila : reportes.resumenCosechaPorCultivo(plan.getFincaId(), year, mes)) {
                realesPorCultivo.put(String.valueOf(fila[0]), fila);
            }
        }
        List<Map<String, Object>> actividades = plan.getActividades().stream().map(a -> {
            Object[] real = realesPorCultivo.get(String.valueOf(a.getTipoCultivoId()));
            BigDecimal ingresoReal = real == null ? CERO : decimal(real[2]);
            BigDecimal salarioReal = real == null ? CERO : decimal(real[3]);
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("tipoCultivoNombre", a.getCultivoSnapshot()); fila.put("actividad", a.getActividadSnapshot());
            fila.put("laborAgricola", a.getLaborAgricolaSnapshot()); fila.put("codigoCentroCosto", a.getCodigoCentroCosto());
            fila.put("centroCosto", a.getCentroCostoSnapshot());
            fila.put("cantidadPlanificada", a.getCantidadPlanificada()); fila.put("cantidadReal", BigDecimal.ZERO);
            fila.put("salarioPlanificado", a.getCostoSalario()); fila.put("salarioReal", salarioReal);
            fila.put("comprasPlanificadas", a.getCompras()); fila.put("comprasReales", CERO);
            fila.put("serviciosPlanificados", a.getServicios()); fila.put("serviciosReales", CERO);
            fila.put("gastosPlanificados", a.getGastos().stream().map(GastoPlanMensual::toAggregate).toList());
            fila.put("_ingresoReal", ingresoReal); return fila;
        }).toList();
        BigDecimal ingresoReal = actividades.stream().map(x -> (BigDecimal) x.remove("_ingresoReal")).reduce(CERO, BigDecimal::add);
        BigDecimal salarioReal = actividades.stream().map(x -> (BigDecimal) x.get("salarioReal")).reduce(CERO, BigDecimal::add);
        resultado.put("totalIngresosReales", ingresoReal);
        resultado.put("totalEgresosReales", salarioReal);
        resultado.put("totalComprasPlanificadas", plan.getPagosCompras());
        resultado.put("totalServiciosPlanificados", plan.getComprasServicios());
        resultado.put("disponibleReal", ingresoReal.subtract(salarioReal).setScale(4));
        resultado.put("actividades", actividades);
        return resultado;
    }

    private void poblar(PlanIngresoVenta plan, PlanIngresoVentaDto solicitud) {
        boolean usaDotaciones = tieneActividadesConDotacion(solicitud);
        boolean usaGastos = tieneGastos(solicitud);
        BigDecimal pagosSalario = (usaDotaciones || usaGastos) ? CERO : importe(solicitud.getPagosSalario());
        BigDecimal porcentaje = solicitud.getPorcentajeAportePresupuesto() == null
                ? new BigDecimal("10.0000") : solicitud.getPorcentajeAportePresupuesto();
        BigDecimal aporte = pagosSalario.multiply(porcentaje).divide(CIEN, 4, RoundingMode.HALF_UP);
        BigDecimal pagosCompras = usaGastos ? CERO : importe(solicitud.getPagosCompras());
        BigDecimal comprasServicios = usaGastos ? CERO : importe(solicitud.getComprasServicios());

        plan.setPagosSalario(pagosSalario);
        plan.setPorcentajeAportePresupuesto(porcentaje.setScale(4, RoundingMode.HALF_UP));
        plan.setAportePresupuesto(aporte);
        plan.setPagosCompras(pagosCompras);
        plan.setComprasServicios(comprasServicios);
        plan.setObservaciones(texto(solicitud.getObservaciones(), 1000));
        plan.getElementos().clear();
        plan.getActividades().clear();

        BigDecimal ingresos = CERO;
        int orden = 1;
        for (ElementoPlanIngresoVentaDto detalle : solicitud.getElementos() == null ? List.<ElementoPlanIngresoVentaDto>of() : solicitud.getElementos()) {
            BigDecimal cantidad = importe(detalle.getCantidadPlanificada());
            BigDecimal precio = importe(detalle.getPrecioUnitarioSnapshot());
            BigDecimal importePlanificado = cantidad.multiply(precio).setScale(4, RoundingMode.HALF_UP);
            ElementoPlanIngresoVenta elemento = new ElementoPlanIngresoVenta();
            elemento.setId(UUID.randomUUID());
            elemento.setPlan(plan);
            elemento.setOrden(orden++);
            elemento.setProductoCodigoSnapshot(texto(detalle.getProductoCodigoSnapshot(), 50));
            elemento.setProductoNombreSnapshot(texto(detalle.getProductoNombreSnapshot(), 150));
            elemento.setUnidadMedidaSnapshot(texto(detalle.getUnidadMedidaSnapshot(), 50));
            elemento.setTipoPrecioSnapshot(texto(detalle.getTipoPrecioSnapshot(), 30));
            elemento.setCantidadPlanificada(cantidad);
            elemento.setPrecioUnitarioSnapshot(precio);
            elemento.setImportePlanificado(importePlanificado);
            plan.getElementos().add(elemento);
            ingresos = ingresos.add(importePlanificado);
        }
        int ordenActividad = 1;
        BigDecimal costosActividades = CERO;
        for (ActividadPlanMensualDto detalle : solicitud.getActividades() == null ? List.<ActividadPlanMensualDto>of() : solicitud.getActividades()) {
            if (detalle == null || (vacio(detalle.getActividad()) && detalle.getLaborAgricolaId() == null
                    && vacio(detalle.getLaborAgricolaSnapshot()))) throw new IllegalArgumentException("Cada actividad debe indicar una descripción.");
            validarNoNegativo(detalle.getAreaPlanificada(), "El área planificada"); validarNoNegativo(detalle.getCantidadMeta(), "La cantidad meta");
            validarNoNegativo(detalle.getPagoSalario(), "El pago salarial"); validarNoNegativo(detalle.getCompras(), "Las compras"); validarNoNegativo(detalle.getServicios(), "Los servicios");
            var actividad = new com.kynsoft.report.infrastructure.entity.ActividadPlanMensual();
            actividad.setId(UUID.randomUUID()); actividad.setPlan(plan); actividad.setOrden(ordenActividad++);
            actividad.setTipoCultivoId(detalle.getTipoCultivoId()); actividad.setCultivoSnapshot(texto(detalle.getTipoCultivoNombreSnapshot(), 150));
            actividad.setBloqueSnapshot(texto(detalle.getBloqueSnapshot(), 100)); actividad.setCampoSnapshot(texto(detalle.getCampoSnapshot(), 100));
            poblarCentroCostoYLabor(actividad, detalle);
            actividad.setActividadSnapshot(texto(detalle.getActividad(), 200));
            if (vacio(actividad.getActividadSnapshot()) && !vacio(actividad.getLaborAgricolaSnapshot())) actividad.setActividadSnapshot(actividad.getLaborAgricolaSnapshot());
            if (!vacio(detalle.getUnidadMedidaSnapshot())) actividad.setUnidadMedidaSnapshot(texto(detalle.getUnidadMedidaSnapshot(), 50));
            actividad.setAreaPlanificada(detalle.getAreaPlanificada() == null ? null : importe(detalle.getAreaPlanificada()));
            actividad.setCantidadPlanificada(detalle.getCantidadMeta() == null ? null : importe(detalle.getCantidadMeta()));
            BigDecimal salarioActividad = poblarDotaciones(actividad, detalle.getDotaciones());
            // Compatibilidad de planes históricos: una actividad sin dotación conserva su costo guardado.
            if ((detalle.getDotaciones() == null || detalle.getDotaciones().isEmpty()) && !usaDotaciones && !usaGastos) {
                salarioActividad = importe(detalle.getPagoSalario());
            }
            actividad.setCostoSalario(salarioActividad);
            BigDecimal comprasActividad = usaGastos ? poblarGastos(actividad, detalle.getGastos()) : importe(detalle.getCompras());
            BigDecimal serviciosActividad = usaGastos ? sumarGastosPorCategoria(actividad, "SERVICIO") : importe(detalle.getServicios());
            actividad.setCompras(comprasActividad); actividad.setServicios(serviciosActividad);
            actividad.setTotal(actividad.getCostoSalario().add(actividad.getCompras()).add(actividad.getServicios()).setScale(4));
            plan.getActividades().add(actividad); costosActividades = costosActividades.add(actividad.getTotal());
            if (usaDotaciones) pagosSalario = pagosSalario.add(salarioActividad);
            if (usaGastos) { pagosCompras = pagosCompras.add(comprasActividad); comprasServicios = comprasServicios.add(serviciosActividad); }
        }
        // El aporte siempre se calcula sobre el salario planificado, nunca sobre un importe del cliente.
        aporte = pagosSalario.multiply(porcentaje).divide(CIEN, 4, RoundingMode.HALF_UP);
        plan.setPagosSalario(pagosSalario);
        plan.setPagosCompras(pagosCompras);
        plan.setComprasServicios(comprasServicios);
        plan.setAportePresupuesto(aporte);
        BigDecimal costosAdicionalesActividades = usaDotaciones
                ? costosActividades.subtract(pagosSalario) : costosActividades;
        BigDecimal egresos = usaGastos
                ? pagosSalario.add(aporte).add(costosAdicionalesActividades).setScale(4)
                : pagosSalario.add(aporte).add(pagosCompras).add(comprasServicios)
                        .add(costosAdicionalesActividades).setScale(4);
        plan.setTotalIngresosPlanificados(ingresos);
        plan.setTotalEgresos(egresos);
        plan.setDisponible(ingresos.subtract(plan.getTotalEgresos()).setScale(4));
        plan.setActualizadoEn(OffsetDateTime.now());
    }

    private boolean tieneActividadesConDotacion(PlanIngresoVentaDto solicitud) {
        return solicitud.getActividades() != null && solicitud.getActividades().stream()
                .anyMatch(a -> a != null && a.getDotaciones() != null && !a.getDotaciones().isEmpty());
    }

    private boolean tieneGastos(PlanIngresoVentaDto solicitud) {
        return solicitud.getActividades() != null && solicitud.getActividades().stream()
                .anyMatch(a -> a != null && a.getGastos() != null && !a.getGastos().isEmpty());
    }

    private void poblarCentroCostoYLabor(com.kynsoft.report.infrastructure.entity.ActividadPlanMensual actividad,
            ActividadPlanMensualDto detalle) {
        actividad.setCodigoCentroCosto(texto(detalle.getCodigoCentroCosto(), 30));
        actividad.setCentroCostoSnapshot(texto(detalle.getCentroCostoSnapshot(), 150));
        actividad.setLaborAgricolaId(detalle.getLaborAgricolaId());
        actividad.setLaborAgricolaSnapshot(texto(detalle.getLaborAgricolaSnapshot(), 200));
        if (detalle.getLaborAgricolaId() != null) {
            if (labores == null) throw new IllegalStateException("No está disponible el nomenclador de labores agrícolas.");
            Labor labor = labores.findById(detalle.getLaborAgricolaId())
                    .orElseThrow(() -> new IllegalArgumentException("Labor agrícola no encontrada."));
            actividad.setLaborAgricolaSnapshot(texto(labor.getName(), 200));
            if (vacio(actividad.getCodigoCentroCosto())) actividad.setCodigoCentroCosto(texto(labor.getCodigoCentroCosto(), 30));
            if (vacio(actividad.getUnidadMedidaSnapshot())) actividad.setUnidadMedidaSnapshot(texto(labor.getUnidadMedida(), 50));
        }
        if (!vacio(actividad.getCodigoCentroCosto())) {
            if (cuentasContables == null) throw new IllegalStateException("No está disponible el nomenclador de centros de costo.");
            CuentaContable centro = cuentasContables.findByCodigoAndActivoTrue(actividad.getCodigoCentroCosto())
                    .orElseThrow(() -> new IllegalArgumentException("Centro de costo no encontrado."));
            if (!Boolean.TRUE.equals(centro.getEsCentroCosto())) throw new IllegalArgumentException("La cuenta indicada no está definida como centro de costo.");
            actividad.setCodigoCentroCosto(centro.getCodigo()); actividad.setCentroCostoSnapshot(centro.getNombre());
        }
    }

    private BigDecimal poblarGastos(com.kynsoft.report.infrastructure.entity.ActividadPlanMensual actividad,
            List<GastoPlanMensualDto> gastos) {
        BigDecimal compras = CERO;
        int orden = 1;
        for (GastoPlanMensualDto detalle : gastos == null ? List.<GastoPlanMensualDto>of() : gastos) {
            if (detalle == null || vacio(detalle.getCategoria()) || vacio(detalle.getDescripcion()))
                throw new IllegalArgumentException("Cada gasto debe indicar categoría y descripción.");
            String categoria = detalle.getCategoria().trim().toUpperCase();
            String subcategoria = vacio(detalle.getSubcategoria()) ? null : detalle.getSubcategoria().trim().toUpperCase();
            if (!"INSUMO".equals(categoria)) subcategoria = null;
            validarCategoriaGasto(categoria, subcategoria);
            validarNoNegativo(detalle.getCantidad(), "La cantidad del gasto"); validarNoNegativo(detalle.getPrecioUnitarioSnapshot(), "El precio unitario del gasto");
            if (vacio(actividad.getCodigoCentroCosto())) throw new IllegalArgumentException("Cada gasto debe estar agrupado por un centro de costo.");
            BigDecimal importeGasto = importe(detalle.getCantidad()).multiply(importe(detalle.getPrecioUnitarioSnapshot())).setScale(4, RoundingMode.HALF_UP);
            GastoPlanMensual gasto = new GastoPlanMensual();
            gasto.setId(UUID.randomUUID()); gasto.setActividad(actividad); gasto.setOrden(orden++); gasto.setCategoria(categoria);
            gasto.setSubcategoria(subcategoria); gasto.setDescripcion(texto(detalle.getDescripcion(), 250));
            gasto.setUnidadMedidaSnapshot(texto(detalle.getUnidadMedidaSnapshot(), 50)); gasto.setCantidad(importe(detalle.getCantidad()));
            gasto.setPrecioUnitarioSnapshot(importe(detalle.getPrecioUnitarioSnapshot())); gasto.setImportePlanificado(importeGasto);
            gasto.setObservacion(texto(detalle.getObservacion(), 500)); actividad.getGastos().add(gasto);
            if (!"SERVICIO".equals(categoria)) compras = compras.add(importeGasto);
        }
        return compras.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal sumarGastosPorCategoria(com.kynsoft.report.infrastructure.entity.ActividadPlanMensual actividad, String categoria) {
        return actividad.getGastos().stream().filter(g -> categoria.equals(g.getCategoria()))
                .map(GastoPlanMensual::getImportePlanificado).reduce(CERO, BigDecimal::add).setScale(4, RoundingMode.HALF_UP);
    }

    private void validarCategoriaGasto(String categoria, String subcategoria) {
        if (!("COMBUSTIBLE".equals(categoria) || "LUBRICANTE".equals(categoria) || "ENERGIA".equals(categoria)
                || "MATERIAL".equals(categoria) || "INSUMO".equals(categoria) || "SERVICIO".equals(categoria) || "OTRO".equals(categoria)))
            throw new IllegalArgumentException("La categoría del gasto no es válida.");
        if (subcategoria != null && !("HERBICIDA".equals(subcategoria) || "FERTILIZANTE".equals(subcategoria)
                || "PLAGUICIDA".equals(subcategoria) || "SEMILLA".equals(subcategoria) || "ABONO".equals(subcategoria) || "OTRO".equals(subcategoria)))
            throw new IllegalArgumentException("La subcategoría del gasto no es válida.");
        if (!"INSUMO".equals(categoria) && subcategoria != null) throw new IllegalArgumentException("La subcategoría solo aplica a insumos.");
    }

    private BigDecimal poblarDotaciones(com.kynsoft.report.infrastructure.entity.ActividadPlanMensual actividad,
            List<DotacionPlanMensualDto> dotaciones) {
        BigDecimal total = CERO;
        for (DotacionPlanMensualDto detalle : dotaciones == null ? List.<DotacionPlanMensualDto>of() : dotaciones) {
            if (detalle == null || detalle.getCargoId() == null) {
                throw new IllegalArgumentException("Cada dotación debe indicar un cargo.");
            }
            if (detalle.getCantidadTrabajadores() == null || detalle.getCantidadTrabajadores() <= 0) {
                throw new IllegalArgumentException("La cantidad de trabajadores debe ser mayor que cero.");
            }
            validarNoNegativo(detalle.getDiasPlanificados(), "Los días planificados");
            validarNoNegativo(detalle.getHorasPorDia(), "Las horas por día");
            if (cargos == null) throw new IllegalStateException("No está disponible el catálogo de cargos.");
            Cargo cargo = cargos.findById(detalle.getCargoId())
                    .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado para la dotación."));
            BigDecimal salarioEscala = importe(cargo.getSalarioEscala());
            BigDecimal dias = importe(detalle.getDiasPlanificados());
            BigDecimal horas = importe(detalle.getHorasPorDia());
            BigDecimal salario = salarioEscala.multiply(BigDecimal.valueOf(detalle.getCantidadTrabajadores()))
                    .multiply(dias).multiply(horas).divide(new BigDecimal("192"), 4, RoundingMode.HALF_UP);
            DotacionPlanMensual dotacion = new DotacionPlanMensual();
            dotacion.setId(UUID.randomUUID()); dotacion.setActividad(actividad); dotacion.setCargoId(cargo.getId());
            dotacion.setCargoNombreSnapshot(texto(cargo.getName(), 150)); dotacion.setSalarioEscalaSnapshot(salarioEscala);
            dotacion.setCantidadTrabajadores(detalle.getCantidadTrabajadores()); dotacion.setDiasPlanificados(dias);
            dotacion.setHorasPorDia(horas); dotacion.setSalarioPlanificado(salario);
            actividad.getDotaciones().add(dotacion);
            total = total.add(salario);
        }
        return total.setScale(4, RoundingMode.HALF_UP);
    }

    private void validarSolicitud(PlanIngresoVentaDto solicitud) {
        if (solicitud == null || solicitud.getPeriodo() == null || solicitud.getFincaId() == null) {
            throw new IllegalArgumentException("La finca y el mes del plan son obligatorios.");
        }
        validarNoNegativo(solicitud.getPagosSalario(), "Los pagos de salario");
        validarNoNegativo(solicitud.getPagosCompras(), "Los pagos de compras");
        validarNoNegativo(solicitud.getComprasServicios(), "Las compras y servicios");
        if (solicitud.getPorcentajeAportePresupuesto() != null
                && (solicitud.getPorcentajeAportePresupuesto().compareTo(BigDecimal.ZERO) < 0
                || solicitud.getPorcentajeAportePresupuesto().compareTo(CIEN) > 0)) {
            throw new IllegalArgumentException("El porcentaje de aporte al presupuesto debe estar entre 0 y 100.");
        }
        for (ElementoPlanIngresoVentaDto detalle : solicitud.getElementos() == null ? List.<ElementoPlanIngresoVentaDto>of() : solicitud.getElementos()) {
            if (detalle == null || vacio(detalle.getProductoNombreSnapshot()) || vacio(detalle.getUnidadMedidaSnapshot())) {
                throw new IllegalArgumentException("Cada concepto debe indicar producto y unidad de medida.");
            }
            validarNoNegativo(detalle.getCantidadPlanificada(), "La cantidad planificada");
            validarNoNegativo(detalle.getPrecioUnitarioSnapshot(), "El precio unitario");
        }
    }

    private UUID resolverYValidarFinca(UUID fincaId) {
        UUID efectiva = TenantContext.get() == null ? fincaId : TenantValidator.getWriteFincaId(fincaId);
        fincas.findById(efectiva).orElseThrow(() -> new IllegalArgumentException("Finca no encontrada."));
        validarEscritura(efectiva);
        return efectiva;
    }
    private LocalDate normalizarPeriodo(LocalDate periodo) { return periodo.withDayOfMonth(1); }
    private void validarLectura(UUID fincaId) { if (fincaId != null && TenantContext.get() != null) TenantValidator.validateReadAccess(fincaId); }
    private void validarEscritura(UUID fincaId) { if (fincaId != null && TenantContext.get() != null) TenantValidator.validateWriteAccess(fincaId); }
    private BigDecimal importe(BigDecimal valor) { return (valor == null ? CERO : valor).setScale(4, RoundingMode.HALF_UP); }
    private BigDecimal decimal(Object valor) { return valor instanceof BigDecimal n ? n.setScale(4, RoundingMode.HALF_UP) : new BigDecimal(String.valueOf(valor)).setScale(4, RoundingMode.HALF_UP); }
    private String nombreMes(int mes) { return switch (mes) { case 1 -> "Enero"; case 2 -> "Febrero"; case 3 -> "Marzo"; case 4 -> "Abril"; case 5 -> "Mayo"; case 6 -> "Junio"; case 7 -> "Julio"; case 8 -> "Agosto"; case 9 -> "Septiembre"; case 10 -> "Octubre"; case 11 -> "Noviembre"; case 12 -> "Diciembre"; default -> throw new IllegalArgumentException("Mes inválido"); }; }
    private void validarNoNegativo(BigDecimal valor, String etiqueta) { if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException(etiqueta + " no pueden ser negativos."); }
    private boolean vacio(String texto) { return texto == null || texto.trim().isEmpty(); }
    private String texto(String valor, int maximo) { if (valor == null) return null; String limpio = valor.trim(); return limpio.length() > maximo ? limpio.substring(0, maximo) : limpio; }
    private Map<String, Object> resumen(PlanIngresoVenta plan) {
        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("fincaId", plan.getFincaId()); datos.put("periodo", plan.getPeriodo());
        datos.put("estado", plan.getEstado()); datos.put("ingresos", plan.getTotalIngresosPlanificados());
        datos.put("egresos", plan.getTotalEgresos()); datos.put("disponible", plan.getDisponible());
        datos.put("elementos", plan.getElementos().size()); return datos;
    }
}
