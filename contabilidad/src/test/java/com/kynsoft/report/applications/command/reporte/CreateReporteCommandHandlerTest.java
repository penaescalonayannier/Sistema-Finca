package com.kynsoft.report.applications.command.reporte;

import com.kynsoft.report.applications.command.reporte.create.CreateReporteCommand;
import com.kynsoft.report.applications.command.reporte.create.CreateReporteCommandHandler;
import com.kynsoft.report.domain.dto.ReporteDto;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.IReporteService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateReporteCommandHandler Unit Tests")
class CreateReporteCommandHandlerTest {

    @Mock
    private IReporteService reportService;

    @Mock
    private ITrabajadorService trabajadorService;

    @InjectMocks
    private CreateReporteCommandHandler handler;

    private UUID reporteId;
    private UUID trabajadorResponsableId;
    private TrabajadorDto trabajadorResponsable;

    @BeforeEach
    void setUp() {
        reporteId = UUID.randomUUID();
        trabajadorResponsableId = UUID.randomUUID();

        trabajadorResponsable = TrabajadorDto.builder()
                .id(trabajadorResponsableId)
                .nombre("Juan Pérez")
                .ruc("12345678901")
                .activo(true)
                .build();
    }

    private CreateReporteCommand createCommand(UUID id, String bloque, String campo, String area,
                                                String norma, String fecha, String codigo,
                                                String year, String mes, UUID trabajadorId) {
        return new CreateReporteCommand(id, bloque, campo, area, norma, fecha, codigo, year, mes, trabajadorId);
    }

    @Nested
    @DisplayName("RN-01: Generación de código")
    class GeneracionCodigoTests {

        @Test
        @DisplayName("Debe generar código automáticamente al crear reporte")
        void debeGenerarCodigoAutomaticamente() {
            // Arrange
            CreateReporteCommand command = createCommand(
                    reporteId, "3", "2", "0.6", "1.5", null, null, "2026", "Agosto", trabajadorResponsableId
            );

            when(trabajadorService.findById(trabajadorResponsableId)).thenReturn(trabajadorResponsable);
            when(reportService.generateCodigo("2026", "Agosto")).thenReturn("2026_08_35");

            // Act
            handler.handle(command);

            // Assert
            ArgumentCaptor<ReporteDto> captor = ArgumentCaptor.forClass(ReporteDto.class);
            verify(reportService).create(captor.capture());
            assertEquals("2026_08_35", captor.getValue().getCodigo());
        }

        @Test
        @DisplayName("Debe usar año y mes del command para generar código")
        void debeUsarYearYMesDelCommand() {
            // Arrange
            CreateReporteCommand command = createCommand(
                    reporteId, "1", "1", "1.0", "1.0", null, null, "2025", "Enero", trabajadorResponsableId
            );

            when(trabajadorService.findById(trabajadorResponsableId)).thenReturn(trabajadorResponsable);
            when(reportService.generateCodigo("2025", "Enero")).thenReturn("2025_01_01");

            // Act
            handler.handle(command);

            // Assert
            verify(reportService).generateCodigo("2025", "Enero");
        }
    }

    @Nested
    @DisplayName("RN-02: Validación trabajador responsable")
    class ValidacionTrabajadorResponsableTests {

        @Test
        @DisplayName("Debe rechazar si trabajadorResponsableId es null")
        void debeRechazarTrabajadorResponsableNull() {
            // Arrange
            CreateReporteCommand command = createCommand(
                    reporteId, "3", "2", "0.6", "1.5", null, null, "2026", "Agosto", null
            );

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> handler.handle(command)
            );
            assertEquals("El trabajador responsable es obligatorio", exception.getMessage());
        }

