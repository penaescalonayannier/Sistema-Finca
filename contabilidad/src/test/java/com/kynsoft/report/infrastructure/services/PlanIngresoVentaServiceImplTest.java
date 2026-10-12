package com.kynsoft.report.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.kynsoft.report.domain.dto.AuditoriaDto;
import com.kynsoft.report.domain.dto.ActividadPlanMensualDto;
import com.kynsoft.report.domain.dto.DotacionPlanMensualDto;
import com.kynsoft.report.domain.dto.GastoPlanMensualDto;
import com.kynsoft.report.domain.dto.LaborDto;
import com.kynsoft.report.domain.dto.ElementoPlanIngresoVentaDto;
import com.kynsoft.report.domain.dto.ActividadPlanMensualDto;
import com.kynsoft.report.domain.dto.DotacionPlanMensualDto;
import com.kynsoft.report.domain.dto.EstadoPlanIngresoVenta;
import com.kynsoft.report.domain.dto.PlanIngresoVentaDto;
import com.kynsoft.report.domain.dto.Rol;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.services.IAuditoriaService;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.entity.Cargo;
import com.kynsoft.report.infrastructure.entity.CuentaContable;
import com.kynsoft.report.infrastructure.entity.Labor;
import com.kynsoft.report.infrastructure.entity.PlanIngresoVenta;
import com.kynsoft.report.infrastructure.entity.Cargo;
import com.kynsoft.report.infrastructure.repository.command.PlanIngresoVentaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CargoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CuentaContableReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.LaborReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.PlanIngresoVentaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CargoReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantInfo;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

/** Pruebas sin Mockito: esta estación ejecuta los tests dentro de un sandbox sin attach de agentes JVM. */
class PlanIngresoVentaServiceImplTest {
    @AfterEach void limpiarTenant() { TenantContext.clear(); }

    @Test
    void creaPlanCalculandoSnapshotsYTotalesEnElServidor() {
        UUID fincaId = UUID.randomUUID();
        List<PlanIngresoVenta> guardados = new ArrayList<>();
        PlanIngresoVentaServiceImpl service = service(fincaId, false, guardados);
        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());

        service.create(plan(fincaId, new BigDecimal("100.00"), new BigDecimal("2.5"), new BigDecimal("12.00")));

