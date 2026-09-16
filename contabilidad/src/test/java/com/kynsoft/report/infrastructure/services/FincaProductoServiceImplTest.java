package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.report.domain.services.IMovimientoStockService;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.entity.FincaProducto;
import com.kynsoft.report.infrastructure.entity.Producto;
import com.kynsoft.report.infrastructure.repository.command.FincaProductoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ProductoReadDataJPARepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FincaProductoService Unit Tests")
class FincaProductoServiceImplTest {

    @Mock
    private FincaProductoWriteDataJPARepository repositoryCommand;

    @Mock
    private FincaProductoReadDataJPARepository repositoryQuery;

    @Mock
    private FincaReadDataJPARepository fincaRepository;

    @Mock
    private ProductoReadDataJPARepository productoRepository;

    @Mock
    private IMovimientoStockService movimientoStockService;

    @InjectMocks
    private FincaProductoServiceImpl fincaProductoService;

    private UUID fincaId;
    private UUID productoId;
    private Finca finca;
    private Producto producto;
    private FincaProducto fincaProducto;

    @BeforeEach
    void setUp() {
        fincaId = UUID.randomUUID();
        productoId = UUID.randomUUID();

        finca = new Finca();
        finca.setId(fincaId);
        finca.setCode("FINCA-001");
        finca.setName("Finca Test");

        producto = new Producto();
        producto.setId(productoId);
        producto.setCode("PROD-001");
        producto.setName("Producto Test");

        fincaProducto = new FincaProducto();
        fincaProducto.setId(UUID.randomUUID());
        fincaProducto.setFinca(finca);
        fincaProducto.setProducto(producto);
        fincaProducto.setStock(100);
        fincaProducto.setStockMinimo(10);
        fincaProducto.setActivo(true);
    }

    @Nested
    @DisplayName("asignarProductoAFinca() Tests")
    class AsignarProductoAFincaTests {

        @Test
        @DisplayName("Debe rechazar finca no existente")
        void debeRechazarFincaNoExistente() {
            // Arrange
            when(fincaRepository.findById(fincaId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> fincaProductoService.asignarProductoAFinca(fincaId, productoId, 50.0, 10.0)
            );
        }

        @Test
        @DisplayName("Debe rechazar producto no existente")
        void debeRechazarProductoNoExistente() {
            // Arrange
            when(fincaRepository.findById(fincaId)).thenReturn(Optional.of(finca));
            when(productoRepository.findById(productoId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> fincaProductoService.asignarProductoAFinca(fincaId, productoId, 50.0, 10.0)
            );
        }

        @Test
        @DisplayName("Debe rechazar producto ya asignado activo")
        void debeRechazarProductoYaAsignadoActivo() {
            // Arrange
            when(fincaRepository.findById(fincaId)).thenReturn(Optional.of(finca));
            when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
            when(repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId))
                    .thenReturn(Optional.of(fincaProducto)); // Ya existe y está activo

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> fincaProductoService.asignarProductoAFinca(fincaId, productoId, 50.0, 10.0)
            );
        }

        @Test
        @DisplayName("RN-05: Debe reactivar producto inactivo")
        void debeReactivarProductoInactivo() {
            // Arrange
            FincaProducto fpInactivo = new FincaProducto();
            fpInactivo.setId(UUID.randomUUID());
            fpInactivo.setFinca(finca);
            fpInactivo.setProducto(producto);
            fpInactivo.setStock(0);
            fpInactivo.setStockMinimo(5);
            fpInactivo.setActivo(false); // Inactivo

            when(fincaRepository.findById(fincaId)).thenReturn(Optional.of(finca));
            when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
            when(repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId))
                    .thenReturn(Optional.of(fpInactivo));
            when(repositoryCommand.save(any(FincaProducto.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            UUID result = fincaProductoService.asignarProductoAFinca(fincaId, productoId, 100.0, 15.0);

            // Assert
            assertNotNull(result);
            verify(repositoryCommand).save(argThat(fp ->
                    fp.getActivo() && fp.getStock() == 100 && fp.getStockMinimo() == 15
            ));
        }

        @Test
        @DisplayName("Debe asignar nuevo producto a finca")
        void debeAsignarNuevoProductoAFinca() {
            // Arrange
            when(fincaRepository.findById(fincaId)).thenReturn(Optional.of(finca));
            when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
            when(repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId))
                    .thenReturn(Optional.empty());
            when(repositoryCommand.save(any(FincaProducto.class))).thenAnswer(inv -> {
                FincaProducto saved = inv.getArgument(0);
                saved.setId(UUID.randomUUID());
                return saved;
            });

            // Act
            UUID result = fincaProductoService.asignarProductoAFinca(fincaId, productoId, 50.0, 5.0);

            // Assert
            assertNotNull(result);
            verify(repositoryCommand).save(any(FincaProducto.class));
        }
    }

