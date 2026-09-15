package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.report.infrastructure.entity.Reporte;
import com.kynsoft.report.infrastructure.repository.command.ReporteWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DiaTrabajoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ReporteReadDataJPARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReporteServiceImpl Unit Tests")
class ReporteServiceImplTest {

    @Mock
    private ReporteWriteDataJPARepository repositoryCommand;

    @Mock
    private ReporteReadDataJPARepository repositoryQuery;

    @Mock
    private DiaTrabajoReadDataJPARepository diaTrabajoRepository;

    @InjectMocks
    private ReporteServiceImpl reporteService;

    private UUID reporteId;
    private Reporte reporte;

    @BeforeEach
    void setUp() {
        reporteId = UUID.randomUUID();
        reporte = new Reporte();
        reporte.setId(reporteId);
        reporte.setBloque("3");
        reporte.setCampo("2");
        reporte.setArea("0.6");
        reporte.setNorma("1.5");
        reporte.setYear("2026");
        reporte.setMes("Agosto");
        reporte.setCodigo("2026_08_01");
        reporte.setActivo(true);
    }

    @Nested
    @DisplayName("RN-01: generateCodigo() Tests")
    class GenerateCodigoTests {

        @Test
        @DisplayName("Debe generar primer código cuando no existen reportes")
        void debeGenerarPrimerCodigo() {
            // Arrange
            when(repositoryQuery.findMaxCodigoByYearAndMes("2026", "Agosto")).thenReturn(null);

            // Act
            String codigo = reporteService.generateCodigo("2026", "Agosto");

            // Assert
            assertEquals("2026_08_1", codigo);
        }

        @Test
        @DisplayName("Debe generar siguiente código basado en máximo existente")
        void debeGenerarSiguienteCodigo() {
            // Arrange
            when(repositoryQuery.findMaxCodigoByYearAndMes("2026", "Agosto")).thenReturn("2026_08_34");

            // Act
            String codigo = reporteService.generateCodigo("2026", "Agosto");

            // Assert
            assertEquals("2026_08_35", codigo);
        }

        @Test
        @DisplayName("Debe formatear mes con dos dígitos")
        void debeFormatearMesConDosDigitos() {
            // Arrange
            when(repositoryQuery.findMaxCodigoByYearAndMes("2026", "Enero")).thenReturn(null);

            // Act
            String codigo = reporteService.generateCodigo("2026", "Enero");

            // Assert
            assertTrue(codigo.startsWith("2026_01_"));
        }

        @Test
        @DisplayName("RN-01: Debe considerar reportes inactivos en el consecutivo")
        void debeConsiderarReportesInactivosEnConsecutivo() {
            // Arrange - Simula que el max código es 50 aunque muchos estén inactivos
            when(repositoryQuery.findMaxCodigoByYearAndMes("2026", "Agosto")).thenReturn("2026_08_50");

            // Act
            String codigo = reporteService.generateCodigo("2026", "Agosto");

            // Assert - Debe ser 51, no reiniciar
            assertEquals("2026_08_51", codigo);
        }

        @Test
        @DisplayName("Debe manejar diferentes meses correctamente")
        void debeManejarDiferentesMeses() {
            // Arrange & Act & Assert
            when(repositoryQuery.findMaxCodigoByYearAndMes("2026", "Diciembre")).thenReturn(null);
            assertEquals("2026_12_1", reporteService.generateCodigo("2026", "Diciembre"));

            when(repositoryQuery.findMaxCodigoByYearAndMes("2026", "Marzo")).thenReturn(null);
            assertEquals("2026_03_1", reporteService.generateCodigo("2026", "Marzo"));
        }
    }

    @Nested
    @DisplayName("RN-08: Soft Delete Tests")
    class SoftDeleteTests {

