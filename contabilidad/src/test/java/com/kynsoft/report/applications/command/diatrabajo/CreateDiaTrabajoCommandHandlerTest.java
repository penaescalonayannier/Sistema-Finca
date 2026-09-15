package com.kynsoft.report.applications.command.diatrabajo;

import com.kynsoft.report.applications.command.diatrabajo.create.CreateDiaTrabajoCommand;
import com.kynsoft.report.applications.command.diatrabajo.create.CreateDiaTrabajoCommandHandler;
import com.kynsoft.report.domain.dto.DiaTrabajoDto;
import com.kynsoft.report.domain.services.IDiaTrabajoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateDiaTrabajoCommandHandler Unit Tests")
class CreateDiaTrabajoCommandHandlerTest {

    @Mock
    private IDiaTrabajoService diaTrabajoService;

    @InjectMocks
    private CreateDiaTrabajoCommandHandler handler;

    private UUID diaTrabajoId;
    private UUID reporteId;

    @BeforeEach
    void setUp() {
        diaTrabajoId = UUID.randomUUID();
        reporteId = UUID.randomUUID();
    }

    private CreateDiaTrabajoCommand createCommand(UUID id, UUID reporteId, LocalDate fecha) {
        return new CreateDiaTrabajoCommand(id, reporteId, fecha);
    }

    @Nested
    @DisplayName("Creación básica")
    class CreacionBasicaTests {

        @Test
        @DisplayName("Debe crear día de trabajo con datos válidos")
        void debeCrearDiaTrabajoConDatosValidos() {
            // Arrange
            LocalDate fecha = LocalDate.of(2026, 8, 20); // Jueves
            CreateDiaTrabajoCommand command = createCommand(diaTrabajoId, reporteId, fecha);

            // Act
            handler.handle(command);

            // Assert
            ArgumentCaptor<DiaTrabajoDto> captor = ArgumentCaptor.forClass(DiaTrabajoDto.class);
            verify(diaTrabajoService).create(captor.capture());
            assertEquals(diaTrabajoId, captor.getValue().getId());
            assertEquals(reporteId, captor.getValue().getReporteId());
            assertEquals(fecha, captor.getValue().getFecha());
        }

        @Test
        @DisplayName("Debe propagar ID del command al DTO")
        void debePropagarIdDelCommand() {
            // Arrange
            LocalDate fecha = LocalDate.of(2026, 8, 21);
            CreateDiaTrabajoCommand command = createCommand(diaTrabajoId, reporteId, fecha);

            // Act
            handler.handle(command);

            // Assert
            ArgumentCaptor<DiaTrabajoDto> captor = ArgumentCaptor.forClass(DiaTrabajoDto.class);
            verify(diaTrabajoService).create(captor.capture());
            assertEquals(diaTrabajoId, captor.getValue().getId());
        }

        @Test
        @DisplayName("Debe llamar diaTrabajoService.create una vez")
        void debeLlamarCreateUnaVez() {
            // Arrange
            LocalDate fecha = LocalDate.of(2026, 8, 19);
            CreateDiaTrabajoCommand command = createCommand(diaTrabajoId, reporteId, fecha);

            // Act
            handler.handle(command);

            // Assert
            verify(diaTrabajoService, times(1)).create(any(DiaTrabajoDto.class));
        }
    }

    /**
     * NOTA: Los siguientes tests están comentados porque la validación de horas
     * aún no está implementada en el handler. Cuando se implemente RN-07,
     * descomentar estos tests.
     *
     * RN-07: Horas válidas por tipo de día
     * - Lunes a Viernes: máximo 8 horas
     * - Sábado: máximo 4 horas
     * - Domingo: 0 horas (no se permite trabajo)
     *
     * Para implementar esta validación, se necesita:
     * 1. Agregar lista de trabajadores al CreateDiaTrabajoCommand
     * 2. Validar horas según el día de la semana antes de crear
     * 3. Usar fecha.getDayOfWeek() para determinar el tipo de día
     */

    // @Nested
    // @DisplayName("RN-07: Validación de horas por tipo de día")
    // class ValidacionHorasPorTipoDeDiaTests {
    //
    //     @Test
    //     @DisplayName("Debe permitir hasta 8 horas en día laboral (lunes-viernes)")
    //     void debePermitirHasta8HorasEnDiaLaboral() {
    //         LocalDate miercoles = LocalDate.of(2026, 8, 19);
    //         assertEquals(DayOfWeek.WEDNESDAY, miercoles.getDayOfWeek());
    //         // ... test implementation
    //     }
    //
    //     @Test
    //     @DisplayName("Debe rechazar más de 8 horas en día laboral")
    //     void debeRechazarMasDe8HorasEnDiaLaboral() {
    //         // ... test implementation
    //     }
    //
    //     @Test
    //     @DisplayName("Debe permitir hasta 4 horas en sábado")
    //     void debePermitirHasta4HorasEnSabado() {
    //         LocalDate sabado = LocalDate.of(2026, 8, 22);
    //         assertEquals(DayOfWeek.SATURDAY, sabado.getDayOfWeek());
    //         // ... test implementation
    //     }
    //
    //     @Test
    //     @DisplayName("Debe rechazar más de 4 horas en sábado")
    //     void debeRechazarMasDe4HorasEnSabado() {
    //         // ... test implementation
    //     }
    //
    //     @Test
    //     @DisplayName("Debe rechazar cualquier hora en domingo")
    //     void debeRechazarCualquierHoraEnDomingo() {
    //         LocalDate domingo = LocalDate.of(2026, 8, 23);
    //         assertEquals(DayOfWeek.SUNDAY, domingo.getDayOfWeek());
    //         // ... test implementation
    //     }
    // }
}
