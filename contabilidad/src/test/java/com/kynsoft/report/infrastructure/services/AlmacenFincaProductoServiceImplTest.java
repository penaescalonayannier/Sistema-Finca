package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.services.IMovimientoStockService;
import com.kynsoft.report.infrastructure.entity.Almacen;
import com.kynsoft.report.infrastructure.entity.AlmacenFincaProducto;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.entity.FincaProducto;
import com.kynsoft.report.infrastructure.entity.Producto;
import com.kynsoft.report.infrastructure.repository.command.AlmacenFincaProductoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.FincaProductoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AlmacenFincaProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AlmacenReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaProductoReadDataJPARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlmacenFincaProductoService Unit Tests")
class AlmacenFincaProductoServiceImplTest {

    @Mock
    private AlmacenFincaProductoWriteDataJPARepository repositoryCommand;

    @Mock
    private AlmacenFincaProductoReadDataJPARepository repositoryQuery;

    @Mock
    private AlmacenReadDataJPARepository almacenRepository;

    @Mock
    private FincaProductoReadDataJPARepository fincaProductoRepository;

    @Mock
    private FincaProductoWriteDataJPARepository fincaProductoWriteRepository;

    @Mock
    private IMovimientoStockService movimientoStockService;

    @InjectMocks
    private AlmacenFincaProductoServiceImpl service;

    private UUID almacenId;
    private UUID fincaProductoId;
    private UUID fincaId;
    private UUID productoId;
    private Finca finca;
    private Almacen almacen;
    private Producto producto;
    private FincaProducto fincaProducto;
    private AlmacenFincaProducto almacenFincaProducto;

    @BeforeEach
    void setUp() {
        fincaId = UUID.randomUUID();
        almacenId = UUID.randomUUID();
        fincaProductoId = UUID.randomUUID();
        productoId = UUID.randomUUID();

        finca = new Finca();
        finca.setId(fincaId);
        finca.setCode("FINCA-001");
        finca.setName("Finca Test");

        almacen = new Almacen();
        almacen.setId(almacenId);
        almacen.setNombre("Almacen Principal");
        almacen.setFinca(finca);
        almacen.setActivo(true);

        producto = new Producto();
        producto.setId(productoId);
        producto.setCode("PROD-001");
        producto.setName("Producto Test");

        fincaProducto = new FincaProducto();
        fincaProducto.setId(fincaProductoId);
        fincaProducto.setFinca(finca);
        fincaProducto.setProducto(producto);
        fincaProducto.setStock(100);
        fincaProducto.setStockMinimo(10);
        fincaProducto.setActivo(true);

        almacenFincaProducto = new AlmacenFincaProducto();
        almacenFincaProducto.setId(UUID.randomUUID());
        almacenFincaProducto.setAlmacen(almacen);
        almacenFincaProducto.setFincaProducto(fincaProducto);
        almacenFincaProducto.setStock(50);
        almacenFincaProducto.setStockMinimo(5);
        almacenFincaProducto.setActivo(true);
    }

    // ==================== ENTRADA TESTS ====================

    @Nested
    @DisplayName("entrada() Tests")
    class EntradaTests {

