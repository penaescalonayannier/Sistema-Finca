package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.TrabajadorDiaDto;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.infrastructure.entity.DiaTrabajo;
import com.kynsoft.report.infrastructure.entity.Reporte;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.entity.TrabajadorDia;
import com.kynsoft.report.infrastructure.repository.command.TrabajadorDiaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DiaTrabajoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorDiaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrabajadorDiaServiceImplTest {
    @Mock private TrabajadorDiaWriteDataJPARepository writeRepository;
    @Mock private TrabajadorDiaReadDataJPARepository readRepository;
    @Mock private DiaTrabajoReadDataJPARepository diaRepository;
    @Mock private TrabajadorReadDataJPARepository trabajadorRepository;
    @Mock private AuditoriaTransaccionalService auditoria;
    @InjectMocks private TrabajadorDiaServiceImpl service;

    private UUID diaId;
    private UUID trabajadorId;
    private UUID jornadaId;

    @BeforeEach
    void preparar() {
        diaId = UUID.randomUUID();
        trabajadorId = UUID.randomUUID();
        jornadaId = UUID.randomUUID();
        Reporte reporte = new Reporte();
        reporte.setId(UUID.randomUUID());
        DiaTrabajo dia = new DiaTrabajo();
        dia.setId(diaId);
        dia.setReporte(reporte);
        Trabajador trabajador = new Trabajador();
        trabajador.setId(trabajadorId);
        when(diaRepository.findById(diaId)).thenReturn(Optional.of(dia));
        when(trabajadorRepository.findById(trabajadorId)).thenReturn(Optional.of(trabajador));
        when(readRepository.findByDiaTrabajoIdAndTrabajadorId(diaId, trabajadorId)).thenReturn(Optional.empty());
        when(writeRepository.save(any(TrabajadorDia.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void normalizaJornadaYPublicaAuditoriaDespuesDeConfirmar() {
        service.create(TrabajadorDiaDto.builder().id(jornadaId).diaTrabajoId(diaId).trabajadorId(trabajadorId)
                .horas(" 7,5000 ").norma(" 2,2500 ").build());

        verify(writeRepository).save(org.mockito.ArgumentMatchers.argThat(jornada ->
                "7.5".equals(jornada.getHoras()) && "2.25".equals(jornada.getNorma())));
        verify(auditoria).registrarDespuesDeConfirmar(eq(TipoAccion.CREATE), eq("TRABAJADOR_DIA"),
                eq(jornadaId), anyString(), isNull(), any());
    }

    @Test
    void actualizaConDecimalesYConservaAuditoriaDeAntesYDespues() {
        DiaTrabajo dia = diaRepository.findById(diaId).orElseThrow();
        Trabajador trabajador = trabajadorRepository.findById(trabajadorId).orElseThrow();
        TrabajadorDia existente = new TrabajadorDia();
        existente.setId(jornadaId);
        existente.setDiaTrabajo(dia);
        existente.setTrabajador(trabajador);
        existente.setHoras("2");
        existente.setNorma("1");
        when(readRepository.findById(jornadaId)).thenReturn(Optional.of(existente));

        service.update(TrabajadorDiaDto.builder().id(jornadaId).horas(" 3,2500 ").norma(" ").build());

        verify(writeRepository).save(org.mockito.ArgumentMatchers.argThat(jornada ->
                "3.25".equals(jornada.getHoras()) && jornada.getNorma() == null));
        verify(auditoria).registrarDespuesDeConfirmar(eq(TipoAccion.UPDATE), eq("TRABAJADOR_DIA"),
                eq(jornadaId), anyString(), any(), any());
    }

    @Test
    void eliminaSoloDespuesDeValidarElContextoYDejaTraza() {
        DiaTrabajo dia = diaRepository.findById(diaId).orElseThrow();
        Trabajador trabajador = trabajadorRepository.findById(trabajadorId).orElseThrow();
        TrabajadorDia existente = new TrabajadorDia();
        existente.setId(jornadaId);
        existente.setDiaTrabajo(dia);
        existente.setTrabajador(trabajador);
        existente.setHoras("2");
        when(readRepository.findById(jornadaId)).thenReturn(Optional.of(existente));

        service.delete(jornadaId);

        verify(writeRepository).deleteById(jornadaId);
        verify(auditoria).registrarDespuesDeConfirmar(eq(TipoAccion.DELETE), eq("TRABAJADOR_DIA"),
                eq(jornadaId), anyString(), any(), isNull());
    }
}
