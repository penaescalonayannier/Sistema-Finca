package com.kynsoft.report.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kynsoft.report.domain.dto.EstadoEvaluacion;
import com.kynsoft.report.domain.dto.EvaluacionDto;
import com.kynsoft.report.infrastructure.entity.Evaluacion;
import com.kynsoft.report.infrastructure.repository.command.EvaluacionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.EvaluacionReadDataJPARepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class EvaluacionServiceImplTest {

    @Mock private EvaluacionWriteDataJPARepository writeRepository;
    @Mock private EvaluacionReadDataJPARepository readRepository;
    @Mock private AuditoriaTransaccionalService auditoria;
    private EvaluacionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EvaluacionServiceImpl(writeRepository, readRepository, auditoria);
    }

    @Test
    void creaComoBorradorCuandoElFlujoVigenteNoIndicaEstado() {
        EvaluacionDto dto = dto(EstadoEvaluacion.BORRADOR);
        dto.setEstado(null);
        when(writeRepository.save(any(Evaluacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.create(dto);

        verify(writeRepository).save(any(Evaluacion.class));
        assertEquals(EstadoEvaluacion.BORRADOR, dto.getEstado());
    }

    @Test
    void noPermiteEditarUnaEvaluacionCerrada() {
        Evaluacion cerrada = new Evaluacion(dto(EstadoEvaluacion.CERRADA));
        when(readRepository.findById(cerrada.getId())).thenReturn(Optional.of(cerrada));

        assertThrows(IllegalStateException.class, () -> service.update(dto(EstadoEvaluacion.BORRADOR, cerrada.getId())));
        verify(writeRepository, never()).save(any(Evaluacion.class));
    }

    @Test
    void noPermiteEditarUnaEvaluacionAnulada() {
        Evaluacion anulada = new Evaluacion(dto(EstadoEvaluacion.ANULADA));
        when(readRepository.findById(anulada.getId())).thenReturn(Optional.of(anulada));

        assertThrows(IllegalStateException.class, () -> service.update(dto(EstadoEvaluacion.BORRADOR, anulada.getId())));
        verify(writeRepository, never()).save(any(Evaluacion.class));
    }

    @Test
    void exigeAmbasConstanciasAlCerrar() {
        Evaluacion enviada = new Evaluacion(dto(EstadoEvaluacion.ENVIADA));
        when(readRepository.findById(enviada.getId())).thenReturn(Optional.of(enviada));

        assertThrows(IllegalArgumentException.class,
                () -> service.cambiarEstado(enviada.getId(), EstadoEvaluacion.CERRADA, "Jefe", "", ""));
        verify(writeRepository, never()).save(any(Evaluacion.class));
    }

    @Test
    void cierraYRegistraFechasCuandoElCicloEstaCompleto() {
        Evaluacion enviada = new Evaluacion(dto(EstadoEvaluacion.ENVIADA));
        when(readRepository.findById(enviada.getId())).thenReturn(Optional.of(enviada));
        when(writeRepository.save(any(Evaluacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EvaluacionDto resultado = service.cambiarEstado(enviada.getId(), EstadoEvaluacion.CERRADA,
                "Constancia jefe", "Constancia trabajador", "Cierre revisado");

        assertEquals(EstadoEvaluacion.CERRADA, resultado.getEstado());
        assertEquals("Cierre revisado", resultado.getObservacionesCierre());
        assertEquals("Constancia jefe", resultado.getConstanciaJefe());
        assertEquals("Constancia trabajador", resultado.getConstanciaTrabajador());
        verify(writeRepository).save(enviada);
    }

    @Test
    void noPermiteSaltarDeBorradorACerrada() {
        Evaluacion borrador = new Evaluacion(dto(EstadoEvaluacion.BORRADOR));
        when(readRepository.findById(borrador.getId())).thenReturn(Optional.of(borrador));

        assertThrows(IllegalArgumentException.class,
                () -> service.cambiarEstado(borrador.getId(), EstadoEvaluacion.CERRADA, "Jefe", "Trabajador", ""));
    }

    private EvaluacionDto dto(EstadoEvaluacion estado) {
        return dto(estado, UUID.randomUUID());
    }

    private EvaluacionDto dto(EstadoEvaluacion estado, UUID id) {
        return EvaluacionDto.builder().id(id).trabajadorId(UUID.randomUUID()).jefeId(UUID.randomUUID())
                .mes("Enero").year(2026).calificacion(5).comentarios("Correcta")
                .fechaEvaluacion(java.time.LocalDateTime.now()).estado(estado).build();
    }
}
