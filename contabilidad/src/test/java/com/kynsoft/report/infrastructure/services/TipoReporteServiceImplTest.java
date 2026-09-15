package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.CategoriaTipoCultivo;
import com.kynsoft.report.domain.dto.TipoReporteDto;
import com.kynsoft.report.domain.dto.TipoSubclasificacion;
import com.kynsoft.report.infrastructure.entity.TipoReporte;
import com.kynsoft.report.infrastructure.repository.command.TipoReporteWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TipoReporteReadDataJPARepository;
import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TipoReporteServiceImpl Unit Tests")
class TipoReporteServiceImplTest {

    @Mock
    private TipoReporteWriteDataJPARepository writeRepository;

    @Mock
    private TipoReporteReadDataJPARepository readRepository;

    @InjectMocks
    private TipoReporteServiceImpl tipoReporteService;

    private UUID tipoReporteId;
    private UUID tipoCultivoAutoId;
    private TipoReporte tipoReporte;
    private TipoReporteDto tipoReporteDto;

    @BeforeEach
    void setUp() {
        tipoReporteId = UUID.randomUUID();
        tipoCultivoAutoId = UUID.randomUUID();

        tipoReporte = new TipoReporte();
        tipoReporte.setId(tipoReporteId);
        tipoReporte.setCodigo("REPORTE_CANNA");
        tipoReporte.setNombre("Reporte Caña");
        tipoReporte.setDescripcion("Reporte de producción de caña");
        tipoReporte.setCodigoCentroCosto("CC001");
        tipoReporte.setTipoSubclasificacion(TipoSubclasificacion.CULTIVO);
        tipoReporte.setTipoCultivoCategoriaFiltro(CategoriaTipoCultivo.CANNA);
        tipoReporte.setTipoCultivoAutoId(tipoCultivoAutoId);
        tipoReporte.setRequiereCampo(true);
        tipoReporte.setActivo(true);
        tipoReporte.setOrden(1);

        tipoReporteDto = TipoReporteDto.builder()
                .id(tipoReporteId)
                .codigo("REPORTE_CANNA")
                .nombre("Reporte Caña")
                .descripcion("Reporte de producción de caña")
                .codigoCentroCosto("CC001")
                .tipoSubclasificacion(TipoSubclasificacion.CULTIVO)
                .tipoCultivoCategoriaFiltro(CategoriaTipoCultivo.CANNA)
                .tipoCultivoAutoId(tipoCultivoAutoId)
                .requiereCampo(true)
                .activo(true)
                .orden(1)
                .build();
    }

    @Nested
    @DisplayName("create() Tests")
    class CreateTests {

