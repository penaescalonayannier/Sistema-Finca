package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.AreaTrabajoDto;
import com.kynsoft.report.domain.dto.Rol;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.dto.TipoAreaTrabajo;
import com.kynsoft.report.infrastructure.entity.AreaTrabajo;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.repository.command.AreaTrabajoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AreaTrabajoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantInfo;
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
class AreaTrabajoServiceImplTest {
    @Mock private AreaTrabajoWriteDataJPARepository write;
    @Mock private AreaTrabajoReadDataJPARepository read;
    @Mock private FincaReadDataJPARepository fincas;
    @Mock private TrabajadorReadDataJPARepository trabajadores;
    @Mock private AuditoriaTransaccionalService auditoria;
    private AreaTrabajoServiceImpl service;
    private UUID fincaId;

    @BeforeEach void preparar() {
        service = new AreaTrabajoServiceImpl(write, read, fincas, trabajadores, auditoria);
        fincaId = UUID.randomUUID();
        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());
        when(fincas.existsById(fincaId)).thenReturn(true);
        when(read.existsByFincaIdAndCodigo(fincaId, "ADM")).thenReturn(false);
        when(write.save(any(AreaTrabajo.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }
    @AfterEach void limpiar() { TenantContext.clear(); }

    @Test void creaAreaAuditadaConDatosObligatorios() {
        AreaTrabajoDto resultado = service.create(AreaTrabajoDto.builder().fincaId(fincaId).codigo("ADM")
                .nombre("Administración").tipo(TipoAreaTrabajo.AREA).build());
        ArgumentCaptor<AreaTrabajo> captor = ArgumentCaptor.forClass(AreaTrabajo.class);
        verify(write).save(captor.capture());
        assertEquals("Administración", captor.getValue().getNombre());
        assertEquals("ADM", resultado.getCodigo());
        verify(auditoria).registrarDespuesDeConfirmar(eq(TipoAccion.CREATE), eq("AREA_TRABAJO"),
                eq(resultado.getId()), anyString(), isNull(), any());
    }

    @Test void rechazaAreaSuperiorDeOtraFinca() {
        AreaTrabajo superior = new AreaTrabajo(AreaTrabajoDto.builder().id(UUID.randomUUID()).fincaId(UUID.randomUUID())
                .codigo("OTRA").nombre("Otra").tipo(TipoAreaTrabajo.AREA).activo(true).build());
        when(read.findById(superior.getId())).thenReturn(Optional.of(superior));
        AreaTrabajoDto dto = AreaTrabajoDto.builder().fincaId(fincaId).codigo("ADM").nombre("Administración")
                .tipo(TipoAreaTrabajo.AREA).areaPadreId(superior.getId()).build();
        assertThrows(IllegalArgumentException.class, () -> service.create(dto));
    }

    @Test void rechazaResponsableInactivo() {
        UUID responsableId = UUID.randomUUID(); Trabajador trabajador = new Trabajador(); trabajador.setId(responsableId);
        trabajador.setFincaId(fincaId); trabajador.setActivo(false);
        when(trabajadores.findById(responsableId)).thenReturn(Optional.of(trabajador));
        AreaTrabajoDto dto = AreaTrabajoDto.builder().fincaId(fincaId).codigo("ADM").nombre("Administración")
                .tipo(TipoAreaTrabajo.AREA).responsableId(responsableId).build();
        assertThrows(IllegalArgumentException.class, () -> service.create(dto));
    }
}