    @Nested
    @DisplayName("removerProductoDeFinca() Tests")
    class RemoverProductoDeFincaTests {

        @Test
        @DisplayName("Debe rechazar producto no asignado")
        void debeRechazarProductoNoAsignado() {
            // Arrange
            when(repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> fincaProductoService.removerProductoDeFinca(fincaId, productoId)
            );
        }

        @Test
        @DisplayName("Debe rechazar producto ya removido")
        void debeRechazarProductoYaRemovido() {
            // Arrange
            fincaProducto.setActivo(false);
            when(repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId))
                    .thenReturn(Optional.of(fincaProducto));

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> fincaProductoService.removerProductoDeFinca(fincaId, productoId)
            );
        }

        @Test
        @DisplayName("RN-04: Debe rechazar remover con stock mayor a cero")
        void debeRechazarRemoverConStockMayorACero() {
            // Arrange
            fincaProducto.setStock(50);
            when(repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId))
                    .thenReturn(Optional.of(fincaProducto));

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> fincaProductoService.removerProductoDeFinca(fincaId, productoId)
            );
        }

        @Test
        @DisplayName("Debe remover producto con stock cero")
        void debeRemoverProductoConStockCero() {
            // Arrange
            fincaProducto.setStock(0);
            when(repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId))
                    .thenReturn(Optional.of(fincaProducto));
            when(repositoryCommand.save(any(FincaProducto.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            fincaProductoService.removerProductoDeFinca(fincaId, productoId);

            // Assert
            verify(repositoryCommand).save(argThat(fp -> !fp.getActivo()));
        }
    }

    @Nested
    @DisplayName("obtenerStock() Tests")
    class ObtenerStockTests {

        @Test
        @DisplayName("Debe obtener stock de producto en finca")
        void debeObtenerStockDeProductoEnFinca() {
            // Arrange
            fincaProducto.setStock(150);
            when(repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId))
                    .thenReturn(Optional.of(fincaProducto));

            // Act
            Double stock = fincaProductoService.obtenerStock(fincaId, productoId);

            // Assert
            assertEquals(150, stock);
        }
    }

    @Nested
    @DisplayName("getEstadoStock() Tests - Entity Level")
    class EstadoStockTests {

        @Test
        @DisplayName("Estado CRITICO cuando stock es 0")
        void estadoCriticoCuandoStockEsCero() {
            // Arrange
            fincaProducto.setStock(0);
            fincaProducto.setStockMinimo(10);

            // Act & Assert
            assertEquals(com.kynsoft.report.domain.dto.EstadoStock.CRITICO, fincaProducto.getEstadoStock());
        }

        @Test
        @DisplayName("Estado CRITICO cuando stock es null")
        void estadoCriticoCuandoStockEsNull() {
            // Arrange
            fincaProducto.setStock(null);

            // Act & Assert
            assertEquals(com.kynsoft.report.domain.dto.EstadoStock.CRITICO, fincaProducto.getEstadoStock());
        }

        @Test
        @DisplayName("Estado BAJO cuando stock < stockMinimo")
        void estadoBajoCuandoStockMenorQueMinimo() {
            // Arrange
            fincaProducto.setStock(5);
            fincaProducto.setStockMinimo(10);

            // Act & Assert
            assertEquals(com.kynsoft.report.domain.dto.EstadoStock.BAJO, fincaProducto.getEstadoStock());
        }

        @Test
        @DisplayName("Estado NORMAL cuando stock está entre mínimo y máximo")
        void estadoNormalCuandoStockEnRango() {
            // Arrange
            fincaProducto.setStock(50);
            fincaProducto.setStockMinimo(10);
            fincaProducto.setStockMaximo(100);

            // Act & Assert
            assertEquals(com.kynsoft.report.domain.dto.EstadoStock.NORMAL, fincaProducto.getEstadoStock());
        }

        @Test
        @DisplayName("Estado EXCESO cuando stock > stockMaximo")
        void estadoExcesoCuandoStockMayorQueMaximo() {
            // Arrange
            fincaProducto.setStock(150);
            fincaProducto.setStockMinimo(10);
            fincaProducto.setStockMaximo(100);

            // Act & Assert
            assertEquals(com.kynsoft.report.domain.dto.EstadoStock.EXCESO, fincaProducto.getEstadoStock());
        }

        @Test
        @DisplayName("Estado NORMAL cuando stockMinimo es 0 y hay stock")
        void estadoNormalCuandoStockMinimoEsCero() {
            // Arrange
            fincaProducto.setStock(50);
            fincaProducto.setStockMinimo(0);
            fincaProducto.setStockMaximo(null);

            // Act & Assert
            assertEquals(com.kynsoft.report.domain.dto.EstadoStock.NORMAL, fincaProducto.getEstadoStock());
        }
    }

    @Nested
    @DisplayName("actualizarStock() Tests")
    class ActualizarStockTests {

        @Test
        @DisplayName("Debe actualizar stock con valor positivo (entrada)")
        void debeActualizarStockConEntrada() {
            // Arrange
            fincaProducto.setStock(100);
            when(repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId))
                    .thenReturn(Optional.of(fincaProducto));
            when(repositoryCommand.save(any(FincaProducto.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            fincaProductoService.actualizarStock(fincaId, productoId, 50.0);

            // Assert
            verify(repositoryCommand).save(argThat(fp -> fp.getStock() == 50));
        }

        @Test
        @DisplayName("Debe rechazar producto no asignado al actualizar stock")
        void debeRechazarProductoNoAsignadoAlActualizarStock() {
            // Arrange
            when(repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> fincaProductoService.actualizarStock(fincaId, productoId, 50.0)
            );
        }

        @Test
        @DisplayName("Debe permitir stock cero")
        void debePermitirStockCero() {
            // Arrange
            fincaProducto.setStock(100);
            when(repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId))
                    .thenReturn(Optional.of(fincaProducto));
            when(repositoryCommand.save(any(FincaProducto.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            fincaProductoService.actualizarStock(fincaId, productoId, 0.0);

            // Assert
            verify(repositoryCommand).save(argThat(fp -> fp.getStock() == 0));
        }
    }

    @Nested
    @DisplayName("toAggregate() Conversion Tests")
    class ToAggregateTests {

        @Test
        @DisplayName("Debe convertir entidad a DTO correctamente")
        void debeConvertirEntidadADtoCorrectamente() {
            // Arrange
            fincaProducto.setStock(75);
            fincaProducto.setStockMinimo(20);
            fincaProducto.setStockMaximo(200);
            fincaProducto.setActivo(true);

            // Act
            var dto = fincaProducto.toAggregate();

            // Assert
            assertEquals(fincaProducto.getId(), dto.getId());
            assertEquals(fincaId, dto.getFincaId());
            assertEquals(productoId, dto.getProductoId());
            assertEquals(75, dto.getStock());
            assertEquals(20, dto.getStockMinimo());
            assertEquals(200, dto.getStockMaximo());
            assertTrue(dto.getActivo());
            assertEquals(com.kynsoft.report.domain.dto.EstadoStock.NORMAL, dto.getEstadoStock());
        }

        @Test
        @DisplayName("Debe manejar finca null en conversión")
        void debeManejarFincaNullEnConversion() {
            // Arrange
            fincaProducto.setFinca(null);

            // Act
            var dto = fincaProducto.toAggregate();

            // Assert
            assertNull(dto.getFincaId());
        }
    }
}
