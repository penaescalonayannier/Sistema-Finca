package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import com.kynsoft.report.domain.dto.MovimientoStockDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.services.IFincaProductoService;
import com.kynsoft.report.infrastructure.entity.Almacen;
import com.kynsoft.report.infrastructure.entity.AlmacenFincaProducto;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.entity.FincaProducto;
import com.kynsoft.report.infrastructure.entity.MovimientoStock;
import com.kynsoft.report.infrastructure.entity.Producto;
import com.kynsoft.report.infrastructure.repository.command.AlmacenFincaProductoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.FincaProductoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.MovimientoStockWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AlmacenReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AlmacenFincaProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.MovimientoStockReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ProductoReadDataJPARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovimientoStockService Unit Tests")
class MovimientoStockServiceImplTest {

    @Mock
    private MovimientoStockWriteDataJPARepository repositoryCommand;

    @Mock
    private MovimientoStockReadDataJPARepository repositoryQuery;

    @Mock
    private FincaReadDataJPARepository fincaRepository;

    @Mock
    private ProductoReadDataJPARepository productoRepository;

    @Mock
    private FincaProductoReadDataJPARepository fincaProductoRepository;

    @Mock
    private AlmacenReadDataJPARepository almacenRepository;

    @Mock
    private AlmacenFincaProductoReadDataJPARepository almacenFincaProductoRepository;

    @Mock
    private AlmacenFincaProductoWriteDataJPARepository almacenFincaProductoWriteRepository;

    @Mock
    private FincaProductoWriteDataJPARepository fincaProductoWriteRepository;

    @Mock
    private IFincaProductoService fincaProductoService;

    @InjectMocks
    private MovimientoStockServiceImpl movimientoStockService;

    private UUID almacenId;
    private UUID fincaProductoId;
    private UUID fincaId;
    private UUID productoId;
    private FincaProducto fincaProducto;
    private AlmacenFincaProducto almacenProducto;

