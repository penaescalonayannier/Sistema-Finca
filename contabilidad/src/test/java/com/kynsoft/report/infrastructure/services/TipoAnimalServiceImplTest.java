package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.TipoAnimalDto;
import com.kynsoft.report.infrastructure.entity.TipoAnimal;
import com.kynsoft.report.infrastructure.repository.command.TipoAnimalWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TipoAnimalReadDataJPARepository;
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
@DisplayName("TipoAnimalServiceImpl Unit Tests")
class TipoAnimalServiceImplTest {

    @Mock
    private TipoAnimalWriteDataJPARepository writeRepository;

    @Mock
    private TipoAnimalReadDataJPARepository readRepository;

    @InjectMocks
    private TipoAnimalServiceImpl tipoAnimalService;

    private UUID tipoAnimalId;
    private TipoAnimal tipoAnimal;
    private TipoAnimalDto tipoAnimalDto;

    @BeforeEach
    void setUp() {
        tipoAnimalId = UUID.randomUUID();

        tipoAnimal = new TipoAnimal();
        tipoAnimal.setId(tipoAnimalId);
        tipoAnimal.setCodigo("VACA");
        tipoAnimal.setNombre("Vacas");
        tipoAnimal.setDescripcion("Ganado vacuno");
        tipoAnimal.setActivo(true);
        tipoAnimal.setOrden(1);

        tipoAnimalDto = TipoAnimalDto.builder()
                .id(tipoAnimalId)
                .codigo("VACA")
                .nombre("Vacas")
                .descripcion("Ganado vacuno")
                .activo(true)
                .orden(1)
                .build();
    }

    @Nested
    @DisplayName("create() Tests")
    class CreateTests {