        @Test
        @DisplayName("Debe marcar reporte como inactivo al eliminar")
        void debeMarcarReporteInactivoAlEliminar() {
            // Arrange
            when(repositoryQuery.findById(reporteId)).thenReturn(Optional.of(reporte));
            when(repositoryCommand.save(any(Reporte.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            reporteService.delete(reporteId);

            // Assert
            verify(repositoryCommand).save(argThat(r -> !r.getActivo()));
        }

        @Test
        @DisplayName("Debe lanzar excepción si reporte no existe")
        void debeLanzarExcepcionSiReporteNoExiste() {
            // Arrange
            when(repositoryQuery.findById(reporteId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> reporteService.delete(reporteId)
            );
        }
    }

    @Nested
    @DisplayName("findById() Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Debe retornar DTO cuando reporte existe")
        void debeRetornarDtoCuandoReporteExiste() {
            // Arrange
            when(repositoryQuery.findById(reporteId)).thenReturn(Optional.of(reporte));

            // Act
            var dto = reporteService.findById(reporteId);

            // Assert
            assertNotNull(dto);
            assertEquals(reporteId, dto.getId());
            assertEquals("3", dto.getBloque());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando reporte no existe")
        void debeLanzarExcepcionCuandoReporteNoExiste() {
            // Arrange
            when(repositoryQuery.findById(reporteId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> reporteService.findById(reporteId)
            );
        }
    }

    @Nested
    @DisplayName("update() Tests")
    class UpdateTests {

        @Test
        @DisplayName("Debe actualizar campos básicos del reporte")
        void debeActualizarCamposBasicos() {
            // Arrange
            var dto = reporte.toAggregate();
            dto.setBloque("5");
            dto.setCampo("4");
            dto.setArea("1.2");

            when(repositoryQuery.findById(reporteId)).thenReturn(Optional.of(reporte));
            when(repositoryCommand.save(any(Reporte.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            reporteService.update(dto);

            // Assert
            verify(repositoryCommand).save(argThat(r ->
                    r.getBloque().equals("5") &&
                    r.getCampo().equals("4") &&
                    r.getArea().equals("1.2")
            ));
        }

        @Test
        @DisplayName("Debe lanzar excepción si reporte no existe")
        void debeLanzarExcepcionSiReporteNoExisteEnUpdate() {
            // Arrange
            var dto = reporte.toAggregate();
            when(repositoryQuery.findById(reporteId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> reporteService.update(dto)
            );
        }
    }

    @Nested
    @DisplayName("getConsolidado() Tests")
    class GetConsolidadoTests {

        @Test
        @DisplayName("Debe retornar consolidado vacío cuando no hay días")
        void debeRetornarConsolidadoVacioCuandoNoHayDias() {
            // Arrange
            when(diaTrabajoRepository.findByYearAndMesWithTrabajadores("2026", "Agosto"))
                    .thenReturn(Collections.emptyList());

            // Act
            var consolidado = reporteService.getConsolidado("2026", "Agosto");

            // Assert
            assertNotNull(consolidado);
            assertEquals("2026", consolidado.getYear());
            assertEquals("Agosto", consolidado.getMes());
            assertTrue(consolidado.getTrabajadores().isEmpty());
        }
    }

    /**
     * NOTA: Los siguientes tests documentan el comportamiento de herencia de norma (RN-12).
     * Cuando TrabajadorDia.norma es null, debe usarse Reporte.norma como valor de referencia.
     *
     * Para implementar completamente, se necesitaría:
     * 1. Cambiar el tipo de horas y norma de String a Double
     * 2. Añadir lógica de cálculo que considere la norma heredada
     *
     * Estos tests servirán como especificación cuando se implemente.
     */

    // @Nested
    // @DisplayName("RN-12: Norma heredada del reporte")
    // class NormaHeredadaTests {
    //
    //     @Test
    //     @DisplayName("Debe usar norma del reporte cuando TrabajadorDia.norma es null")
    //     void debeUsarNormaDelReporteCuandoTrabajadorDiaNormaEsNull() {
    //         // Este test valida el comportamiento esperado de herencia de norma
    //         // Implementación pendiente cuando se cambie a tipos Double
    //     }
    //
    //     @Test
    //     @DisplayName("Debe usar norma específica cuando TrabajadorDia.norma está definida")
    //     void debeUsarNormaEspecificaCuandoDefinida() {
    //         // Este test valida que la norma específica tiene prioridad
    //         // Implementación pendiente cuando se cambie a tipos Double
    //     }
    // }
}
