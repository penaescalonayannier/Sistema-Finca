package com.kynsoft.report.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kynsoft.report.domain.dto.AnularExpedienteDisciplinarioRequest;
import com.kynsoft.report.domain.dto.CrearExpedienteDisciplinarioRequest;
import com.kynsoft.report.domain.dto.EstadoExpedienteDisciplinario;
import com.kynsoft.report.domain.dto.ResolverExpedienteDisciplinarioRequest;
import com.kynsoft.report.domain.dto.Rol;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.dto.TipoIncidenciaDisciplinaria;
import com.kynsoft.report.infrastructure.entity.ExpedienteDisciplinario;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.repository.command.ExpedienteDisciplinarioWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ExpedienteDisciplinarioReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantInfo;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ExpedienteDisciplinarioServiceImplTest {
    private final ExpedienteDisciplinarioWriteDataJPARepository write = mock(ExpedienteDisciplinarioWriteDataJPARepository.class);
    private final ExpedienteDisciplinarioReadDataJPARepository read = mock(ExpedienteDisciplinarioReadDataJPARepository.class);
    private final TrabajadorReadDataJPARepository trabajadores = mock(TrabajadorReadDataJPARepository.class);
    private final FincaReadDataJPARepository fincas = mock(FincaReadDataJPARepository.class);
    private final AuditoriaTransaccionalService auditoria = mock(AuditoriaTransaccionalService.class);
    private final ExpedienteDisciplinarioServiceImpl service = new ExpedienteDisciplinarioServiceImpl(write, read, trabajadores, fincas, auditoria);
    private final UUID fincaId = UUID.randomUUID(); private final UUID trabajadorId = UUID.randomUUID();

    @AfterEach void limpiarTenant() { TenantContext.clear(); }

    @Test void creaBorradorConTrabajadorDeLaFincaYAudiTa() {
        prepararFincaYTrabajador();
        CrearExpedienteDisciplinarioRequest request = solicitud();
        UUID id = service.crear(request);
        ArgumentCaptor<ExpedienteDisciplinario> captor = ArgumentCaptor.forClass(ExpedienteDisciplinario.class);
        verify(write).save(captor.capture());
        assertEquals(id, captor.getValue().getId());
        assertEquals(EstadoExpedienteDisciplinario.BORRADOR, captor.getValue().getEstado());
        verify(auditoria).registrarDespuesDeConfirmar(org.mockito.ArgumentMatchers.eq(TipoAccion.CREATE), org.mockito.ArgumentMatchers.eq("EXPEDIENTE_DISCIPLINARIO"), org.mockito.ArgumentMatchers.eq(id), any(), any(), any());
    }

    @Test void soloNotificadoSePuedeResolverYExigeMedidaYResolucion() {
        prepararFincaYTrabajador();
        ExpedienteDisciplinario e = expediente(EstadoExpedienteDisciplinario.NOTIFICADA);
        e.setAprobadorId(trabajadorId);
        when(write.findByIdForUpdate(e.getId())).thenReturn(Optional.of(e));
        ResolverExpedienteDisciplinarioRequest invalida = new ResolverExpedienteDisciplinarioRequest(); invalida.setResolucion("Concluido");
        assertThrows(IllegalArgumentException.class, () -> service.resolver(e.getId(), invalida));
        ResolverExpedienteDisciplinarioRequest valida = new ResolverExpedienteDisciplinarioRequest(); valida.setMedida("Amonestación"); valida.setResolucion("Notificada y registrada");
        service.resolver(e.getId(), valida);
        assertEquals(EstadoExpedienteDisciplinario.RESUELTA, e.getEstado());
        verify(write).save(e);
    }

    @Test void noAnulaUnExpedienteResueltoNiLoElimina() {
        prepararFincaYTrabajador();
        ExpedienteDisciplinario e = expediente(EstadoExpedienteDisciplinario.RESUELTA);
        when(write.findByIdForUpdate(e.getId())).thenReturn(Optional.of(e));
        AnularExpedienteDisciplinarioRequest request = new AnularExpedienteDisciplinarioRequest(); request.setMotivo("Error de captura");
        assertThrows(IllegalArgumentException.class, () -> service.anular(e.getId(), request));
        org.mockito.Mockito.verify(write, org.mockito.Mockito.never()).save(any());
    }

    @Test void rechazaTrabajadorDeOtraFinca() {
        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());
        Finca finca = new Finca(); finca.setId(fincaId); when(fincas.findById(fincaId)).thenReturn(Optional.of(finca));
        Trabajador trabajador = new Trabajador(); trabajador.setId(trabajadorId); trabajador.setFincaId(UUID.randomUUID()); when(trabajadores.findById(trabajadorId)).thenReturn(Optional.of(trabajador));
        assertThrows(IllegalArgumentException.class, () -> service.crear(solicitud()));
    }

    private void prepararFincaYTrabajador() { TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build()); Finca finca = new Finca(); finca.setId(fincaId); when(fincas.findById(fincaId)).thenReturn(Optional.of(finca)); Trabajador trabajador = new Trabajador(); trabajador.setId(trabajadorId); trabajador.setFincaId(fincaId); trabajador.setNombre("Trabajador"); when(trabajadores.findById(trabajadorId)).thenReturn(Optional.of(trabajador)); }
    private CrearExpedienteDisciplinarioRequest solicitud() { CrearExpedienteDisciplinarioRequest r = new CrearExpedienteDisciplinarioRequest(); r.setFincaId(fincaId); r.setTrabajadorId(trabajadorId); r.setTipo(TipoIncidenciaDisciplinaria.TARDANZA); r.setFecha(LocalDate.now()); r.setDescripcion("Llegada fuera del horario registrado"); return r; }
    private ExpedienteDisciplinario expediente(EstadoExpedienteDisciplinario estado) { ExpedienteDisciplinario e = new ExpedienteDisciplinario(); e.setId(UUID.randomUUID()); e.setFincaId(fincaId); e.setTrabajadorId(trabajadorId); e.setTipo(TipoIncidenciaDisciplinaria.TARDANZA); e.setFecha(LocalDate.now()); e.setDescripcion("Descripción"); e.setEstado(estado); return e; }
}
