package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.CategoriaTipoCultivo;
import com.kynsoft.report.domain.dto.TipoCultivoDto;
import com.kynsoft.report.infrastructure.entity.TipoCultivo;
import com.kynsoft.report.infrastructure.repository.command.TipoCultivoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TipoCultivoReadDataJPARepository;
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
@DisplayName("TipoCultivoServiceImpl Unit Tests")
class TipoCultivoServiceImplTest {

    @Mock
    private TipoCultivoWriteDataJPARepository writeRepository;

    @Mock
    private TipoCultivoReadDataJPARepository readRepository;

    @InjectMocks
    private TipoCultivoServiceImpl tipoCultivoService;

    private UUID tipoCultivoId;
    private TipoCultivo tipoCultivo;
    private TipoCultivoDto tipoCultivoDto;

    @BeforeEach
    void setUp() {
        tipoCultivoId = UUID.randomUUID();

        tipoCultivo = new TipoCultivo();
        tipoCultivo.setId(tipoCultivoId);
        tipoCultivo.setCodigo("CANA");
        tipoCultivo.setNombre("Caña de Azúcar");
        tipoCultivo.setDescripcion("Cultivo de caña");
        tipoCultivo.setCategoria(CategoriaTipoCultivo.CANNA);
        tipoCultivo.setRequiereCampo(true);
        tipoCultivo.setActivo(true);
        tipoCultivo.setOrden(1);

        tipoCultivoDto = TipoCultivoDto.builder()
                .id(tipoCultivoId)
                .codigo("CANA")
                .nombre("Caña de Azúcar")
                .descripcion("Cultivo de caña")
                .categoria(CategoriaTipoCultivo.CANNA)
                .requiereCampo(true)
                .activo(true)
                .orden(1)
                .build();
    }

    @Nested
    @DisplayName("create() Tests")
    class CreateTests {

