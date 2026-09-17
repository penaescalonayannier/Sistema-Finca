package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.HistorialSalarioDto;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.infrastructure.entity.HistorialSalario;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.repository.command.HistorialSalarioWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.HistorialSalarioReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
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
class HistorialSalarioServiceImplTest {
    @Mock private HistorialSalarioWriteDataJPARepository write;
    @Mock private HistorialSalarioReadDataJPARepository read;
    @Mock private TrabajadorReadDataJPARepository trabajadores;
    @Mock private AuditoriaTransaccionalService auditoria;
    private HistorialSalarioServiceImpl service;
    private UUID trabajadorId;

    @BeforeEach
    void preparar() {
        service = new HistorialSalarioServiceImpl(write, read, trabajadores, auditoria);
        trabajadorId = UUID.randomUUID();
        Trabajador trabajador = new Trabajador();
        trabajador.setId(trabajadorId);
        trabajador.setFincaId(UUID.randomUUID());
        trabajador.setCargoId(UUID.randomUUID());
        when(trabajadores.findById(trabajadorId)).thenReturn(Optional.of(trabajador));
        when(read.existsByTrabajadorIdAndFechaVigencia(any(), any())).thenReturn(false);
        when(write.save(any(HistorialSalario.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void registraNuevaVigenciaComoInstantaneaAuditada() {
        UUID id = service.registrar(HistorialSalarioDto.builder().trabajadorId(trabajadorId)
                .fechaVigencia(LocalDate.of(2026, 9, 1)).salarioEscala(new BigDecimal("2500.50"))
                .anticipoDiario(new BigDecimal("10")).tasa(BigDecimal.ZERO).motivo("Promoción").build());
        ArgumentCaptor<HistorialSalario> captor = ArgumentCaptor.forClass(HistorialSalario.class);
        verify(write).save(captor.capture());
        assertEquals(id, captor.getValue().getId());
        assertEquals("ACTIVO", captor.getValue().getEstado());
        assertEquals(new BigDecimal("2500.50"), captor.getValue().getSalarioEscala());
        verify(auditoria).registrarDespuesDeConfirmar(eq(TipoAccion.CREATE), eq("HISTORIAL_SALARIAL"),
                eq(id), anyString(), isNull(), any());
    }

    @Test
    void rechazaVigenciaDuplicadaONegativa() {
        LocalDate fecha = LocalDate.of(2026, 9, 1);
        when(read.existsByTrabajadorIdAndFechaVigencia(trabajadorId, fecha)).thenReturn(true);
        HistorialSalarioDto duplicado = HistorialSalarioDto.builder().trabajadorId(trabajadorId)
                .fechaVigencia(fecha).salarioEscala(BigDecimal.ONE).motivo("Ajuste").build();
        assertThrows(IllegalArgumentException.class, () -> service.registrar(duplicado));
        HistorialSalarioDto negativo = HistorialSalarioDto.builder().trabajadorId(trabajadorId)
                .fechaVigencia(LocalDate.of(2026, 9, 2)).salarioEscala(BigDecimal.valueOf(-1)).motivo("Error").build();
        assertThrows(IllegalArgumentException.class, () -> service.registrar(negativo));
    }

    @Test
    void anulaSinBorrarElRegistroYAudita() {
        HistorialSalario registro = new HistorialSalario();
        registro.setId(UUID.randomUUID()); registro.setTrabajadorId(trabajadorId);
        registro.setFincaId(UUID.randomUUID()); registro.setFechaVigencia(LocalDate.now());
        registro.setSalarioEscala(BigDecimal.ONE); registro.setAnticipoDiario(BigDecimal.ZERO);
        registro.setTasa(BigDecimal.ZERO); registro.setMotivo("Corrección"); registro.setEstado("ACTIVO");
        when(read.findById(registro.getId())).thenReturn(Optional.of(registro));
        service.anular(registro.getId(), "Documento incorrecto");
        assertEquals("ANULADO", registro.getEstado());
        verify(write).save(registro);
        verify(auditoria).registrarDespuesDeConfirmar(eq(TipoAccion.UPDATE), eq("HISTORIAL_SALARIAL"),
                eq(registro.getId()), anyString(), any(), any());
    }
}