        @Test
        @DisplayName("Debe crear tipo de reporte CANNA con auto-selección de cultivo")
        void debeCrearTipoReporteCanna() {
            // Arrange
            when(writeRepository.save(any(TipoReporte.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            tipoReporteService.create(tipoReporteDto);

            // Assert
            verify(writeRepository).save(argThat(entity ->
                    entity.getCodigo().equals("REPORTE_CANNA") &&
                    entity.getTipoSubclasificacion() == TipoSubclasificacion.CULTIVO &&
                    entity.getTipoCultivoCategoriaFiltro() == CategoriaTipoCultivo.CANNA &&
                    entity.getTipoCultivoAutoId().equals(tipoCultivoAutoId) &&
                    entity.getRequiereCampo()
            ));
        }

        @Test
        @DisplayName("Debe crear tipo de reporte VAQUERIA con subclasificación ANIMAL")
        void debeCrearTipoReporteVaqueria() {
            // Arrange
            TipoReporteDto vaqueriaDto = TipoReporteDto.builder()
                    .id(UUID.randomUUID())
                    .codigo("REPORTE_VAQUERIA")
                    .nombre("Reporte Vaquería")
                    .codigoCentroCosto("CC002")
                    .tipoSubclasificacion(TipoSubclasificacion.ANIMAL)
                    .requiereCampo(false)
                    .activo(true)
                    .orden(2)
                    .build();

            when(writeRepository.save(any(TipoReporte.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            tipoReporteService.create(vaqueriaDto);

            // Assert
            verify(writeRepository).save(argThat(entity ->
                    entity.getCodigo().equals("REPORTE_VAQUERIA") &&
                    entity.getTipoSubclasificacion() == TipoSubclasificacion.ANIMAL &&
                    !entity.getRequiereCampo()
            ));
        }

        @Test
        @DisplayName("Debe crear tipo de reporte TALLER sin subclasificación")
        void debeCrearTipoReporteTaller() {
            // Arrange
            TipoReporteDto tallerDto = TipoReporteDto.builder()
                    .id(UUID.randomUUID())
                    .codigo("REPORTE_TALLER")
                    .nombre("Reporte Taller")
                    .codigoCentroCosto("CC003")
                    .tipoSubclasificacion(TipoSubclasificacion.NINGUNO)
                    .requiereCampo(false)
                    .activo(true)
                    .orden(3)
                    .build();

            when(writeRepository.save(any(TipoReporte.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            tipoReporteService.create(tallerDto);

            // Assert
            verify(writeRepository).save(argThat(entity ->
                    entity.getCodigo().equals("REPORTE_TALLER") &&
                    entity.getTipoSubclasificacion() == TipoSubclasificacion.NINGUNO &&
                    entity.getTipoCultivoCategoriaFiltro() == null &&
                    entity.getTipoCultivoAutoId() == null
            ));
        }

        @Test
        @DisplayName("Debe crear tipo de reporte PLAN_VIANDA con filtro de categoría VIANDA")
        void debeCrearTipoReportePlanVianda() {
            // Arrange
            TipoReporteDto planViandaDto = TipoReporteDto.builder()
                    .id(UUID.randomUUID())
                    .codigo("REPORTE_PLAN_VIANDA")
                    .nombre("Reporte Plan Vianda")
                    .codigoCentroCosto("CC004")
                    .tipoSubclasificacion(TipoSubclasificacion.CULTIVO)
                    .tipoCultivoCategoriaFiltro(CategoriaTipoCultivo.VIANDA)
                    .requiereCampo(true)
                    .activo(true)
                    .orden(4)
                    .build();

            when(writeRepository.save(any(TipoReporte.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            tipoReporteService.create(planViandaDto);

            // Assert
            verify(writeRepository).save(argThat(entity ->
                    entity.getCodigo().equals("REPORTE_PLAN_VIANDA") &&
                    entity.getTipoSubclasificacion() == TipoSubclasificacion.CULTIVO &&
                    entity.getTipoCultivoCategoriaFiltro() == CategoriaTipoCultivo.VIANDA &&
                    entity.getTipoCultivoAutoId() == null // No auto-selección, el usuario elige
            ));
        }
    }

    @Nested
    @DisplayName("update() Tests")
    class UpdateTests {

        @Test
        @DisplayName("Debe actualizar tipo de reporte existente")
        void debeActualizarTipoReporteExistente() {
            // Arrange
            when(readRepository.findById(tipoReporteId)).thenReturn(Optional.of(tipoReporte));
            when(writeRepository.save(any(TipoReporte.class))).thenAnswer(inv -> inv.getArgument(0));

            TipoReporteDto dtoActualizado = TipoReporteDto.builder()
                    .id(tipoReporteId)
                    .codigo("REPORTE_CANNA_MOD")
                    .nombre("Reporte Caña Modificado")
                    .descripcion("Descripción modificada")
                    .codigoCentroCosto("CC001_MOD")
                    .tipoSubclasificacion(TipoSubclasificacion.CULTIVO)
                    .tipoCultivoCategoriaFiltro(CategoriaTipoCultivo.CANNA)
                    .tipoCultivoAutoId(tipoCultivoAutoId)
                    .requiereCampo(true)
                    .activo(true)
                    .orden(10)
                    .build();

            // Act
            tipoReporteService.update(dtoActualizado);

            // Assert
            verify(writeRepository).save(argThat(entity ->
                    entity.getCodigo().equals("REPORTE_CANNA_MOD") &&
                    entity.getNombre().equals("Reporte Caña Modificado") &&
                    entity.getCodigoCentroCosto().equals("CC001_MOD") &&
                    entity.getOrden() == 10
            ));
        }

        @Test
        @DisplayName("Debe lanzar excepción si tipo de reporte no existe")
        void debeLanzarExcepcionSiNoExiste() {
            // Arrange
            when(readRepository.findById(tipoReporteId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> tipoReporteService.update(tipoReporteDto)
            );
        }
    }

    @Nested
    @DisplayName("delete() Tests")
    class DeleteTests {

        @Test
        @DisplayName("Debe eliminar tipo de reporte existente")
        void debeEliminarTipoReporteExistente() {
            // Arrange
            when(readRepository.findById(tipoReporteId)).thenReturn(Optional.of(tipoReporte));
            doNothing().when(writeRepository).delete(any(TipoReporte.class));

            // Act
            tipoReporteService.delete(tipoReporteId);

            // Assert
            verify(writeRepository).delete(tipoReporte);
        }

        @Test
        @DisplayName("Debe lanzar excepción si tipo de reporte no existe al eliminar")
        void debeLanzarExcepcionSiNoExisteAlEliminar() {
            // Arrange
            when(readRepository.findById(tipoReporteId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> tipoReporteService.delete(tipoReporteId)
            );
        }
    }

    @Nested
    @DisplayName("findById() Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Debe retornar DTO cuando tipo de reporte existe")
        void debeRetornarDtoCuandoExiste() {
            // Arrange
            when(readRepository.findById(tipoReporteId)).thenReturn(Optional.of(tipoReporte));

            // Act
            TipoReporteDto result = tipoReporteService.findById(tipoReporteId);

            // Assert
            assertNotNull(result);
            assertEquals(tipoReporteId, result.getId());
            assertEquals("REPORTE_CANNA", result.getCodigo());
            assertEquals(TipoSubclasificacion.CULTIVO, result.getTipoSubclasificacion());
            assertEquals(CategoriaTipoCultivo.CANNA, result.getTipoCultivoCategoriaFiltro());
            assertTrue(result.getRequiereCampo());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando no existe")
        void debeLanzarExcepcionCuandoNoExiste() {
            // Arrange
            when(readRepository.findById(tipoReporteId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> tipoReporteService.findById(tipoReporteId)
            );
        }
    }

    @Nested
    @DisplayName("findAllActive() Tests")
    class FindAllActiveTests {

        @Test
        @DisplayName("Debe retornar todos los tipos de reporte activos ordenados")
        void debeRetornarTodosLosActivos() {
            // Arrange
            TipoReporte vaqueria = new TipoReporte();
            vaqueria.setId(UUID.randomUUID());
            vaqueria.setCodigo("REPORTE_VAQUERIA");
            vaqueria.setNombre("Reporte Vaquería");
            vaqueria.setTipoSubclasificacion(TipoSubclasificacion.ANIMAL);
            vaqueria.setActivo(true);
            vaqueria.setOrden(2);

            when(readRepository.findByActivoTrueOrderByOrdenAsc())
                    .thenReturn(Arrays.asList(tipoReporte, vaqueria));

            // Act
            List<TipoReporteDto> result = tipoReporteService.findAllActive();

            // Assert
            assertEquals(2, result.size());
            assertEquals("REPORTE_CANNA", result.get(0).getCodigo());
            assertEquals("REPORTE_VAQUERIA", result.get(1).getCodigo());
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay activos")
        void debeRetornarListaVaciaCuandoNoHayActivos() {
            // Arrange
            when(readRepository.findByActivoTrueOrderByOrdenAsc()).thenReturn(Collections.emptyList());

            // Act
            List<TipoReporteDto> result = tipoReporteService.findAllActive();

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("existsByCodigo() Tests")
    class ExistsByCodigoTests {

        @Test
        @DisplayName("Debe retornar true cuando código existe")
        void debeRetornarTrueCuandoCodigoExiste() {
            // Arrange
            when(readRepository.existsByCodigo("REPORTE_CANNA")).thenReturn(true);

            // Act
            boolean result = tipoReporteService.existsByCodigo("REPORTE_CANNA");

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Debe retornar false cuando código no existe")
        void debeRetornarFalseCuandoCodigoNoExiste() {
            // Arrange
            when(readRepository.existsByCodigo("INEXISTENTE")).thenReturn(false);

            // Act
            boolean result = tipoReporteService.existsByCodigo("INEXISTENTE");

            // Assert
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Reglas de negocio - Configuración de TipoReporte")
    class ReglasDeNegocioTests {

        @Test
        @DisplayName("REPORTE_CANNA debe auto-seleccionar TipoCultivo y requerir campo")
        void reporteCannaDebeAutoSeleccionarYRequerirCampo() {
            // Arrange
            when(readRepository.findById(tipoReporteId)).thenReturn(Optional.of(tipoReporte));

            // Act
            TipoReporteDto result = tipoReporteService.findById(tipoReporteId);

            // Assert
            assertEquals(TipoSubclasificacion.CULTIVO, result.getTipoSubclasificacion());
            assertEquals(CategoriaTipoCultivo.CANNA, result.getTipoCultivoCategoriaFiltro());
            assertNotNull(result.getTipoCultivoAutoId());
            assertTrue(result.getRequiereCampo());
        }

        @Test
        @DisplayName("REPORTE_PLAN_VIANDA debe filtrar cultivos VIANDA sin auto-selección")
        void reportePlanViandaDebeFiltrarSinAutoSeleccion() {
            // Arrange
            TipoReporte planVianda = new TipoReporte();
            planVianda.setId(UUID.randomUUID());
            planVianda.setCodigo("REPORTE_PLAN_VIANDA");
            planVianda.setTipoSubclasificacion(TipoSubclasificacion.CULTIVO);
            planVianda.setTipoCultivoCategoriaFiltro(CategoriaTipoCultivo.VIANDA);
            planVianda.setTipoCultivoAutoId(null); // Sin auto-selección
            planVianda.setRequiereCampo(true);
            planVianda.setActivo(true);

            when(readRepository.findById(planVianda.getId())).thenReturn(Optional.of(planVianda));

            // Act
            TipoReporteDto result = tipoReporteService.findById(planVianda.getId());

            // Assert
            assertEquals(TipoSubclasificacion.CULTIVO, result.getTipoSubclasificacion());
            assertEquals(CategoriaTipoCultivo.VIANDA, result.getTipoCultivoCategoriaFiltro());
            assertNull(result.getTipoCultivoAutoId()); // Usuario debe seleccionar
        }

        @Test
        @DisplayName("REPORTE_VAQUERIA debe usar subclasificación ANIMAL")
        void reporteVaqueriaDebeUsarSubclasificacionAnimal() {
            // Arrange
            TipoReporte vaqueria = new TipoReporte();
            vaqueria.setId(UUID.randomUUID());
            vaqueria.setCodigo("REPORTE_VAQUERIA");
            vaqueria.setTipoSubclasificacion(TipoSubclasificacion.ANIMAL);
            vaqueria.setTipoCultivoCategoriaFiltro(null); // No aplica
            vaqueria.setTipoCultivoAutoId(null); // No aplica
            vaqueria.setRequiereCampo(false);
            vaqueria.setActivo(true);

            when(readRepository.findById(vaqueria.getId())).thenReturn(Optional.of(vaqueria));

            // Act
            TipoReporteDto result = tipoReporteService.findById(vaqueria.getId());

            // Assert
            assertEquals(TipoSubclasificacion.ANIMAL, result.getTipoSubclasificacion());
            assertNull(result.getTipoCultivoCategoriaFiltro());
            assertFalse(result.getRequiereCampo());
        }

        @Test
        @DisplayName("REPORTE_TALLER no debe requerir subclasificación")
        void reporteTallerNoDebeRequerirSubclasificacion() {
            // Arrange
            TipoReporte taller = new TipoReporte();
            taller.setId(UUID.randomUUID());
            taller.setCodigo("REPORTE_TALLER");
            taller.setTipoSubclasificacion(TipoSubclasificacion.NINGUNO);
            taller.setTipoCultivoCategoriaFiltro(null);
            taller.setTipoCultivoAutoId(null);
            taller.setRequiereCampo(false);
            taller.setActivo(true);

            when(readRepository.findById(taller.getId())).thenReturn(Optional.of(taller));

            // Act
            TipoReporteDto result = tipoReporteService.findById(taller.getId());

            // Assert
            assertEquals(TipoSubclasificacion.NINGUNO, result.getTipoSubclasificacion());
            assertNull(result.getTipoCultivoCategoriaFiltro());
            assertNull(result.getTipoCultivoAutoId());
        }
    }
}
