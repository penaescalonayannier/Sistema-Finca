package com.kynsoft.report.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.kynsoft.report.domain.dto.CriterioEvaluacionDto;
import com.kynsoft.report.infrastructure.repository.command.CriterioEvaluacionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CriterioEvaluacionReadDataJPARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CriterioEvaluacionServiceImplTest {
    @Mock private CriterioEvaluacionWriteDataJPARepository writeRepository;
    @Mock private CriterioEvaluacionReadDataJPARepository readRepository;
    @Mock private AuditoriaTransaccionalService auditoria;
    private CriterioEvaluacionServiceImpl service;

    @BeforeEach void setUp() { service = new CriterioEvaluacionServiceImpl(writeRepository, readRepository, auditoria); }

    @Test
    void requiereNombreParaNoCrearCriteriosAmbiguos() {
        assertThrows(IllegalArgumentException.class, () -> service.guardar(CriterioEvaluacionDto.builder().build()));
    }

    @Test
    void asignaIdentificadorYActivoPorDefecto() {
        when(writeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        CriterioEvaluacionDto guardado = service.guardar(CriterioEvaluacionDto.builder().nombre("Resultados").build());
        assertEquals("Resultados", guardado.getNombre());
        assertEquals(true, guardado.getActivo());
    }
}