        @Test
        @DisplayName("Debe registrar entrada y actualizar stock de almacen y finca")
        void debeRegistrarEntradaYActualizarStockDeAlmacenYFinca() {
            // Arrange
            Double cantidad = 30.0;
            Double stockAlmacenAnterior = 50.0;
            Double stockFincaAnterior = 100.0;

            almacenFincaProducto.setStock(stockAlmacenAnterior);
            fincaProducto.setStock(stockFincaAnterior);

            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));
            when(repositoryCommand.save(any(AlmacenFincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(fincaProductoWriteRepository.save(any(FincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            service.entrada(almacenFincaProducto.getId(), cantidad,
                    TipoMovimientoStock.ENTRADA_PRODUCCION, "Entrada test");

            // Assert
            ArgumentCaptor<AlmacenFincaProducto> afpCaptor = ArgumentCaptor.forClass(AlmacenFincaProducto.class);
            verify(repositoryCommand).save(afpCaptor.capture());
            assertEquals(stockAlmacenAnterior + cantidad, afpCaptor.getValue().getStock());

            ArgumentCaptor<FincaProducto> fpCaptor = ArgumentCaptor.forClass(FincaProducto.class);
            verify(fincaProductoWriteRepository).save(fpCaptor.capture());
            assertEquals(stockFincaAnterior + cantidad, fpCaptor.getValue().getStock());

            verify(movimientoStockService).registrar(any());
        }

        @Test
        @DisplayName("Debe rechazar cantidad negativa")
        void debeRechazarCantidadNegativa() {
            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> service.entrada(almacenFincaProducto.getId(), -10.0,
                            TipoMovimientoStock.ENTRADA_PRODUCCION, "Test")
            );
        }

        @Test
        @DisplayName("Debe rechazar cantidad cero")
        void debeRechazarCantidadCero() {
            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> service.entrada(almacenFincaProducto.getId(), 0.0,
                            TipoMovimientoStock.ENTRADA_PRODUCCION, "Test")
            );
        }

        @Test
        @DisplayName("Debe rechazar cantidad null")
        void debeRechazarCantidadNull() {
            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> service.entrada(almacenFincaProducto.getId(), null,
                            TipoMovimientoStock.ENTRADA_PRODUCCION, "Test")
            );
        }

        @Test
        @DisplayName("Debe rechazar producto no encontrado")
        void debeRechazarProductoNoEncontrado() {
            // Arrange
            UUID idInexistente = UUID.randomUUID();
            when(repositoryQuery.findById(idInexistente)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> service.entrada(idInexistente, 10.0,
                            TipoMovimientoStock.ENTRADA_PRODUCCION, "Test")
            );
        }

        @Test
        @DisplayName("Debe registrar entrada por factura con numero")
        void debeRegistrarEntradaPorFacturaConNumero() {
            // Arrange
            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));
            when(repositoryCommand.save(any(AlmacenFincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(fincaProductoWriteRepository.save(any(FincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            service.entradaFactura(almacenFincaProducto.getId(), 20.0, "FACT-001", "Compra");

            // Assert
            verify(repositoryCommand).save(any(AlmacenFincaProducto.class));
            verify(fincaProductoWriteRepository).save(any(FincaProducto.class));
            verify(movimientoStockService).registrar(any());
        }
    }

    // ==================== SALIDA TESTS ====================

    @Nested
    @DisplayName("salida() Tests")
    class SalidaTests {

        @Test
        @DisplayName("Debe registrar salida y actualizar stock de almacen y finca")
        void debeRegistrarSalidaYActualizarStockDeAlmacenYFinca() {
            // Arrange
            Double cantidad = 20.0;
            Double stockAlmacenAnterior = 50.0;
            Double stockFincaAnterior = 100.0;

            almacenFincaProducto.setStock(stockAlmacenAnterior);
            fincaProducto.setStock(stockFincaAnterior);

            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));
            when(repositoryCommand.save(any(AlmacenFincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(fincaProductoWriteRepository.save(any(FincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            service.salida(almacenFincaProducto.getId(), cantidad, "Salida test");

            // Assert
            ArgumentCaptor<AlmacenFincaProducto> afpCaptor = ArgumentCaptor.forClass(AlmacenFincaProducto.class);
            verify(repositoryCommand).save(afpCaptor.capture());
            assertEquals(stockAlmacenAnterior - cantidad, afpCaptor.getValue().getStock());

            ArgumentCaptor<FincaProducto> fpCaptor = ArgumentCaptor.forClass(FincaProducto.class);
            verify(fincaProductoWriteRepository).save(fpCaptor.capture());
            assertEquals(stockFincaAnterior - cantidad, fpCaptor.getValue().getStock());

            verify(movimientoStockService).registrar(any());
        }

        @Test
        @DisplayName("Debe rechazar salida con stock insuficiente")
        void debeRechazarSalidaConStockInsuficiente() {
            // Arrange
            almacenFincaProducto.setStock(10);
            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> service.salida(almacenFincaProducto.getId(), 50.0, "Test")
            );
        }

        @Test
        @DisplayName("Debe permitir salida con stock exacto")
        void debePermitirSalidaConStockExacto() {
            // Arrange
            Double stockActual = 30.0;
            almacenFincaProducto.setStock(stockActual);
            fincaProducto.setStock(stockActual);

            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));
            when(repositoryCommand.save(any(AlmacenFincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(fincaProductoWriteRepository.save(any(FincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            service.salida(almacenFincaProducto.getId(), stockActual, "Salida total");

            // Assert
            ArgumentCaptor<AlmacenFincaProducto> afpCaptor = ArgumentCaptor.forClass(AlmacenFincaProducto.class);
            verify(repositoryCommand).save(afpCaptor.capture());
            assertEquals(0, afpCaptor.getValue().getStock());
        }

        @Test
        @DisplayName("Stock de finca no debe ser negativo")
        void stockDeFincaNoDebeSerNegativo() {
            // Arrange
            almacenFincaProducto.setStock(30);
            fincaProducto.setStock(20); // Menos que la cantidad en almacen (inconsistencia)

            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));
            when(repositoryCommand.save(any(AlmacenFincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(fincaProductoWriteRepository.save(any(FincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            service.salida(almacenFincaProducto.getId(), 30.0, "Salida");

            // Assert - Stock de finca debería ser max(0, 20-30) = 0, no -10
            ArgumentCaptor<FincaProducto> fpCaptor = ArgumentCaptor.forClass(FincaProducto.class);
            verify(fincaProductoWriteRepository).save(fpCaptor.capture());
            assertEquals(0, fpCaptor.getValue().getStock());
        }

        @Test
        @DisplayName("Debe rechazar cantidad negativa en salida")
        void debeRechazarCantidadNegativaEnSalida() {
            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> service.salida(almacenFincaProducto.getId(), -10.0, "Test")
            );
        }
    }

    // ==================== TRANSFERENCIA TESTS ====================

    @Nested
    @DisplayName("transferir() Tests")
    class TransferirTests {

        private Almacen almacenDestino;
        private AlmacenFincaProducto afpDestino;

        @BeforeEach
        void setUpTransferencia() {
            almacenDestino = new Almacen();
            almacenDestino.setId(UUID.randomUUID());
            almacenDestino.setNombre("Almacen Secundario");
            almacenDestino.setFinca(finca); // Misma finca
            almacenDestino.setActivo(true);

            afpDestino = new AlmacenFincaProducto();
            afpDestino.setId(UUID.randomUUID());
            afpDestino.setAlmacen(almacenDestino);
            afpDestino.setFincaProducto(fincaProducto);
            afpDestino.setStock(20);
            afpDestino.setActivo(true);
        }

        @Test
        @DisplayName("Debe transferir entre almacenes de la misma finca")
        void debeTransferirEntreAlmacenesDeLaMismaFinca() {
            // Arrange
            Double cantidad = 15.0;
            Double stockOrigenAnterior = 50.0;
            Double stockDestinoAnterior = 20.0;

            almacenFincaProducto.setStock(stockOrigenAnterior);
            afpDestino.setStock(stockDestinoAnterior);

            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));
            when(almacenRepository.findById(almacenDestino.getId()))
                    .thenReturn(Optional.of(almacenDestino));
            when(repositoryQuery.findByAlmacenIdAndFincaProductoIdAndActivoTrue(
                    almacenDestino.getId(), fincaProductoId))
                    .thenReturn(Optional.of(afpDestino));
            when(repositoryCommand.save(any(AlmacenFincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            service.transferir(almacenFincaProducto.getId(), almacenDestino.getId(), cantidad, "Test");

            // Assert
            verify(repositoryCommand, times(2)).save(any(AlmacenFincaProducto.class));
            verify(movimientoStockService, times(2)).registrar(any());
            // Stock total de finca no debería cambiar
            verify(fincaProductoWriteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe rechazar transferencia con stock insuficiente")
        void debeRechazarTransferenciaConStockInsuficiente() {
            // Arrange
            almacenFincaProducto.setStock(10);
            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> service.transferir(almacenFincaProducto.getId(),
                            almacenDestino.getId(), 50.0, "Test")
            );
        }

        @Test
        @DisplayName("Debe rechazar transferencia a almacen de otra finca")
        void debeRechazarTransferenciaAAlmacenDeOtraFinca() {
            // Arrange
            Finca otraFinca = new Finca();
            otraFinca.setId(UUID.randomUUID());
            otraFinca.setName("Otra Finca");

            Almacen almacenOtraFinca = new Almacen();
            almacenOtraFinca.setId(UUID.randomUUID());
            almacenOtraFinca.setFinca(otraFinca);
            almacenOtraFinca.setActivo(true);

            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));
            when(almacenRepository.findById(almacenOtraFinca.getId()))
                    .thenReturn(Optional.of(almacenOtraFinca));

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> service.transferir(almacenFincaProducto.getId(),
                            almacenOtraFinca.getId(), 10.0, "Test")
            );
        }

        @Test
        @DisplayName("Debe rechazar transferencia a almacen inactivo")
        void debeRechazarTransferenciaAAlmacenInactivo() {
            // Arrange
            almacenDestino.setActivo(false);

            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));
            when(almacenRepository.findById(almacenDestino.getId()))
                    .thenReturn(Optional.of(almacenDestino));

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> service.transferir(almacenFincaProducto.getId(),
                            almacenDestino.getId(), 10.0, "Test")
            );
        }

        @Test
        @DisplayName("Debe crear producto en almacen destino si no existe")
        void debeCrearProductoEnAlmacenDestinoSiNoExiste() {
            // Arrange
            Double cantidad = 15.0;
            almacenFincaProducto.setStock(50);

            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));
            when(almacenRepository.findById(almacenDestino.getId()))
                    .thenReturn(Optional.of(almacenDestino));
            when(repositoryQuery.findByAlmacenIdAndFincaProductoIdAndActivoTrue(
                    almacenDestino.getId(), fincaProductoId))
                    .thenReturn(Optional.empty()); // No existe
            when(repositoryCommand.save(any(AlmacenFincaProducto.class)))
                    .thenAnswer(inv -> {
                        AlmacenFincaProducto saved = inv.getArgument(0);
                        if (saved.getId() == null) {
                            saved.setId(UUID.randomUUID());
                        }
                        return saved;
                    });

            // Act
            service.transferir(almacenFincaProducto.getId(), almacenDestino.getId(), cantidad, "Test");

            // Assert - Se debe crear el nuevo y actualizar ambos
            verify(repositoryCommand, times(3)).save(any(AlmacenFincaProducto.class));
        }
    }

    // ==================== ACTUALIZAR STOCK TESTS ====================

    @Nested
    @DisplayName("actualizarStock() Tests")
    class ActualizarStockTests {

        @Test
        @DisplayName("Debe actualizar stock y sincronizar con finca (incremento)")
        void debeActualizarStockYSincronizarConFincaIncremento() {
            // Arrange
            Double stockAnterior = 50.0;
            Double nuevoStock = 80.0;
            Double diferencia = nuevoStock - stockAnterior; // +30
            Double stockFincaAnterior = 100.0;

            almacenFincaProducto.setStock(stockAnterior);
            fincaProducto.setStock(stockFincaAnterior);

            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));
            when(repositoryCommand.save(any(AlmacenFincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(fincaProductoWriteRepository.save(any(FincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            service.actualizarStock(almacenFincaProducto.getId(), nuevoStock);

            // Assert
            ArgumentCaptor<AlmacenFincaProducto> afpCaptor = ArgumentCaptor.forClass(AlmacenFincaProducto.class);
            verify(repositoryCommand).save(afpCaptor.capture());
            assertEquals(nuevoStock, afpCaptor.getValue().getStock());

            ArgumentCaptor<FincaProducto> fpCaptor = ArgumentCaptor.forClass(FincaProducto.class);
            verify(fincaProductoWriteRepository).save(fpCaptor.capture());
            assertEquals(stockFincaAnterior + diferencia, fpCaptor.getValue().getStock());
        }

        @Test
        @DisplayName("Debe actualizar stock y sincronizar con finca (decremento)")
        void debeActualizarStockYSincronizarConFincaDecremento() {
            // Arrange
            Double stockAnterior = 50.0;
            Double nuevoStock = 30.0;
            Double diferencia = nuevoStock - stockAnterior; // -20
            Double stockFincaAnterior = 100.0;

            almacenFincaProducto.setStock(stockAnterior);
            fincaProducto.setStock(stockFincaAnterior);

            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));
            when(repositoryCommand.save(any(AlmacenFincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(fincaProductoWriteRepository.save(any(FincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            service.actualizarStock(almacenFincaProducto.getId(), nuevoStock);

            // Assert
            ArgumentCaptor<FincaProducto> fpCaptor = ArgumentCaptor.forClass(FincaProducto.class);
            verify(fincaProductoWriteRepository).save(fpCaptor.capture());
            assertEquals(stockFincaAnterior + diferencia, fpCaptor.getValue().getStock()); // 100 + (-20) = 80
        }

        @Test
        @DisplayName("Stock de finca no debe ser negativo en ajuste")
        void stockDeFincaNoDebeSerNegativoEnAjuste() {
            // Arrange
            almacenFincaProducto.setStock(100);
            fincaProducto.setStock(50); // Inconsistencia: almacen tiene mas que finca

            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));
            when(repositoryCommand.save(any(AlmacenFincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(fincaProductoWriteRepository.save(any(FincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            service.actualizarStock(almacenFincaProducto.getId(), 0.0); // Diferencia: -100

            // Assert
            ArgumentCaptor<FincaProducto> fpCaptor = ArgumentCaptor.forClass(FincaProducto.class);
            verify(fincaProductoWriteRepository).save(fpCaptor.capture());
            assertEquals(0, fpCaptor.getValue().getStock()); // max(0, 50 + (-100)) = 0
        }
    }

    // ==================== ASIGNAR PRODUCTO TESTS ====================

    @Nested
    @DisplayName("asignarProducto() Tests")
    class AsignarProductoTests {

        @Test
        @DisplayName("Debe asignar producto a almacen")
        void debeAsignarProductoAAlmacen() {
            // Arrange
            when(almacenRepository.findById(almacenId)).thenReturn(Optional.of(almacen));
            when(fincaProductoRepository.findById(fincaProductoId)).thenReturn(Optional.of(fincaProducto));
            when(repositoryQuery.existsByAlmacenIdAndFincaProductoIdAndActivoTrue(almacenId, fincaProductoId))
                    .thenReturn(false);
            when(repositoryCommand.save(any(AlmacenFincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            UUID result = service.asignarProducto(almacenId, fincaProductoId, 100.0, 10.0, 500.0);

            // Assert
            assertNotNull(result);
            verify(repositoryCommand).save(argThat(afp ->
                    afp.getStock() == 100 &&
                    afp.getStockMinimo() == 10 &&
                    afp.getStockMaximo() == 500 &&
                    afp.getActivo()
            ));
            verify(movimientoStockService).registrar(any());
        }

        @Test
        @DisplayName("Debe rechazar almacen no existente")
        void debeRechazarAlmacenNoExistente() {
            // Arrange
            when(almacenRepository.findById(almacenId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> service.asignarProducto(almacenId, fincaProductoId, 100.0, 10.0, 500.0)
            );
        }

        @Test
        @DisplayName("Debe rechazar producto ya asignado")
        void debeRechazarProductoYaAsignado() {
            // Arrange
            when(almacenRepository.findById(almacenId)).thenReturn(Optional.of(almacen));
            when(fincaProductoRepository.findById(fincaProductoId)).thenReturn(Optional.of(fincaProducto));
            when(repositoryQuery.existsByAlmacenIdAndFincaProductoIdAndActivoTrue(almacenId, fincaProductoId))
                    .thenReturn(true);

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> service.asignarProducto(almacenId, fincaProductoId, 100.0, 10.0, 500.0)
            );
        }

        @Test
        @DisplayName("No debe registrar movimiento si stock inicial es cero")
        void noDebeRegistrarMovimientoSiStockInicialEsCero() {
            // Arrange
            when(almacenRepository.findById(almacenId)).thenReturn(Optional.of(almacen));
            when(fincaProductoRepository.findById(fincaProductoId)).thenReturn(Optional.of(fincaProducto));
            when(repositoryQuery.existsByAlmacenIdAndFincaProductoIdAndActivoTrue(almacenId, fincaProductoId))
                    .thenReturn(false);
            when(repositoryCommand.save(any(AlmacenFincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            service.asignarProducto(almacenId, fincaProductoId, 0.0, 10.0, 500.0);

            // Assert
            verify(movimientoStockService, never()).registrar(any());
        }
    }

    // ==================== REMOVER PRODUCTO TESTS ====================

    @Nested
    @DisplayName("removerProducto() Tests")
    class RemoverProductoTests {

        @Test
        @DisplayName("Debe remover producto con stock cero")
        void debeRemoverProductoConStockCero() {
            // Arrange
            almacenFincaProducto.setStock(0);
            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));
            when(repositoryCommand.save(any(AlmacenFincaProducto.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            service.removerProducto(almacenFincaProducto.getId());

            // Assert
            verify(repositoryCommand).save(argThat(afp -> !afp.getActivo()));
        }

        @Test
        @DisplayName("Debe rechazar remover producto con stock mayor a cero")
        void debeRechazarRemoverProductoConStockMayorACero() {
            // Arrange
            almacenFincaProducto.setStock(10);
            when(repositoryQuery.findById(almacenFincaProducto.getId()))
                    .thenReturn(Optional.of(almacenFincaProducto));

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> service.removerProducto(almacenFincaProducto.getId())
            );
        }
    }

    // ==================== QUERIES TESTS ====================

    @Nested
    @DisplayName("Query Methods Tests")
    class QueryMethodsTests {

        @Test
        @DisplayName("Debe calcular stock total del almacen")
        void debeCalcularStockTotalDelAlmacen() {
            // Arrange
            when(repositoryQuery.sumStockByAlmacenId(almacenId)).thenReturn(250.0);

            // Act
            Double total = service.getStockTotalAlmacen(almacenId);

            // Assert
            assertEquals(250, total);
        }

        @Test
        @DisplayName("Debe retornar cero si stock total es null")
        void debeRetornarCeroSiStockTotalEsNull() {
            // Arrange
            when(repositoryQuery.sumStockByAlmacenId(almacenId)).thenReturn(null);

            // Act
            Double total = service.getStockTotalAlmacen(almacenId);

            // Assert
            assertEquals(0, total);
        }

        @Test
        @DisplayName("Debe verificar existencia de producto en almacen")
        void debeVerificarExistenciaDeProductoEnAlmacen() {
            // Arrange
            when(repositoryQuery.existsByAlmacenIdAndFincaProductoIdAndActivoTrue(almacenId, fincaProductoId))
                    .thenReturn(true);

            // Act
            boolean exists = service.existsProductoEnAlmacen(almacenId, fincaProductoId);

            // Assert
            assertTrue(exists);
        }
    }
}