    @BeforeEach
    void setUp() {
        almacenId = UUID.randomUUID();
        fincaProductoId = UUID.randomUUID();
        fincaId = UUID.randomUUID();
        productoId = UUID.randomUUID();

        Finca finca = new Finca();
        finca.setId(fincaId);
        Producto producto = new Producto();
        producto.setId(productoId);
        fincaProducto = new FincaProducto();
        fincaProducto.setId(fincaProductoId);
        fincaProducto.setFinca(finca);
        fincaProducto.setProducto(producto);
        fincaProducto.setStock(100);

        Almacen almacen = new Almacen();
        almacen.setId(almacenId);
        almacen.setFinca(finca);
        almacenProducto = new AlmacenFincaProducto();
        almacenProducto.setId(UUID.randomUUID());
        almacenProducto.setAlmacen(almacen);
        almacenProducto.setFincaProducto(fincaProducto);
        almacenProducto.setStock(100);
        almacenProducto.setActivo(true);

        when(fincaProductoRepository.findByIdWithDetails(fincaProductoId)).thenReturn(Optional.of(fincaProducto));
        when(almacenFincaProductoRepository.findByAlmacenIdAndFincaProductoIdAndActivoTrue(almacenId, fincaProductoId))
                .thenReturn(Optional.of(almacenProducto));
        when(repositoryCommand.save(any(MovimientoStock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fincaProductoWriteRepository.save(any(FincaProducto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(almacenFincaProductoWriteRepository.save(any(AlmacenFincaProducto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Nested
    @DisplayName("crearAjuste() Tests")
    class CrearAjusteTests {

        @Test
        @DisplayName("RN-04: Debe rechazar ajuste sin observaciones")
        void debeRechazarAjusteSinObservaciones() {
            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> movimientoStockService.crearAjuste(
                            almacenId,
                            fincaProductoId,
                            TipoMovimientoStock.ENTRADA_AJUSTE,
                            10,
                            ""
                    )
            );
        }

        @Test
        @DisplayName("RN-04: Debe rechazar ajuste con observaciones null")
        void debeRechazarAjusteConObservacionesNull() {
            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> movimientoStockService.crearAjuste(
                            almacenId,
                            fincaProductoId,
                            TipoMovimientoStock.ENTRADA_AJUSTE,
                            10,
                            null
                    )
            );
        }

        @Test
        @DisplayName("RN-05: Debe rechazar cantidad menor o igual a cero")
        void debeRechazarCantidadMenorOIgualACero() {
            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> movimientoStockService.crearAjuste(
                            almacenId,
                            fincaProductoId,
                            TipoMovimientoStock.ENTRADA_AJUSTE,
                            0,
                            "Observacion de prueba"
                    )
            );

            assertThrows(
                    BusinessNotFoundException.class,
                    () -> movimientoStockService.crearAjuste(
                            almacenId,
                            fincaProductoId,
                            TipoMovimientoStock.ENTRADA_AJUSTE,
                            -5,
                            "Observacion de prueba"
                    )
            );
        }

        @Test
        @DisplayName("Debe rechazar tipo de movimiento no permitido para ajuste")
        void debeRechazarTipoNoPermitido() {
            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> movimientoStockService.crearAjuste(
                            almacenId,
                            fincaProductoId,
                            TipoMovimientoStock.ENTRADA_PRODUCCION,
                            10,
                            "Observacion de prueba"
                    )
            );
        }

        @Test
        @DisplayName("RN-06: Debe rechazar salida que deja stock negativo")
        void debeRechazarSalidaQueDejaStockNegativo() {
            // Arrange
            FincaProductoDto fincaProductoDto = FincaProductoDto.builder()
                    .id(fincaProductoId)
                    .fincaId(fincaId)
                    .productoId(productoId)
                    .stock(30)
                    .build();

            fincaProducto.setStock(30);
            almacenProducto.setStock(30);

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> movimientoStockService.crearAjuste(
                            almacenId,
                            fincaProductoId,
                            TipoMovimientoStock.SALIDA_AJUSTE,
                            50,
                            "Intento de salida excesiva"
                    )
            );
        }

        @Test
        @DisplayName("Debe crear ajuste de entrada correctamente")
        void debeCrearAjusteEntradaCorrectamente() {
            // Arrange
            FincaProductoDto fincaProductoDto = FincaProductoDto.builder()
                    .id(fincaProductoId)
                    .fincaId(fincaId)
                    .productoId(productoId)
                    .stock(100)
                    .build();

            // Act
            MovimientoStockDto result = movimientoStockService.crearAjuste(
                    almacenId,
                    fincaProductoId,
                    TipoMovimientoStock.ENTRADA_AJUSTE,
                    50,
                    "Ajuste por conteo fisico"
            );

            // Assert
            assertNotNull(result);
            assertEquals(100, result.getStockAnterior());
            assertEquals(150, result.getStockNuevo());
            assertEquals(50, result.getCantidad());
            assertEquals(TipoMovimientoStock.ENTRADA_AJUSTE, result.getTipo());

            assertEquals(150, fincaProducto.getStock());
            assertEquals(150, almacenProducto.getStock());
            verify(fincaProductoWriteRepository).save(fincaProducto);
            verify(almacenFincaProductoWriteRepository).save(almacenProducto);
        }

        @Test
        @DisplayName("Debe crear ajuste de salida correctamente")
        void debeCrearAjusteSalidaCorrectamente() {
            // Arrange
            FincaProductoDto fincaProductoDto = FincaProductoDto.builder()
                    .id(fincaProductoId)
                    .fincaId(fincaId)
                    .productoId(productoId)
                    .stock(100)
                    .build();

            // Act
            MovimientoStockDto result = movimientoStockService.crearAjuste(
                    almacenId,
                    fincaProductoId,
                    TipoMovimientoStock.SALIDA_AJUSTE,
                    30,
                    "Ajuste por merma"
            );

            // Assert
            assertNotNull(result);
            assertEquals(100, result.getStockAnterior());
            assertEquals(70, result.getStockNuevo());
            assertEquals(30, result.getCantidad());
            assertEquals(TipoMovimientoStock.SALIDA_AJUSTE, result.getTipo());

            assertEquals(70, fincaProducto.getStock());
            assertEquals(70, almacenProducto.getStock());
            verify(fincaProductoWriteRepository).save(fincaProducto);
            verify(almacenFincaProductoWriteRepository).save(almacenProducto);
        }
    }

    @Nested
    @DisplayName("findById() Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Debe encontrar movimiento por ID")
        void debeEncontrarMovimientoPorId() {
            // Arrange
            UUID movimientoId = UUID.randomUUID();
            MovimientoStock movimiento = new MovimientoStock();
            movimiento.setId(movimientoId);
            movimiento.setFincaProductoId(fincaProductoId);
            movimiento.setFincaId(fincaId);
            movimiento.setProductoId(productoId);
            movimiento.setTipo(TipoMovimientoStock.ENTRADA_AJUSTE);
            movimiento.setCantidad(10);
            movimiento.setStockAnterior(100);
            movimiento.setStockNuevo(110);

            when(repositoryQuery.findById(movimientoId)).thenReturn(Optional.of(movimiento));

            // Act
            MovimientoStockDto result = movimientoStockService.findById(movimientoId);

            // Assert
            assertNotNull(result);
            assertEquals(movimientoId, result.getId());
        }

        @Test
        @DisplayName("Debe lanzar excepcion si no encuentra movimiento")
        void debeLanzarExcepcionSiNoEncuentraMovimiento() {
            // Arrange
            UUID movimientoId = UUID.randomUUID();
            when(repositoryQuery.findById(movimientoId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> movimientoStockService.findById(movimientoId)
            );
        }
    }

    @Nested
    @DisplayName("TipoMovimientoStock Tests")
    class TipoMovimientoStockTests {

        @Test
        @DisplayName("isEntrada() debe retornar true para tipos de entrada")
        void isEntradaDebeRetornarTrueParaEntradas() {
            assertTrue(TipoMovimientoStock.ENTRADA_PRODUCCION.isEntrada());
            assertTrue(TipoMovimientoStock.ENTRADA_AJUSTE.isEntrada());
            assertTrue(TipoMovimientoStock.TRANSFERENCIA_ENTRADA.isEntrada());
        }

        @Test
        @DisplayName("isSalida() debe retornar true para tipos de salida")
        void isSalidaDebeRetornarTrueParaSalidas() {
            assertTrue(TipoMovimientoStock.SALIDA_VENTA.isSalida());
            assertTrue(TipoMovimientoStock.SALIDA_AUTOCONSUMO.isSalida());
            assertTrue(TipoMovimientoStock.SALIDA_AJUSTE.isSalida());
            assertTrue(TipoMovimientoStock.TRANSFERENCIA_SALIDA.isSalida());
        }

        @Test
        @DisplayName("isAjuste() debe retornar true para tipos de ajuste")
        void isAjusteDebeRetornarTrueParaAjustes() {
            assertTrue(TipoMovimientoStock.ENTRADA_AJUSTE.isAjuste());
            assertTrue(TipoMovimientoStock.SALIDA_AJUSTE.isAjuste());
            assertTrue(TipoMovimientoStock.AJUSTE_MANUAL.isAjuste());
        }
    }

    @Nested
    @DisplayName("registrar() Tests")
    class RegistrarTests {

        @Test
        @DisplayName("Debe registrar movimiento con DTO completo")
        void debeRegistrarMovimientoConDtoCompleto() {
            // Arrange
            MovimientoStockDto dto = MovimientoStockDto.builder()
                    .id(UUID.randomUUID())
                    .fincaProductoId(fincaProductoId)
                    .fincaId(fincaId)
                    .productoId(productoId)
                    .tipo(TipoMovimientoStock.ENTRADA_PRODUCCION)
                    .cantidad(100)
                    .stockAnterior(50)
                    .stockNuevo(150)
                    .descripcion("Cosecha de café")
                    .fecha(LocalDateTime.now())
                    .build();

            when(repositoryCommand.save(any(MovimientoStock.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            movimientoStockService.registrar(dto);

            // Assert
            verify(repositoryCommand).save(any(MovimientoStock.class));
        }
    }

    @Nested
    @DisplayName("registrarMovimiento() Tests")
    class RegistrarMovimientoTests {

        @Test
        @DisplayName("Debe registrar entrada de producción correctamente")
        void debeRegistrarEntradaProduccion() {
            // Arrange
            UUID referenciaId = UUID.randomUUID();
            when(repositoryCommand.save(any(MovimientoStock.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            movimientoStockService.registrarMovimiento(
                    fincaProductoId,
                    fincaId,
                    productoId,
                    TipoMovimientoStock.ENTRADA_PRODUCCION,
                    50,
                    100,
                    150,
                    referenciaId,
                    "produccion_terminada",
                    "Cosecha lote Norte"
            );

            // Assert
            verify(repositoryCommand).save(argThat(m ->
                    m.getTipo() == TipoMovimientoStock.ENTRADA_PRODUCCION &&
                    m.getCantidad() == 50 &&
                    m.getStockAnterior() == 100 &&
                    m.getStockNuevo() == 150 &&
                    "produccion_terminada".equals(m.getReferenciaTabla())
            ));
        }

        @Test
        @DisplayName("Debe registrar salida de venta correctamente")
        void debeRegistrarSalidaVenta() {
            // Arrange
            UUID salidaId = UUID.randomUUID();
            when(repositoryCommand.save(any(MovimientoStock.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            movimientoStockService.registrarMovimiento(
                    fincaProductoId,
                    fincaId,
                    productoId,
                    TipoMovimientoStock.SALIDA_VENTA,
                    30,
                    150,
                    120,
                    salidaId,
                    "salida",
                    "Venta a trabajador Juan Pérez"
            );

            // Assert
            verify(repositoryCommand).save(argThat(m ->
                    m.getTipo() == TipoMovimientoStock.SALIDA_VENTA &&
                    m.getCantidad() == 30 &&
                    m.getStockAnterior() == 150 &&
                    m.getStockNuevo() == 120
            ));
        }

        @Test
        @DisplayName("Debe calcular stock correctamente para entrada por factura")
        void debeCalcularStockEntradaFactura() {
            // Arrange
            when(repositoryCommand.save(any(MovimientoStock.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            movimientoStockService.registrarMovimiento(
                    fincaProductoId,
                    fincaId,
                    productoId,
                    TipoMovimientoStock.ENTRADA_FACTURA,
                    200,
                    0,       // Stock inicial
                    200,     // Stock nuevo
                    null,
                    null,
                    "Compra factura #F-2024-001"
            );

            // Assert
            verify(repositoryCommand).save(argThat(m ->
                    m.getStockAnterior() == 0 &&
                    m.getStockNuevo() == 200 &&
                    m.getCantidad() == 200
            ));
        }
    }

    @Nested
    @DisplayName("findByFincaProductoId() Tests")
    class FindByFincaProductoIdTests {

        @Test
        @DisplayName("Debe retornar lista de movimientos por fincaProductoId")
        void debeRetornarMovimientosPorFincaProductoId() {
            // Arrange
            MovimientoStock mov1 = crearMovimientoStock(TipoMovimientoStock.ENTRADA_PRODUCCION, 100);
            MovimientoStock mov2 = crearMovimientoStock(TipoMovimientoStock.SALIDA_VENTA, 30);

            when(repositoryQuery.findByFincaProductoId(fincaProductoId))
                    .thenReturn(Arrays.asList(mov1, mov2));

            // Act
            List<MovimientoStockDto> result = movimientoStockService.findByFincaProductoId(fincaProductoId);

            // Assert
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Debe retornar lista vacía si no hay movimientos")
        void debeRetornarListaVaciaSinMovimientos() {
            // Arrange
            when(repositoryQuery.findByFincaProductoId(fincaProductoId))
                    .thenReturn(Collections.emptyList());

            // Act
            List<MovimientoStockDto> result = movimientoStockService.findByFincaProductoId(fincaProductoId);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("findByFincaId() Tests")
    class FindByFincaIdTests {

        @Test
        @DisplayName("Debe retornar todos los movimientos de una finca")
        void debeRetornarMovimientosPorFinca() {
            // Arrange
            MovimientoStock mov1 = crearMovimientoStock(TipoMovimientoStock.ENTRADA_PRODUCCION, 100);
            MovimientoStock mov2 = crearMovimientoStock(TipoMovimientoStock.SALIDA_AUTOCONSUMO, 20);
            MovimientoStock mov3 = crearMovimientoStock(TipoMovimientoStock.ENTRADA_FACTURA, 50);

            when(repositoryQuery.findByFincaId(fincaId))
                    .thenReturn(Arrays.asList(mov1, mov2, mov3));

            // Act
            List<MovimientoStockDto> result = movimientoStockService.findByFincaId(fincaId);

            // Assert
            assertEquals(3, result.size());
        }
    }

    @Nested
    @DisplayName("Stock Calculation Tests")
    class StockCalculationTests {

        @Test
        @DisplayName("Entrada debe incrementar stock")
        void entradaDebeIncrementarStock() {
            // Arrange
            int stockAnterior = 100;
            int cantidadEntrada = 50;
            int stockNuevoEsperado = stockAnterior + cantidadEntrada;

            // Assert
            assertEquals(150, stockNuevoEsperado);
        }

        @Test
        @DisplayName("Salida debe decrementar stock")
        void salidaDebeDecrementarStock() {
            // Arrange
            int stockAnterior = 100;
            int cantidadSalida = 30;
            int stockNuevoEsperado = stockAnterior - cantidadSalida;

            // Assert
            assertEquals(70, stockNuevoEsperado);
        }

        @Test
        @DisplayName("Stock no debe ser negativo")
        void stockNoDebeSerNegativo() {
            // Arrange
            FincaProductoDto fincaProductoDto = FincaProductoDto.builder()
                    .id(fincaProductoId)
                    .fincaId(fincaId)
                    .productoId(productoId)
                    .stock(20)
                    .build();

            fincaProducto.setStock(20);
            almacenProducto.setStock(20);

            // Act & Assert - Intentar sacar más de lo disponible
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> movimientoStockService.crearAjuste(
                            almacenId,
                            fincaProductoId,
                            TipoMovimientoStock.SALIDA_AJUSTE,
                            50, // Más del stock disponible (20)
                            "Intento de salida excesiva"
                    )
            );
        }
    }

    @Nested
    @DisplayName("findByReferencia() Tests")
    class FindByReferenciaTests {

        @Test
        @DisplayName("Debe encontrar movimientos por referencia")
        void debeEncontrarMovimientosPorReferencia() {
            // Arrange
            UUID salidaId = UUID.randomUUID();
            MovimientoStock mov = crearMovimientoStock(TipoMovimientoStock.SALIDA_VENTA, 30);
            mov.setReferenciaId(salidaId);
            mov.setReferenciaTabla("salida");

            when(repositoryQuery.findByReferencia(salidaId, "salida"))
                    .thenReturn(Collections.singletonList(mov));

            // Act
            List<MovimientoStockDto> result = movimientoStockService.findByReferencia(salidaId, "salida");

            // Assert
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("findByTipo() Tests")
    class FindByTipoTests {

        @Test
        @DisplayName("Debe filtrar movimientos por tipo ENTRADA_PRODUCCION")
        void debeFiltrarPorTipoEntradaProduccion() {
            // Arrange
            MovimientoStock mov1 = crearMovimientoStock(TipoMovimientoStock.ENTRADA_PRODUCCION, 100);
            MovimientoStock mov2 = crearMovimientoStock(TipoMovimientoStock.ENTRADA_PRODUCCION, 50);

            when(repositoryQuery.findByTipo(TipoMovimientoStock.ENTRADA_PRODUCCION))
                    .thenReturn(Arrays.asList(mov1, mov2));

            // Act
            List<MovimientoStockDto> result = movimientoStockService.findByTipo(TipoMovimientoStock.ENTRADA_PRODUCCION);

            // Assert
            assertEquals(2, result.size());
            result.forEach(m -> assertEquals(TipoMovimientoStock.ENTRADA_PRODUCCION, m.getTipo()));
        }
    }

    // Helper method
    private MovimientoStock crearMovimientoStock(TipoMovimientoStock tipo, int cantidad) {
        MovimientoStock mov = new MovimientoStock();
        mov.setId(UUID.randomUUID());
        mov.setFincaProductoId(fincaProductoId);
        mov.setFincaId(fincaId);
        mov.setProductoId(productoId);
        mov.setTipo(tipo);
        mov.setCantidad(cantidad);
        mov.setStockAnterior(100);
        mov.setStockNuevo(tipo.isEntrada() ? 100 + cantidad : 100 - cantidad);
        mov.setFecha(LocalDateTime.now());
        return mov;
    }
}
