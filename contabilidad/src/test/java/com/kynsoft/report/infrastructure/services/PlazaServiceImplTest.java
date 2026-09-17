package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.PlazaDto;
import com.kynsoft.report.domain.dto.Rol;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.infrastructure.entity.Plaza;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.repository.command.PlazaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.TrabajadorWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AreaTrabajoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CargoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.GrupoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.PlazaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantInfo;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlazaServiceImplTest {
    @Mock private PlazaWriteDataJPARepository write;
    @Mock private PlazaReadDataJPARepository read;
    @Mock private FincaReadDataJPARepository fincas;
    @Mock private AreaTrabajoReadDataJPARepository areas;
    @Mock private GrupoReadDataJPARepository grupos;
    @Mock private CargoReadDataJPARepository cargos;
    @Mock private TrabajadorReadDataJPARepository trabajadores;
    @Mock private TrabajadorWriteDataJPARepository trabajadorWrite;
    @Mock private AuditoriaTransaccionalService auditoria;
    private PlazaServiceImpl service;
    private UUID fincaId, cargoId;

    @BeforeEach void preparar() {
        service = new PlazaServiceImpl(write, read, fincas, areas, grupos, cargos, trabajadores, trabajadorWrite, auditoria);
        fincaId = UUID.randomUUID(); cargoId = UUID.randomUUID();
        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());
        when(fincas.existsById(fincaId)).thenReturn(true); when(cargos.existsById(cargoId)).thenReturn(true);
        when(read.existsByFincaIdAndCodigo(fincaId, "P-01")).thenReturn(false);
        when(write.save(any(Plaza.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trabajadores.findByPlazaIdAndActivoTrue(any())).thenReturn(Optional.empty());
    }
    @AfterEach void limpiar() { TenantContext.clear(); }

    @Test void creaPlazaVacanteYAudita() {
        PlazaDto resultado = service.create(PlazaDto.builder().fincaId(fincaId).cargoId(cargoId).codigo("P-01").nombre("Contador").build());
        ArgumentCaptor<Plaza> captor = ArgumentCaptor.forClass(Plaza.class); verify(write).save(captor.capture());
        assertEquals("VACANTE", resultado.getEstado()); assertEquals(cargoId, captor.getValue().getCargoId());
        verify(auditoria).registrarDespuesDeConfirmar(eq(TipoAccion.CREATE), eq("PLAZA"), eq(resultado.getId()), anyString(), isNull(), any());
    }

    @Test void impideAsignarPlazaDeOtroCargoOFueraDeVigencia() {
        UUID plazaId = UUID.randomUUID(); Plaza plaza = new Plaza(PlazaDto.builder().id(plazaId).fincaId(fincaId).cargoId(cargoId).codigo("P-01").activo(true).fechaFin(LocalDate.now().minusDays(1)).build());
        when(read.findById(plazaId)).thenReturn(Optional.of(plaza));
        assertThrows(IllegalArgumentException.class, () -> service.validarAsignacion(plazaId, fincaId, cargoId));
        plaza.setFechaFin(null);
        assertThrows(IllegalArgumentException.class, () -> service.validarAsignacion(plazaId, fincaId, UUID.randomUUID()));
    }

    @Test void asignaSoloTrabajadorCompatible() {
        UUID plazaId = UUID.randomUUID(), trabajadorId = UUID.randomUUID();
        Plaza plaza = new Plaza(PlazaDto.builder().id(plazaId).fincaId(fincaId).cargoId(cargoId).codigo("P-01").activo(true).build());
        Trabajador trabajador = new Trabajador(); trabajador.setId(trabajadorId); trabajador.setFincaId(fincaId); trabajador.setCargoId(cargoId); trabajador.setActivo(true);
        when(write.findById(plazaId)).thenReturn(Optional.of(plaza)); when(read.findById(plazaId)).thenReturn(Optional.of(plaza)); when(trabajadorWrite.findById(trabajadorId)).thenReturn(Optional.of(trabajador));
        when(trabajadores.findByPlazaIdAndActivoTrue(plazaId)).thenReturn(Optional.empty(), Optional.of(trabajador));
        PlazaDto resultado = service.asignarTrabajador(plazaId, trabajadorId);
        assertEquals(trabajadorId, trabajador.getId()); assertEquals(trabajadorId, resultado.getTrabajadorId()); verify(trabajadorWrite).save(trabajador);
    }
}
