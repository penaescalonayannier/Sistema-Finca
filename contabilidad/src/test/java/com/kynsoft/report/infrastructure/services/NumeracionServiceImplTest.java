package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.TipoDocumento;
import com.kynsoft.report.infrastructure.entity.ConfiguracionNumeracion;
import com.kynsoft.report.infrastructure.repository.command.ConfiguracionNumeracionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ConfiguracionNumeracionReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ProduccionTerminadaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.SalidaReadDataJPARepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NumeracionServiceImplTest {

    @Mock private ConfiguracionNumeracionReadDataJPARepository readRepository;
    @Mock private ConfiguracionNumeracionWriteDataJPARepository writeRepository;
    @Mock private SalidaReadDataJPARepository salidaReadRepository;
    @Mock private ProduccionTerminadaReadDataJPARepository produccionReadRepository;
    @Mock private EntityManager writeEntityManager;
    @Mock private Query advisoryLockQuery;

    @InjectMocks private NumeracionServiceImpl service;

    @Test
    void generaElSiguienteConsecutivoBloqueandoLaSecuenciaExistente() {
        UUID fincaId = UUID.randomUUID();
        int anio = LocalDate.now().getYear();
        ConfiguracionNumeracion configuracion = new ConfiguracionNumeracion();
        configuracion.setFincaId(fincaId);
        configuracion.setTipo(TipoDocumento.PRODUCCION);
        configuracion.setPrefijo("PT");
        configuracion.setAnio(anio);
        configuracion.setUltimoNumero(7);

        when(writeEntityManager.createNativeQuery(any(String.class))).thenReturn(advisoryLockQuery);
        when(advisoryLockQuery.setParameter(eq("clave"), any(String.class))).thenReturn(advisoryLockQuery);
        when(advisoryLockQuery.getSingleResult()).thenReturn(1);
        when(writeRepository.findByFincaIdAndTipoAndAnioForUpdate(fincaId, TipoDocumento.PRODUCCION, anio))
                .thenReturn(Optional.of(configuracion));

        String numero = service.generarSiguienteNumero(fincaId, TipoDocumento.PRODUCCION);

        assertEquals("PT-" + anio + "-00008", numero);
        assertEquals(8, configuracion.getUltimoNumero());
        verify(writeRepository).findByFincaIdAndTipoAndAnioForUpdate(fincaId, TipoDocumento.PRODUCCION, anio);
        verify(writeRepository).save(configuracion);
    }

    @Test
    void iniciaLaSecuenciaDeProduccionCuandoAunNoHayConfiguracion() {
        UUID fincaId = UUID.randomUUID();
        int anio = LocalDate.now().getYear();

        when(writeEntityManager.createNativeQuery(any(String.class))).thenReturn(advisoryLockQuery);
        when(advisoryLockQuery.setParameter(eq("clave"), any(String.class))).thenReturn(advisoryLockQuery);
        when(advisoryLockQuery.getSingleResult()).thenReturn(1);
        when(writeRepository.findByFincaIdAndTipoAndAnioForUpdate(fincaId, TipoDocumento.PRODUCCION, anio))
                .thenReturn(Optional.empty());
        when(writeRepository.save(any(ConfiguracionNumeracion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String numero = service.generarSiguienteNumero(fincaId, TipoDocumento.PRODUCCION);

        assertEquals("PT-" + anio + "-00001", numero);
        verify(writeRepository, times(2)).save(any(ConfiguracionNumeracion.class));
    }
}