        @Test
        @DisplayName("Debe crear tipo de cultivo correctamente")
        void debeCrearTipoCultivoCorrectamente() {
            // Arrange
            when(writeRepository.save(any(TipoCultivo.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            tipoCultivoService.create(tipoCultivoDto);

            // Assert
            verify(writeRepository).save(argThat(entity ->
                    entity.getCodigo().equals("CANA") &&
                    entity.getNombre().equals("Caña de Azúcar") &&
                    entity.getCategoria() == CategoriaTipoCultivo.CANNA &&
                    entity.getRequiereCampo()
            ));
        }
    }

    @Nested
    @DisplayName("update() Tests")
    class UpdateTests {

        @Test
        @DisplayName("Debe actualizar tipo de cultivo existente")
        void debeActualizarTipoCultivoExistente() {
            // Arrange
            when(readRepository.findById(tipoCultivoId)).thenReturn(Optional.of(tipoCultivo));
            when(writeRepository.save(any(TipoCultivo.class))).thenAnswer(inv -> inv.getArgument(0));

            TipoCultivoDto dtoActualizado = TipoCultivoDto.builder()
                    .id(tipoCultivoId)
                    .codigo("CANA_MOD")
                    .nombre("Caña Modificada")
                    .descripcion("Descripción modificada")
                    .categoria(CategoriaTipoCultivo.CANNA)
                    .requiereCampo(true)
                    .activo(true)
                    .orden(2)
                    .build();

            // Act
            tipoCultivoService.update(dtoActualizado);

            // Assert
            verify(writeRepository).save(argThat(entity ->
                    entity.getCodigo().equals("CANA_MOD") &&
                    entity.getNombre().equals("Caña Modificada") &&
                    entity.getOrden() == 2
            ));
        }

        @Test
        @DisplayName("Debe lanzar excepción si tipo de cultivo no existe")
        void debeLanzarExcepcionSiNoExiste() {
            // Arrange
            when(readRepository.findById(tipoCultivoId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> tipoCultivoService.update(tipoCultivoDto)
            );
        }
    }

    @Nested
    @DisplayName("delete() Tests")
    class DeleteTests {

        @Test
        @DisplayName("Debe eliminar tipo de cultivo existente")
        void debeEliminarTipoCultivoExistente() {
            // Arrange
            when(readRepository.findById(tipoCultivoId)).thenReturn(Optional.of(tipoCultivo));
            doNothing().when(writeRepository).delete(any(TipoCultivo.class));

            // Act
            tipoCultivoService.delete(tipoCultivoId);

            // Assert
            verify(writeRepository).delete(tipoCultivo);
        }

        @Test
        @DisplayName("Debe lanzar excepción si tipo de cultivo no existe")
        void debeLanzarExcepcionSiNoExisteAlEliminar() {
            // Arrange
            when(readRepository.findById(tipoCultivoId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> tipoCultivoService.delete(tipoCultivoId)
            );
        }
    }

    @Nested
    @DisplayName("findById() Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Debe retornar DTO cuando tipo de cultivo existe")
        void debeRetornarDtoCuandoExiste() {
            // Arrange
            when(readRepository.findById(tipoCultivoId)).thenReturn(Optional.of(tipoCultivo));

            // Act
            TipoCultivoDto result = tipoCultivoService.findById(tipoCultivoId);

            // Assert
            assertNotNull(result);
            assertEquals(tipoCultivoId, result.getId());
            assertEquals("CANA", result.getCodigo());
            assertEquals(CategoriaTipoCultivo.CANNA, result.getCategoria());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando no existe")
        void debeLanzarExcepcionCuandoNoExiste() {
            // Arrange
            when(readRepository.findById(tipoCultivoId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> tipoCultivoService.findById(tipoCultivoId)
            );
        }
    }

    @Nested
    @DisplayName("findAllActive() Tests")
    class FindAllActiveTests {

        @Test
        @DisplayName("Debe retornar lista de tipos de cultivo activos")
        void debeRetornarListaDeActivos() {
            // Arrange
            TipoCultivo otro = new TipoCultivo();
            otro.setId(UUID.randomUUID());
            otro.setCodigo("YUCA");
            otro.setNombre("Yuca");
            otro.setCategoria(CategoriaTipoCultivo.VIANDA);
            otro.setActivo(true);

            when(readRepository.findByActivoTrueOrderByOrdenAsc())
                    .thenReturn(Arrays.asList(tipoCultivo, otro));

            // Act
            List<TipoCultivoDto> result = tipoCultivoService.findAllActive();

            // Assert
            assertEquals(2, result.size());
            assertEquals("CANA", result.get(0).getCodigo());
            assertEquals("YUCA", result.get(1).getCodigo());
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay activos")
        void debeRetornarListaVaciaCuandoNoHayActivos() {
            // Arrange
            when(readRepository.findByActivoTrueOrderByOrdenAsc()).thenReturn(Collections.emptyList());

            // Act
            List<TipoCultivoDto> result = tipoCultivoService.findAllActive();

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("findActiveByCategoria() Tests")
    class FindActiveByCategoriaTests {

        @Test
        @DisplayName("Debe retornar tipos de cultivo filtrados por categoría CANNA")
        void debeRetornarFiltradosPorCategoriaCanna() {
            // Arrange
            when(readRepository.findByCategoriaAndActivoTrueOrderByOrdenAsc(CategoriaTipoCultivo.CANNA))
                    .thenReturn(Collections.singletonList(tipoCultivo));

            // Act
            List<TipoCultivoDto> result = tipoCultivoService.findActiveByCategoria(CategoriaTipoCultivo.CANNA);

            // Assert
            assertEquals(1, result.size());
            assertEquals("CANA", result.get(0).getCodigo());
            assertEquals(CategoriaTipoCultivo.CANNA, result.get(0).getCategoria());
        }

        @Test
        @DisplayName("Debe retornar tipos de cultivo filtrados por categoría VIANDA")
        void debeRetornarFiltradosPorCategoriaVianda() {
            // Arrange
            TipoCultivo yuca = new TipoCultivo();
            yuca.setId(UUID.randomUUID());
            yuca.setCodigo("YUCA");
            yuca.setNombre("Yuca");
            yuca.setCategoria(CategoriaTipoCultivo.VIANDA);
            yuca.setActivo(true);

            TipoCultivo platano = new TipoCultivo();
            platano.setId(UUID.randomUUID());
            platano.setCodigo("PLATANO");
            platano.setNombre("Plátano");
            platano.setCategoria(CategoriaTipoCultivo.VIANDA);
            platano.setActivo(true);

            when(readRepository.findByCategoriaAndActivoTrueOrderByOrdenAsc(CategoriaTipoCultivo.VIANDA))
                    .thenReturn(Arrays.asList(yuca, platano));

            // Act
            List<TipoCultivoDto> result = tipoCultivoService.findActiveByCategoria(CategoriaTipoCultivo.VIANDA);

            // Assert
            assertEquals(2, result.size());
            assertTrue(result.stream().allMatch(dto -> dto.getCategoria() == CategoriaTipoCultivo.VIANDA));
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay tipos de cultivo de esa categoría")
        void debeRetornarListaVaciaCuandoNoHayDeLaCategoria() {
            // Arrange
            when(readRepository.findByCategoriaAndActivoTrueOrderByOrdenAsc(CategoriaTipoCultivo.OTRO))
                    .thenReturn(Collections.emptyList());

            // Act
            List<TipoCultivoDto> result = tipoCultivoService.findActiveByCategoria(CategoriaTipoCultivo.OTRO);

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
            when(readRepository.existsByCodigo("CANA")).thenReturn(true);

            // Act
            boolean result = tipoCultivoService.existsByCodigo("CANA");

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Debe retornar false cuando código no existe")
        void debeRetornarFalseCuandoCodigoNoExiste() {
            // Arrange
            when(readRepository.existsByCodigo("INEXISTENTE")).thenReturn(false);

            // Act
            boolean result = tipoCultivoService.existsByCodigo("INEXISTENTE");

            // Assert
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("existsByCodigoAndIdNot() Tests")
    class ExistsByCodigoAndIdNotTests {

        @Test
        @DisplayName("Debe retornar true cuando código existe en otro registro")
        void debeRetornarTrueCuandoCodigoExisteEnOtroRegistro() {
            // Arrange
            UUID otroId = UUID.randomUUID();
            when(readRepository.existsByCodigoAndIdNot("CANA", otroId)).thenReturn(true);

            // Act
            boolean result = tipoCultivoService.existsByCodigoAndIdNot("CANA", otroId);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Debe retornar false cuando código solo existe en mismo registro")
        void debeRetornarFalseCuandoCodigoSoloExisteEnMismoRegistro() {
            // Arrange
            when(readRepository.existsByCodigoAndIdNot("CANA", tipoCultivoId)).thenReturn(false);

            // Act
            boolean result = tipoCultivoService.existsByCodigoAndIdNot("CANA", tipoCultivoId);

            // Assert
            assertFalse(result);
        }
    }
}