        PlanIngresoVenta guardado = guardados.getFirst();
        assertEquals(LocalDate.of(2026, 9, 1), guardado.getPeriodo());
        assertEquals(0, new BigDecimal("30.0000").compareTo(guardado.getTotalIngresosPlanificados()));
        assertEquals(0, new BigDecimal("10.0000").compareTo(guardado.getAportePresupuesto()));
        assertEquals(0, new BigDecimal("110.0000").compareTo(guardado.getTotalEgresos()));
        assertEquals(0, new BigDecimal("-80.0000").compareTo(guardado.getDisponible()));
        assertEquals(1, guardado.getElementos().size());
        assertEquals(0, new BigDecimal("30.0000").compareTo(guardado.getElementos().getFirst().getImportePlanificado()));
    }

    @Test
    void rechazaSegundoPlanParaLaMismaFincaYMes() {
        UUID fincaId = UUID.randomUUID();
        PlanIngresoVentaServiceImpl service = service(fincaId, true, new ArrayList<>());
        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.create(plan(fincaId, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE)));

        assertEquals("Ya existe un plan de ingresos por ventas para esa finca y mes.", error.getMessage());
    }

    @Test
    void usaAportePredeterminadoYRecalculaTotalesIgnorandoImportesDelCliente() {
        UUID fincaId = UUID.randomUUID();
        List<PlanIngresoVenta> guardados = new ArrayList<>();
        PlanIngresoVentaServiceImpl service = service(fincaId, false, guardados);
        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());

        PlanIngresoVentaDto solicitud = plan(fincaId, new BigDecimal("80"), new BigDecimal("3"), new BigDecimal("7"));
        solicitud.setPagosCompras(new BigDecimal("12.50"));
        solicitud.setComprasServicios(new BigDecimal("4.25"));
        solicitud.setPorcentajeAportePresupuesto(null);
        solicitud.setElementos(List.of(
                ElementoPlanIngresoVentaDto.builder().productoNombreSnapshot("Yuca")
                        .unidadMedidaSnapshot("kg").cantidadPlanificada(new BigDecimal("3"))
                        .precioUnitarioSnapshot(new BigDecimal("7")).importePlanificado(BigDecimal.ZERO).build(),
                ElementoPlanIngresoVentaDto.builder().productoNombreSnapshot("Boniato")
                        .unidadMedidaSnapshot("kg").cantidadPlanificada(new BigDecimal("2.5"))
                        .precioUnitarioSnapshot(new BigDecimal("4")).importePlanificado(new BigDecimal("999")).build()));

        service.create(solicitud);

        PlanIngresoVenta guardado = guardados.getFirst();
        assertEquals(0, new BigDecimal("10.0000").compareTo(guardado.getPorcentajeAportePresupuesto()));
        assertEquals(0, new BigDecimal("8.0000").compareTo(guardado.getAportePresupuesto()));
        assertEquals(0, new BigDecimal("31.0000").compareTo(guardado.getTotalIngresosPlanificados()));
        assertEquals(0, new BigDecimal("104.7500").compareTo(guardado.getTotalEgresos()));
        assertEquals(0, new BigDecimal("-73.7500").compareTo(guardado.getDisponible()));
    }

    @Test
    void actualizaBorradorManteniendoFincaYPeriodoYRecalculandoValores() {
        UUID fincaId = UUID.randomUUID();
        PlanIngresoVenta existente = new PlanIngresoVenta();
        existente.setId(UUID.randomUUID());
        existente.setFincaId(fincaId);
        existente.setPeriodo(LocalDate.of(2026, 9, 1));
        existente.setEstado(EstadoPlanIngresoVenta.BORRADOR);
        List<PlanIngresoVenta> guardados = new ArrayList<>();
        PlanIngresoVentaServiceImpl service = serviceConPlanExistente(fincaId, existente, guardados);
        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());
        PlanIngresoVentaDto solicitud = plan(fincaId, new BigDecimal("50"), new BigDecimal("4"), new BigDecimal("6"));
        solicitud.setId(existente.getId());
        solicitud.setPorcentajeAportePresupuesto(new BigDecimal("15"));

        service.update(solicitud);

        assertEquals(existente, guardados.getFirst());
        assertEquals(0, new BigDecimal("24.0000").compareTo(existente.getTotalIngresosPlanificados()));
        assertEquals(0, new BigDecimal("7.5000").compareTo(existente.getAportePresupuesto()));
        assertEquals(0, new BigDecimal("57.5000").compareTo(existente.getTotalEgresos()));
        assertEquals(0, new BigDecimal("-33.5000").compareTo(existente.getDisponible()));
    }

    @Test
    void impideModificarUnPlanQueNoEstaEnBorrador() {
        UUID fincaId = UUID.randomUUID();
        PlanIngresoVenta existente = new PlanIngresoVenta();
        existente.setId(UUID.randomUUID());
        existente.setFincaId(fincaId);
        existente.setPeriodo(LocalDate.of(2026, 9, 1));
        existente.setEstado(EstadoPlanIngresoVenta.APROBADO);
        PlanIngresoVentaServiceImpl service = serviceConPlanExistente(fincaId, existente, new ArrayList<>());
        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());
        PlanIngresoVentaDto solicitud = plan(fincaId, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE);
        solicitud.setId(existente.getId());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> service.update(solicitud));

        assertEquals("Solo se pueden modificar planes en borrador.", error.getMessage());
    }

    @Test
    void rechazaPorcentajeFueraDelRangoPermitido() {
        UUID fincaId = UUID.randomUUID();
        PlanIngresoVentaServiceImpl service = service(fincaId, false, new ArrayList<>());
        PlanIngresoVentaDto solicitud = plan(fincaId, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE);
        solicitud.setPorcentajeAportePresupuesto(new BigDecimal("100.0001"));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> service.create(solicitud));

        assertEquals("El porcentaje de aporte al presupuesto debe estar entre 0 y 100.", error.getMessage());
    }

    @Test
    void calculaSalarioDesdeDotacionYNoAceptaElImporteEnviadoPorCliente() {
        UUID fincaId = UUID.randomUUID();
        UUID cargoId = UUID.randomUUID();
        List<PlanIngresoVenta> guardados = new ArrayList<>();
        PlanIngresoVentaServiceImpl service = serviceConCargo(fincaId, cargoId, new BigDecimal("1920"), guardados);
        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());
        PlanIngresoVentaDto solicitud = plan(fincaId, new BigDecimal("99999"), BigDecimal.ONE, BigDecimal.ONE);
        solicitud.setActividades(List.of(ActividadPlanMensualDto.builder().actividad("Cosecha")
                .pagoSalario(new BigDecimal("1")).compras(BigDecimal.ZERO).servicios(BigDecimal.ZERO)
                .dotaciones(List.of(DotacionPlanMensualDto.builder().cargoId(cargoId).cantidadTrabajadores(2)
                        .diasPlanificados(new BigDecimal("3")).horasPorDia(new BigDecimal("8")).build())).build()));

        service.create(solicitud);

        PlanIngresoVenta guardado = guardados.getFirst();
        assertEquals(0, new BigDecimal("480.0000").compareTo(guardado.getPagosSalario()));
        assertEquals(0, new BigDecimal("48.0000").compareTo(guardado.getAportePresupuesto()));
        assertEquals(0, new BigDecimal("528.0000").compareTo(guardado.getTotalEgresos()));
        assertEquals(0, new BigDecimal("480.0000").compareTo(guardado.getActividades().iterator().next().getCostoSalario()));
        assertEquals("Operario", guardado.getActividades().iterator().next().getDotaciones().iterator().next().getCargoNombreSnapshot());
    }

    @Test
    void calculaSalarioDeDotacionConElCargoEIgnoraPagoManualDelCliente() {
        UUID fincaId = UUID.randomUUID();
        UUID cargoId = UUID.randomUUID();
        Cargo cargo = new Cargo();
        cargo.setId(cargoId);
        cargo.setName("Operario agrícola");
        cargo.setSalarioEscala(new BigDecimal("1920"));
        List<PlanIngresoVenta> guardados = new ArrayList<>();
        PlanIngresoVentaServiceImpl service = serviceConCargo(fincaId, cargo, guardados);
        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());

        PlanIngresoVentaDto solicitud = plan(fincaId, new BigDecimal("999999"), new BigDecimal("2.5"), new BigDecimal("12"));
        solicitud.setActividades(List.of(ActividadPlanMensualDto.builder().actividad("Cosecha de yuca")
                .pagoSalario(new BigDecimal("999999"))
                .dotaciones(List.of(DotacionPlanMensualDto.builder().cargoId(cargoId)
                        .cantidadTrabajadores(2).diasPlanificados(new BigDecimal("3"))
                        .horasPorDia(new BigDecimal("8")).salarioPlanificado(BigDecimal.ZERO).build()))
                .build()));

        service.create(solicitud);

        PlanIngresoVenta guardado = guardados.getFirst();
        var actividad = guardado.getActividades().iterator().next();
        var dotacion = actividad.getDotaciones().iterator().next();
        // 2 trabajadores × 3 días × 8 horas × (1,920 / 24 / 8) = 480.
        assertEquals(0, new BigDecimal("480.0000").compareTo(dotacion.getSalarioPlanificado()));
        assertEquals(0, new BigDecimal("1920.0000").compareTo(dotacion.getSalarioEscalaSnapshot()));
        assertEquals("Operario agrícola", dotacion.getCargoNombreSnapshot());
        assertEquals(0, new BigDecimal("480.0000").compareTo(actividad.getCostoSalario()));
        assertEquals(0, new BigDecimal("480.0000").compareTo(guardado.getPagosSalario()));
        assertEquals(0, new BigDecimal("48.0000").compareTo(guardado.getAportePresupuesto()));
        assertEquals(0, new BigDecimal("528.0000").compareTo(guardado.getTotalEgresos()));
    }

    @Test
    void rechazaDotacionConCargoInexistente() {
        UUID fincaId = UUID.randomUUID();
        PlanIngresoVentaServiceImpl service = serviceConCargo(fincaId, null, new ArrayList<>());
        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());
        PlanIngresoVentaDto solicitud = plan(fincaId, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE);
        solicitud.setActividades(List.of(ActividadPlanMensualDto.builder().actividad("Cosecha")
                .dotaciones(List.of(DotacionPlanMensualDto.builder().cargoId(UUID.randomUUID())
                        .cantidadTrabajadores(1).diasPlanificados(BigDecimal.ONE).horasPorDia(new BigDecimal("8")).build()))
                .build()));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> service.create(solicitud));

        assertEquals("Cargo no encontrado para la dotación.", error.getMessage());
    }

    @Test
    void rechazaDotacionConCantidadDeTrabajadoresNoPositiva() {
        UUID fincaId = UUID.randomUUID();
        UUID cargoId = UUID.randomUUID();
        Cargo cargo = new Cargo();
        cargo.setId(cargoId);
        cargo.setSalarioEscala(new BigDecimal("1920"));
        PlanIngresoVentaServiceImpl service = serviceConCargo(fincaId, cargo, new ArrayList<>());
        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());
        PlanIngresoVentaDto solicitud = plan(fincaId, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE);
        solicitud.setActividades(List.of(ActividadPlanMensualDto.builder().actividad("Cosecha")
                .dotaciones(List.of(DotacionPlanMensualDto.builder().cargoId(cargoId)
                        .cantidadTrabajadores(0).diasPlanificados(BigDecimal.ONE).horasPorDia(new BigDecimal("8")).build()))
                .build()));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> service.create(solicitud));

        assertEquals("La cantidad de trabajadores debe ser mayor que cero.", error.getMessage());
    }

    @Test
    void calculaComprasDesdeGastosPersistidosYLosAgrupaPorCentroDeCosto() {
        UUID fincaId = UUID.randomUUID();
        UUID laborId = UUID.randomUUID();
        List<PlanIngresoVenta> guardados = new ArrayList<>();
        PlanIngresoVentaServiceImpl service = serviceConCentroCostoYLabor(fincaId, laborId, guardados);
        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());
        PlanIngresoVentaDto solicitud = plan(fincaId, new BigDecimal("999"), BigDecimal.ONE, new BigDecimal("1000"));
        solicitud.setActividades(List.of(ActividadPlanMensualDto.builder().laborAgricolaId(laborId)
                .codigoCentroCosto("700.01").actividad("Labor manipulada")
                .gastos(List.of(
                        GastoPlanMensualDto.builder().categoria("INSUMO").subcategoria("HERBICIDA")
                                .descripcion("Herbicida").unidadMedidaSnapshot("L").cantidad(new BigDecimal("2"))
                                .precioUnitarioSnapshot(new BigDecimal("30")).build(),
                        GastoPlanMensualDto.builder().categoria("SERVICIO").descripcion("Transporte")
                                .cantidad(BigDecimal.ONE).precioUnitarioSnapshot(new BigDecimal("20")).build()))
                .build()));

        service.create(solicitud);

        PlanIngresoVenta guardado = guardados.getFirst();
        var actividad = guardado.getActividades().iterator().next();
        assertEquals("Deshierbe", actividad.getLaborAgricolaSnapshot());
        assertEquals("Cultivo de yuca", actividad.getCentroCostoSnapshot());
        assertEquals(0, new BigDecimal("60.0000").compareTo(guardado.getPagosCompras()));
        assertEquals(0, new BigDecimal("20.0000").compareTo(guardado.getComprasServicios()));
        assertEquals(0, new BigDecimal("80.0000").compareTo(guardado.getTotalEgresos()));
        assertEquals(2, actividad.getGastos().size());
    }

    private PlanIngresoVentaServiceImpl service(UUID fincaId, boolean existe, List<PlanIngresoVenta> guardados) {
        PlanIngresoVentaWriteDataJPARepository write = proxy(PlanIngresoVentaWriteDataJPARepository.class,
                (nombre, argumentos) -> {
                    if ("save".equals(nombre)) { guardados.add((PlanIngresoVenta) argumentos[0]); return argumentos[0]; }
                    return null;
                });
        PlanIngresoVentaReadDataJPARepository read = proxy(PlanIngresoVentaReadDataJPARepository.class,
                (nombre, argumentos) -> "existsByFincaIdAndPeriodo".equals(nombre) ? existe : Optional.empty());
        FincaReadDataJPARepository fincas = proxy(FincaReadDataJPARepository.class,
                (nombre, argumentos) -> {
                    if ("findById".equals(nombre)) { Finca finca = new Finca(); finca.setId(fincaId); return Optional.of(finca); }
                    return Optional.empty();
                });
        return new PlanIngresoVentaServiceImpl(write, read, fincas, new AuditoriaTransaccionalService(new AuditoriaNula()));
    }

    private PlanIngresoVentaServiceImpl serviceConPlanExistente(UUID fincaId, PlanIngresoVenta existente,
            List<PlanIngresoVenta> guardados) {
        PlanIngresoVentaWriteDataJPARepository write = proxy(PlanIngresoVentaWriteDataJPARepository.class,
                (nombre, argumentos) -> {
                    if ("findById".equals(nombre)) return Optional.of(existente);
                    if ("save".equals(nombre)) { guardados.add((PlanIngresoVenta) argumentos[0]); return argumentos[0]; }
                    return null;
                });
        PlanIngresoVentaReadDataJPARepository read = proxy(PlanIngresoVentaReadDataJPARepository.class,
                (nombre, argumentos) -> Optional.empty());
        FincaReadDataJPARepository fincas = proxy(FincaReadDataJPARepository.class,
                (nombre, argumentos) -> {
                    if ("findById".equals(nombre)) { Finca finca = new Finca(); finca.setId(fincaId); return Optional.of(finca); }
                    return Optional.empty();
                });
        return new PlanIngresoVentaServiceImpl(write, read, fincas, new AuditoriaTransaccionalService(new AuditoriaNula()));
    }

    private PlanIngresoVentaServiceImpl serviceConCargo(UUID fincaId, UUID cargoId, BigDecimal salario,
            List<PlanIngresoVenta> guardados) {
        PlanIngresoVentaWriteDataJPARepository write = proxy(PlanIngresoVentaWriteDataJPARepository.class,
                (nombre, argumentos) -> { if ("save".equals(nombre)) { guardados.add((PlanIngresoVenta) argumentos[0]); return argumentos[0]; } return null; });
        PlanIngresoVentaReadDataJPARepository read = proxy(PlanIngresoVentaReadDataJPARepository.class,
                (nombre, argumentos) -> "existsByFincaIdAndPeriodo".equals(nombre) ? false : Optional.empty());
        FincaReadDataJPARepository fincas = proxy(FincaReadDataJPARepository.class,
                (nombre, argumentos) -> { if ("findById".equals(nombre)) { Finca finca = new Finca(); finca.setId(fincaId); return Optional.of(finca); } return Optional.empty(); });
        CargoReadDataJPARepository cargos = proxy(CargoReadDataJPARepository.class,
                (nombre, argumentos) -> { if ("findById".equals(nombre)) { Cargo cargo = new Cargo(); cargo.setId(cargoId); cargo.setName("Operario"); cargo.setSalarioEscala(salario); return Optional.of(cargo); } return Optional.empty(); });
        return new PlanIngresoVentaServiceImpl(write, read, fincas, new AuditoriaTransaccionalService(new AuditoriaNula()), null, cargos);
    }

    private PlanIngresoVentaServiceImpl serviceConCargo(UUID fincaId, Cargo cargo, List<PlanIngresoVenta> guardados) {
        PlanIngresoVentaWriteDataJPARepository write = proxy(PlanIngresoVentaWriteDataJPARepository.class,
                (nombre, argumentos) -> {
                    if ("save".equals(nombre)) { guardados.add((PlanIngresoVenta) argumentos[0]); return argumentos[0]; }
                    return null;
                });
        PlanIngresoVentaReadDataJPARepository read = proxy(PlanIngresoVentaReadDataJPARepository.class,
                (nombre, argumentos) -> "existsByFincaIdAndPeriodo".equals(nombre) ? false : Optional.empty());
        FincaReadDataJPARepository fincas = proxy(FincaReadDataJPARepository.class,
                (nombre, argumentos) -> {
                    if ("findById".equals(nombre)) { Finca finca = new Finca(); finca.setId(fincaId); return Optional.of(finca); }
                    return Optional.empty();
                });
        CargoReadDataJPARepository cargos = proxy(CargoReadDataJPARepository.class,
                (nombre, argumentos) -> "findById".equals(nombre) && cargo != null ? Optional.of(cargo) : Optional.empty());
        return new PlanIngresoVentaServiceImpl(write, read, fincas, new AuditoriaTransaccionalService(new AuditoriaNula()), null, cargos);
    }

    private PlanIngresoVentaServiceImpl serviceConCentroCostoYLabor(UUID fincaId, UUID laborId, List<PlanIngresoVenta> guardados) {
        PlanIngresoVentaWriteDataJPARepository write = proxy(PlanIngresoVentaWriteDataJPARepository.class,
                (nombre, argumentos) -> { if ("save".equals(nombre)) { guardados.add((PlanIngresoVenta) argumentos[0]); return argumentos[0]; } return null; });
        PlanIngresoVentaReadDataJPARepository read = proxy(PlanIngresoVentaReadDataJPARepository.class,
                (nombre, argumentos) -> "existsByFincaIdAndPeriodo".equals(nombre) ? false : Optional.empty());
        FincaReadDataJPARepository fincas = proxy(FincaReadDataJPARepository.class,
                (nombre, argumentos) -> { if ("findById".equals(nombre)) { Finca finca = new Finca(); finca.setId(fincaId); return Optional.of(finca); } return Optional.empty(); });
        CuentaContableReadDataJPARepository cuentas = proxy(CuentaContableReadDataJPARepository.class,
                (nombre, argumentos) -> { if ("findByCodigoAndActivoTrue".equals(nombre)) { CuentaContable cuenta = new CuentaContable(); cuenta.setCodigo("700.01"); cuenta.setNombre("Cultivo de yuca"); cuenta.setEsCentroCosto(true); return Optional.of(cuenta); } return Optional.empty(); });
        LaborReadDataJPARepository labores = proxy(LaborReadDataJPARepository.class,
                (nombre, argumentos) -> "findById".equals(nombre) ? Optional.of(new Labor(LaborDto.builder()
                        .id(laborId).code("LAB-01").name("Deshierbe").build())) : Optional.empty());
        return new PlanIngresoVentaServiceImpl(write, read, fincas, new AuditoriaTransaccionalService(new AuditoriaNula()), null, null, cuentas, labores);
    }

    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> tipo, Llamada llamada) {
        return (T) Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[] {tipo},
                (proxy, metodo, argumentos) -> llamada.resolver(metodo.getName(), argumentos == null ? new Object[0] : argumentos));
    }

    private PlanIngresoVentaDto plan(UUID fincaId, BigDecimal salario, BigDecimal cantidad, BigDecimal precio) {
        return PlanIngresoVentaDto.builder().id(UUID.randomUUID()).fincaId(fincaId)
                .periodo(LocalDate.of(2026, 9, 15)).pagosSalario(salario)
                .elementos(List.of(ElementoPlanIngresoVentaDto.builder().productoNombreSnapshot("Yuca")
                        .unidadMedidaSnapshot("Unidad").cantidadPlanificada(cantidad)
                        .precioUnitarioSnapshot(precio).importePlanificado(new BigDecimal("999999")).build()))
                .build();
    }

    @FunctionalInterface private interface Llamada { Object resolver(String nombre, Object[] argumentos); }
    private static class AuditoriaNula implements IAuditoriaService {
        @Override public void registrar(TipoAccion accion, String entidad, UUID entidadId, String descripcion, Object anterior, Object nuevo) { }
        @Override public void registrar(UUID usuarioId, String username, TipoAccion accion, String entidad, UUID entidadId, String descripcion, Object anterior, Object nuevo, String ipAddress) { }
        @Override public AuditoriaDto findById(UUID id) { return null; }
        @Override public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filtros) { return null; }
        @Override public List<AuditoriaDto> getHistorialEntidad(String entidad, UUID entidadId) { return List.of(); }
        @Override public List<String> getEntidades() { return List.of(); }
    }
}
