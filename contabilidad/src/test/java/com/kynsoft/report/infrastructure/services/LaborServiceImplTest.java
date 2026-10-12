package com.kynsoft.report.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.kynsoft.report.domain.dto.LaborDto;
import com.kynsoft.report.infrastructure.entity.Cargo;
import com.kynsoft.report.infrastructure.entity.CuentaContable;
import com.kynsoft.report.infrastructure.entity.Labor;
import com.kynsoft.report.infrastructure.repository.command.LaborWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CargoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CuentaContableReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.LaborReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TipoCultivoReadDataJPARepository;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LaborServiceImplTest {

    @Test
    void guardaPlantillaTecnicaConCargosRequeridos() {
        UUID cultivoId = UUID.randomUUID();
        UUID cargoId = UUID.randomUUID();
        List<Labor> guardadas = new ArrayList<>();
        LaborServiceImpl service = service(guardadas, cultivoId, cargoId, true);

        service.create(LaborDto.builder().id(UUID.randomUUID()).code("DESH-01").name("Deshierbe")
                .activo(true).tipoCultivoId(cultivoId).codigoCentroCosto("CC-01").unidadMedida("ha")
                .normaRendimiento(new BigDecimal("2.5")).dotacionEstandar(new BigDecimal("3"))
                .diasEstimados(new BigDecimal("2")).horasEstimadas(new BigDecimal("8"))
                .cargosRequeridos(Set.of(cargoId)).build());

        Labor labor = guardadas.getFirst();
        assertEquals("CC-01", labor.getCodigoCentroCosto());
        assertEquals(new BigDecimal("3"), labor.getDotacionEstandar());
        assertEquals(Set.of(cargoId), labor.getCargosRequeridos());
    }

    @Test
    void rechazaDotacionNegativa() {
        LaborServiceImpl service = service(new ArrayList<>(), null, null, true);
        LaborDto labor = LaborDto.builder().id(UUID.randomUUID()).code("LAB-01").name("Labor")
                .dotacionEstandar(new BigDecimal("-1")).build();

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> service.create(labor));
        assertEquals("La dotación estándar no puede ser negativa.", error.getMessage());
    }

    @Test
    void rechazaCuentaQueNoEsCentroDeCosto() {
        LaborServiceImpl service = service(new ArrayList<>(), null, null, false);
        LaborDto labor = LaborDto.builder().id(UUID.randomUUID()).code("LAB-01").name("Labor")
                .codigoCentroCosto("CUENTA-NORMAL").build();

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> service.create(labor));
        assertEquals("La cuenta seleccionada no está definida como centro de costo.", error.getMessage());
    }

    @SuppressWarnings("unchecked")
    private LaborServiceImpl service(List<Labor> guardadas, UUID cultivoValido, UUID cargoValido, boolean esCentroCosto) {
        LaborWriteDataJPARepository write = (LaborWriteDataJPARepository) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{LaborWriteDataJPARepository.class}, (proxy, method, args) -> {
                    if (method.getName().equals("save")) { guardadas.add((Labor) args[0]); return args[0]; }
                    return null;
                });
        LaborReadDataJPARepository read = (LaborReadDataJPARepository) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{LaborReadDataJPARepository.class}, (proxy, method, args) -> Optional.empty());
        TipoCultivoReadDataJPARepository cultivos = (TipoCultivoReadDataJPARepository) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{TipoCultivoReadDataJPARepository.class}, (proxy, method, args) ->
                method.getName().equals("existsById") && args[0].equals(cultivoValido));
        CargoReadDataJPARepository cargos = (CargoReadDataJPARepository) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{CargoReadDataJPARepository.class}, (proxy, method, args) ->
                method.getName().equals("existsById") && args[0].equals(cargoValido));
        CuentaContableReadDataJPARepository cuentas = (CuentaContableReadDataJPARepository) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{CuentaContableReadDataJPARepository.class}, (proxy, method, args) -> {
                    if (!method.getName().equals("findByCodigoAndActivoTrue")) return Optional.empty();
                    CuentaContable cuenta = new CuentaContable();
                    cuenta.setEsCentroCosto(esCentroCosto);
                    return Optional.of(cuenta);
                });
        return new LaborServiceImpl(write, read, cultivos, cargos, cuentas);
    }
}
