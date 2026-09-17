package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.AlcanceFormaNumerada;
import com.kynsoft.report.domain.dto.EmitirFormaNumeradaRequest;
import com.kynsoft.report.domain.dto.ModoEmisionFormaNumerada;
import com.kynsoft.report.domain.dto.ReinicioConsecutivoForma;
import com.kynsoft.report.domain.dto.Rol;
import com.kynsoft.report.infrastructure.entity.EmisionFormaNumerada;
import com.kynsoft.report.infrastructure.entity.FormaNumerada;
import com.kynsoft.report.infrastructure.entity.SerieFormaNumerada;
import com.kynsoft.report.infrastructure.repository.command.EmisionFormaNumeradaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.FormaNumeradaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.SerieFormaNumeradaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.EmisionFormaNumeradaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FormaNumeradaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.SerieFormaNumeradaReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistroFormasNumeradasServiceImplTest {

    @Mock private FormaNumeradaWriteDataJPARepository formaWriteRepository;
    @Mock private SerieFormaNumeradaWriteDataJPARepository serieWriteRepository;
    @Mock private EmisionFormaNumeradaWriteDataJPARepository emisionWriteRepository;
    @Mock private FormaNumeradaReadDataJPARepository formaReadRepository;
    @Mock private SerieFormaNumeradaReadDataJPARepository serieReadRepository;
    @Mock private EmisionFormaNumeradaReadDataJPARepository emisionReadRepository;
    @Mock private EntityManager writeEntityManager;
    @Mock private Query advisoryLockQuery;

    @InjectMocks private RegistroFormasNumeradasServiceImpl service;

    @BeforeEach
    void prepararTenantAdministrador() {
        TenantContext.set(TenantInfo.builder().rol(Rol.ADMIN).build());
    }

    @AfterEach
    void limpiarTenant() {
        TenantContext.clear();
    }

    @Test
    void abreSerieAnualYEmitePrimerNumeroBajoBloqueo() {
        UUID formaId = UUID.randomUUID();
        UUID fincaId = UUID.randomUUID();
        FormaNumerada forma = new FormaNumerada();
        forma.setId(formaId);
        forma.setCodigo("VALE_SALIDA");
        forma.setNombre("Vale de salida");
        forma.setPrefijo("VALE");
        forma.setDigitos(5);
        forma.setActiva(true);
        forma.setReinicio(ReinicioConsecutivoForma.ANUAL);
        forma.setAlcancePredeterminado(AlcanceFormaNumerada.FINCA);
        forma.setModoEmision(ModoEmisionFormaNumerada.SISTEMA);

        when(formaWriteRepository.findByCodigo("VALE_SALIDA")).thenReturn(Optional.of(forma));
        when(writeEntityManager.createNativeQuery(any(String.class))).thenReturn(advisoryLockQuery);
        when(advisoryLockQuery.setParameter(eq("clave"), any(String.class))).thenReturn(advisoryLockQuery);
        when(advisoryLockQuery.getSingleResult()).thenReturn(1);
        when(serieWriteRepository.findActivaForUpdate(eq(formaId), eq(AlcanceFormaNumerada.FINCA),
                eq(fincaId), eq(2026), any())).thenReturn(Optional.empty());
        when(serieWriteRepository.save(any(SerieFormaNumerada.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(emisionWriteRepository.save(any(EmisionFormaNumerada.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var respuesta = service.emitir(new EmitirFormaNumeradaRequest(
                "vale_salida", AlcanceFormaNumerada.FINCA, fincaId, LocalDate.of(2026, 9, 17),
                "SALIDA", UUID.randomUUID(), UUID.randomUUID()));

        assertEquals("VALE-2026-00001", respuesta.getNumeroFormateado());
        ArgumentCaptor<SerieFormaNumerada> serie = ArgumentCaptor.forClass(SerieFormaNumerada.class);
        verify(serieWriteRepository).save(serie.capture());
        assertEquals("VALE", serie.getValue().getPrefijo());
        assertEquals(2026, serie.getValue().getAnio());
        assertEquals(1, serie.getValue().getUltimoNumero());
    }
}