        @Test
        @DisplayName("Debe crear tipo de animal correctamente")
        void debeCrearTipoAnimalCorrectamente() {
            // Arrange
            when(writeRepository.save(any(TipoAnimal.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            tipoAnimalService.create(tipoAnimalDto);

            // Assert
            verify(writeRepository).save(argThat(entity ->
                    entity.getCodigo().equals("VACA") &&
                    entity.getNombre().equals("Vacas") &&
                    entity.getActivo()
            ));
        }

        @Test
        @DisplayName("Debe crear todos los tipos de animales de vaquería")
        void debeCrearTodosLosTiposDeAnimales() {
            // Arrange
            when(writeRepository.save(any(TipoAnimal.class))).thenAnswer(inv -> inv.getArgument(0));

            String[] codigos = {"VACA", "OVEJA", "CONEJO", "CHIVO", "CERDO"};
            String[] nombres = {"Vacas", "Ovejas", "Conejos", "Chivos", "Cerdos"};

            // Act & Assert
            for (int i = 0; i < codigos.length; i++) {
                TipoAnimalDto dto = TipoAnimalDto.builder()
                        .id(UUID.randomUUID())
                        .codigo(codigos[i])
                        .nombre(nombres[i])
                        .activo(true)
                        .orden(i + 1)
                        .build();

                tipoAnimalService.create(dto);

                final int index = i;
                verify(writeRepository, atLeastOnce()).save(argThat(entity ->
                        entity.getCodigo().equals(codigos[index])
                ));
            }
        }
    }

    @Nested
    @DisplayName("update() Tests")
    class UpdateTests {

        @Test
        @DisplayName("Debe actualizar tipo de animal existente")
        void debeActualizarTipoAnimalExistente() {
            // Arrange
            when(readRepository.findById(tipoAnimalId)).thenReturn(Optional.of(tipoAnimal));
            when(writeRepository.save(any(TipoAnimal.class))).thenAnswer(inv -> inv.getArgument(0));

            TipoAnimalDto dtoActualizado = TipoAnimalDto.builder()
                    .id(tipoAnimalId)
                    .codigo("VACA_MOD")
                    .nombre("Vacas Modificado")
                    .descripcion("Descripción modificada")
                    .activo(true)
                    .orden(10)
                    .build();

            // Act
            tipoAnimalService.update(dtoActualizado);

            // Assert
            verify(writeRepository).save(argThat(entity ->
                    entity.getCodigo().equals("VACA_MOD") &&
                    entity.getNombre().equals("Vacas Modificado") &&
                    entity.getOrden() == 10
            ));
        }

        @Test
        @DisplayName("Debe lanzar excepción si tipo de animal no existe")
        void debeLanzarExcepcionSiNoExiste() {
            // Arrange
            when(readRepository.findById(tipoAnimalId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> tipoAnimalService.update(tipoAnimalDto)
            );
        }
    }

    @Nested
    @DisplayName("delete() Tests")
    class DeleteTests {

        @Test
        @DisplayName("Debe eliminar tipo de animal existente")
        void debeEliminarTipoAnimalExistente() {
            // Arrange
            when(readRepository.findById(tipoAnimalId)).thenReturn(Optional.of(tipoAnimal));
            doNothing().when(writeRepository).delete(any(TipoAnimal.class));

            // Act
            tipoAnimalService.delete(tipoAnimalId);

            // Assert
            verify(writeRepository).delete(tipoAnimal);
        }

        @Test
        @DisplayName("Debe lanzar excepción si tipo de animal no existe al eliminar")
        void debeLanzarExcepcionSiNoExisteAlEliminar() {
            // Arrange
            when(readRepository.findById(tipoAnimalId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> tipoAnimalService.delete(tipoAnimalId)
            );
        }
    }

    @Nested
    @DisplayName("findById() Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Debe retornar DTO cuando tipo de animal existe")
        void debeRetornarDtoCuandoExiste() {
            // Arrange
            when(readRepository.findById(tipoAnimalId)).thenReturn(Optional.of(tipoAnimal));

            // Act
            TipoAnimalDto result = tipoAnimalService.findById(tipoAnimalId);

            // Assert
            assertNotNull(result);
            assertEquals(tipoAnimalId, result.getId());
            assertEquals("VACA", result.getCodigo());
            assertEquals("Vacas", result.getNombre());
            assertTrue(result.getActivo());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando no existe")
        void debeLanzarExcepcionCuandoNoExiste() {
            // Arrange
            when(readRepository.findById(tipoAnimalId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> tipoAnimalService.findById(tipoAnimalId)
            );
        }
    }

    @Nested
    @DisplayName("findAllActive() Tests")
    class FindAllActiveTests {

        @Test
        @DisplayName("Debe retornar todos los tipos de animal activos ordenados")
        void debeRetornarTodosLosActivos() {
            // Arrange
            TipoAnimal oveja = new TipoAnimal();
            oveja.setId(UUID.randomUUID());
            oveja.setCodigo("OVEJA");
            oveja.setNombre("Ovejas");
            oveja.setActivo(true);
            oveja.setOrden(2);

            TipoAnimal conejo = new TipoAnimal();
            conejo.setId(UUID.randomUUID());
            conejo.setCodigo("CONEJO");
            conejo.setNombre("Conejos");
            conejo.setActivo(true);
            conejo.setOrden(3);

            when(readRepository.findByActivoTrueOrderByOrdenAsc())
                    .thenReturn(Arrays.asList(tipoAnimal, oveja, conejo));

            // Act
            List<TipoAnimalDto> result = tipoAnimalService.findAllActive();

            // Assert
            assertEquals(3, result.size());
            assertEquals("VACA", result.get(0).getCodigo());
            assertEquals("OVEJA", result.get(1).getCodigo());
            assertEquals("CONEJO", result.get(2).getCodigo());
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay activos")
        void debeRetornarListaVaciaCuandoNoHayActivos() {
            // Arrange
            when(readRepository.findByActivoTrueOrderByOrdenAsc()).thenReturn(Collections.emptyList());

            // Act
            List<TipoAnimalDto> result = tipoAnimalService.findAllActive();

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Debe retornar animales en orden correcto")
        void debeRetornarAnimalesEnOrdenCorrecto() {
            // Arrange
            TipoAnimal cerdo = new TipoAnimal();
            cerdo.setId(UUID.randomUUID());
            cerdo.setCodigo("CERDO");
            cerdo.setNombre("Cerdos");
            cerdo.setActivo(true);
            cerdo.setOrden(5);

            TipoAnimal chivo = new TipoAnimal();
            chivo.setId(UUID.randomUUID());
            chivo.setCodigo("CHIVO");
            chivo.setNombre("Chivos");
            chivo.setActivo(true);
            chivo.setOrden(4);

            // Ordenados por orden ascendente
            when(readRepository.findByActivoTrueOrderByOrdenAsc())
                    .thenReturn(Arrays.asList(tipoAnimal, chivo, cerdo));

            // Act
            List<TipoAnimalDto> result = tipoAnimalService.findAllActive();

            // Assert
            assertEquals(3, result.size());
            assertEquals(1, result.get(0).getOrden()); // VACA
            assertEquals(4, result.get(1).getOrden()); // CHIVO
            assertEquals(5, result.get(2).getOrden()); // CERDO
        }
    }

    @Nested
    @DisplayName("existsByCodigo() Tests")
    class ExistsByCodigoTests {

        @Test
        @DisplayName("Debe retornar true cuando código existe")
        void debeRetornarTrueCuandoCodigoExiste() {
            // Arrange
            when(readRepository.existsByCodigo("VACA")).thenReturn(true);

            // Act
            boolean result = tipoAnimalService.existsByCodigo("VACA");

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Debe retornar false cuando código no existe")
        void debeRetornarFalseCuandoCodigoNoExiste() {
            // Arrange
            when(readRepository.existsByCodigo("INEXISTENTE")).thenReturn(false);

            // Act
            boolean result = tipoAnimalService.existsByCodigo("INEXISTENTE");

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Debe verificar códigos de animales estándar de vaquería")
        void debeVerificarCodigosEstandar() {
            // Arrange
            String[] codigosEstandar = {"VACA", "OVEJA", "CONEJO", "CHIVO", "CERDO"};

            for (String codigo : codigosEstandar) {
                when(readRepository.existsByCodigo(codigo)).thenReturn(true);
            }

            // Act & Assert
            for (String codigo : codigosEstandar) {
                assertTrue(tipoAnimalService.existsByCodigo(codigo),
                        "Debería existir el código: " + codigo);
            }
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
            when(readRepository.existsByCodigoAndIdNot("VACA", otroId)).thenReturn(true);

            // Act
            boolean result = tipoAnimalService.existsByCodigoAndIdNot("VACA", otroId);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Debe retornar false cuando código solo existe en mismo registro")
        void debeRetornarFalseCuandoCodigoSoloExisteEnMismoRegistro() {
            // Arrange
            when(readRepository.existsByCodigoAndIdNot("VACA", tipoAnimalId)).thenReturn(false);

            // Act
            boolean result = tipoAnimalService.existsByCodigoAndIdNot("VACA", tipoAnimalId);

            // Assert
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Integración con Vaquería")
    class IntegracionVaqueriaTests {

        @Test
        @DisplayName("Todos los tipos de animales deben tener código único")
        void todosLosTiposDebenTenerCodigoUnico() {
            // Arrange
            TipoAnimal oveja = new TipoAnimal();
            oveja.setId(UUID.randomUUID());
            oveja.setCodigo("OVEJA");
            oveja.setNombre("Ovejas");
            oveja.setActivo(true);

            TipoAnimal conejo = new TipoAnimal();
            conejo.setId(UUID.randomUUID());
            conejo.setCodigo("CONEJO");
            conejo.setNombre("Conejos");
            conejo.setActivo(true);

            TipoAnimal chivo = new TipoAnimal();
            chivo.setId(UUID.randomUUID());
            chivo.setCodigo("CHIVO");
            chivo.setNombre("Chivos");
            chivo.setActivo(true);

            TipoAnimal cerdo = new TipoAnimal();
            cerdo.setId(UUID.randomUUID());
            cerdo.setCodigo("CERDO");
            cerdo.setNombre("Cerdos");
            cerdo.setActivo(true);

            List<TipoAnimal> todosLosAnimales = Arrays.asList(tipoAnimal, oveja, conejo, chivo, cerdo);

            when(readRepository.findByActivoTrueOrderByOrdenAsc()).thenReturn(todosLosAnimales);

            // Act
            List<TipoAnimalDto> result = tipoAnimalService.findAllActive();

            // Assert
            assertEquals(5, result.size());

            // Verificar que todos los códigos son únicos
            long codigosUnicos = result.stream()
                    .map(TipoAnimalDto::getCodigo)
                    .distinct()
                    .count();

            assertEquals(5, codigosUnicos, "Todos los códigos deben ser únicos");
        }
    }
}