        @Test
        @DisplayName("Debe rechazar si trabajador no existe")
        void debeRechazarTrabajadorNoExistente() {
            // Arrange
            CreateReporteCommand command = createCommand(
                    reporteId, "3", "2", "0.6", "1.5", null, null, "2026", "Agosto", trabajadorResponsableId
            );

            when(trabajadorService.findById(trabajadorResponsableId)).thenReturn(null);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> handler.handle(command)
            );
            assertEquals("El trabajador seleccionado no existe", exception.getMessage());
        }

        @Test
        @DisplayName("Debe guardar nombre del trabajador responsable")
        void debeGuardarNombreTrabajadorResponsable() {
            // Arrange
            CreateReporteCommand command = createCommand(
                    reporteId, "3", "2", "0.6", "1.5", null, null, "2026", "Agosto", trabajadorResponsableId
            );

            when(trabajadorService.findById(trabajadorResponsableId)).thenReturn(trabajadorResponsable);
            when(reportService.generateCodigo("2026", "Agosto")).thenReturn("2026_08_01");

            // Act
            handler.handle(command);

            // Assert
            ArgumentCaptor<ReporteDto> captor = ArgumentCaptor.forClass(ReporteDto.class);
            verify(reportService).create(captor.capture());
            assertEquals("Juan Pérez", captor.getValue().getTrabajadorResponsableNombre());
        }
    }

    @Nested
    @DisplayName("RN-03: Campos obligatorios")
    class CamposObligatoriosTests {

        @Test
        @DisplayName("Debe crear reporte con todos los campos requeridos")
        void debeCrearReporteConCamposRequeridos() {
            // Arrange
            CreateReporteCommand command = createCommand(
                    reporteId, "3", "2", "0.6", "1.5", "2026-08-21", null, "2026", "Agosto", trabajadorResponsableId
            );

            when(trabajadorService.findById(trabajadorResponsableId)).thenReturn(trabajadorResponsable);
            when(reportService.generateCodigo("2026", "Agosto")).thenReturn("2026_08_01");

            // Act
            handler.handle(command);

            // Assert
            ArgumentCaptor<ReporteDto> captor = ArgumentCaptor.forClass(ReporteDto.class);
            verify(reportService).create(captor.capture());
            ReporteDto dto = captor.getValue();

            assertEquals("3", dto.getBloque());
            assertEquals("2", dto.getCampo());
            assertEquals("0.6", dto.getArea());
            assertEquals("1.5", dto.getNorma());
            assertEquals("2026", dto.getYear());
            assertEquals("Agosto", dto.getMes());
        }
    }

    @Nested
    @DisplayName("Creación exitosa")
    class CreacionExitosaTests {

        @Test
        @DisplayName("Debe llamar reportService.create una vez")
        void debeLlamarCreateUnaVez() {
            // Arrange
            CreateReporteCommand command = createCommand(
                    reporteId, "1", "1", "1.0", "1.0", null, null, "2026", "Agosto", trabajadorResponsableId
            );

            when(trabajadorService.findById(trabajadorResponsableId)).thenReturn(trabajadorResponsable);
            when(reportService.generateCodigo("2026", "Agosto")).thenReturn("2026_08_01");

            // Act
            handler.handle(command);

            // Assert
            verify(reportService, times(1)).create(any(ReporteDto.class));
        }

        @Test
        @DisplayName("Debe propagar ID del command al DTO")
        void debePropagarIdDelCommand() {
            // Arrange
            CreateReporteCommand command = createCommand(
                    reporteId, "1", "1", "1.0", "1.0", null, null, "2026", "Agosto", trabajadorResponsableId
            );

            when(trabajadorService.findById(trabajadorResponsableId)).thenReturn(trabajadorResponsable);
            when(reportService.generateCodigo("2026", "Agosto")).thenReturn("2026_08_01");

            // Act
            handler.handle(command);

            // Assert
            ArgumentCaptor<ReporteDto> captor = ArgumentCaptor.forClass(ReporteDto.class);
            verify(reportService).create(captor.capture());
            assertEquals(reporteId, captor.getValue().getId());
        }
    }
}
