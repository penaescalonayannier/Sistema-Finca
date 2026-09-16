package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.report.domain.dto.CreateProduccionTerminadaResult;
import com.kynsoft.report.domain.dto.DeleteProduccionTerminadaResult;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.dto.ProduccionTerminadaDto;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.dto.UpdateProduccionTerminadaResult;
import com.kynsoft.report.domain.services.IFincaProductoService;
import com.kynsoft.report.domain.services.IAlmacenFincaProductoService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import com.kynsoft.report.domain.services.INumeracionService;
import com.kynsoft.report.domain.dto.TipoDocumento;
import com.kynsoft.report.infrastructure.entity.ProduccionTerminada;
import com.kynsoft.report.infrastructure.repository.command.ProduccionTerminadaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ProduccionTerminadaReadDataJPARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProduccionTerminadaService Unit Tests")
class ProduccionTerminadaServiceImplTest {

    @Mock
    private ProduccionTerminadaWriteDataJPARepository repositoryCommand;

    @Mock
    private ProduccionTerminadaReadDataJPARepository repositoryQuery;

    @Mock
    private IFincaProductoService fincaProductoService;

    @Mock
    private ITrabajadorService trabajadorService;

    @Mock
    private IAlmacenFincaProductoService almacenFincaProductoService;

    @Mock
    private INumeracionService numeracionService;

    @InjectMocks
    private ProduccionTerminadaServiceImpl produccionTerminadaService;

    private UUID fincaId;
    private UUID productoId;
    private UUID trabajadorEntregaId;
    private UUID trabajadorRecibeId;
    private TrabajadorDto trabajadorEntrega;
    private TrabajadorDto trabajadorRecibe;
    private FincaProductoDto fincaProductoDto;

    @BeforeEach
    void setUp() {
        fincaId = UUID.randomUUID();
        productoId = UUID.randomUUID();
        trabajadorEntregaId = UUID.randomUUID();
        trabajadorRecibeId = UUID.randomUUID();

        trabajadorEntrega = TrabajadorDto.builder()
                .id(trabajadorEntregaId)
                .nombre("Juan Perez")
                .fincaId(fincaId)
                .activo(true)
                .build();

        trabajadorRecibe = TrabajadorDto.builder()
                .id(trabajadorRecibeId)
                .nombre("Maria Garcia")
                .fincaId(fincaId)
                .activo(true)
                .build();

        fincaProductoDto = FincaProductoDto.builder()
                .id(UUID.randomUUID())
                .fincaId(fincaId)
                .productoId(productoId)
                .stock(100)
                .build();
    }

    @Nested
    @DisplayName("create() Tests")
    class CreateTests {

        @Test
        @DisplayName("RN-03: Debe rechazar cantidad menor o igual a cero")
        void debeRechazarCantidadMenorOIgualACero() {
            // Arrange
            ProduccionTerminadaDto dto = ProduccionTerminadaDto.builder()
                    .id(UUID.randomUUID())
                    .fincaId(fincaId)
                    .productoId(productoId)
                    .cantidadTerminada(0)
                    .trabajadorEntregaId(trabajadorEntregaId)
                    .trabajadorRecibeId(trabajadorRecibeId)
                    .build();

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> produccionTerminadaService.create(dto)
            );
        }

        @Test
        @DisplayName("RN-03: Debe rechazar cantidad negativa")
        void debeRechazarCantidadNegativa() {
            // Arrange
            ProduccionTerminadaDto dto = ProduccionTerminadaDto.builder()
                    .id(UUID.randomUUID())
                    .fincaId(fincaId)
                    .productoId(productoId)
                    .cantidadTerminada(-10)
                    .trabajadorEntregaId(trabajadorEntregaId)
                    .trabajadorRecibeId(trabajadorRecibeId)
                    .build();

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> produccionTerminadaService.create(dto)
            );
        }

        @Test
        @DisplayName("RN-01: Debe rechazar producto no asignado a finca")
        void debeRechazarProductoNoAsignadoAFinca() {
            // Arrange
            ProduccionTerminadaDto dto = ProduccionTerminadaDto.builder()
                    .id(UUID.randomUUID())
                    .fincaId(fincaId)
                    .productoId(productoId)
                    .cantidadTerminada(50)
                    .trabajadorEntregaId(trabajadorEntregaId)
                    .trabajadorRecibeId(trabajadorRecibeId)
                    .build();

            when(fincaProductoService.obtenerRelacion(fincaId, productoId))
                    .thenThrow(BusinessNotFoundException.class);

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> produccionTerminadaService.create(dto)
            );
        }

        @Test
        @DisplayName("RN-05: Debe rechazar trabajadores iguales")
        void debeRechazarTrabajadoresIguales() {
            // Arrange
            ProduccionTerminadaDto dto = ProduccionTerminadaDto.builder()
                    .id(UUID.randomUUID())
                    .fincaId(fincaId)
                    .productoId(productoId)
                    .cantidadTerminada(50)
                    .trabajadorEntregaId(trabajadorEntregaId)
                    .trabajadorRecibeId(trabajadorEntregaId) // Mismo trabajador
                    .build();

            when(fincaProductoService.obtenerRelacion(fincaId, productoId)).thenReturn(fincaProductoDto);
            when(trabajadorService.findById(trabajadorEntregaId)).thenReturn(trabajadorEntrega);

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> produccionTerminadaService.create(dto)
            );
        }

        @Test
        @DisplayName("RN-04: Debe rechazar trabajador que entrega de otra finca")
        void debeRechazarTrabajadorEntregaDeOtraFinca() {
            // Arrange
            UUID otraFincaId = UUID.randomUUID();
            TrabajadorDto trabajadorOtraFinca = TrabajadorDto.builder()
                    .id(trabajadorEntregaId)
                    .nombre("Pedro Lopez")
                    .fincaId(otraFincaId) // Otra finca
                    .activo(true)
                    .build();

            ProduccionTerminadaDto dto = ProduccionTerminadaDto.builder()
                    .id(UUID.randomUUID())
                    .fincaId(fincaId)
                    .productoId(productoId)
                    .cantidadTerminada(50)
                    .trabajadorEntregaId(trabajadorEntregaId)
                    .trabajadorRecibeId(trabajadorRecibeId)
                    .build();

            when(fincaProductoService.obtenerRelacion(fincaId, productoId)).thenReturn(fincaProductoDto);
            when(trabajadorService.findById(trabajadorEntregaId)).thenReturn(trabajadorOtraFinca);
            when(trabajadorService.findById(trabajadorRecibeId)).thenReturn(trabajadorRecibe);

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> produccionTerminadaService.create(dto)
            );
        }

        @Test
        @DisplayName("RN-04: Debe rechazar trabajador que recibe de otra finca")
        void debeRechazarTrabajadorRecibeDeOtraFinca() {
            // Arrange
            UUID otraFincaId = UUID.randomUUID();
            TrabajadorDto trabajadorOtraFinca = TrabajadorDto.builder()
                    .id(trabajadorRecibeId)
                    .nombre("Pedro Lopez")
                    .fincaId(otraFincaId) // Otra finca
                    .activo(true)
                    .build();

            ProduccionTerminadaDto dto = ProduccionTerminadaDto.builder()
                    .id(UUID.randomUUID())
                    .fincaId(fincaId)
                    .productoId(productoId)
                    .cantidadTerminada(50)
                    .trabajadorEntregaId(trabajadorEntregaId)
                    .trabajadorRecibeId(trabajadorRecibeId)
                    .build();

            when(fincaProductoService.obtenerRelacion(fincaId, productoId)).thenReturn(fincaProductoDto);
            when(trabajadorService.findById(trabajadorEntregaId)).thenReturn(trabajadorEntrega);
            when(trabajadorService.findById(trabajadorRecibeId)).thenReturn(trabajadorOtraFinca);

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> produccionTerminadaService.create(dto)
            );
        }

        @Test
        @DisplayName("RN-02: Debe rechazar producción sin almacén receptor")
        void debeRechazarProduccionSinAlmacenReceptor() {
            // Arrange
            ProduccionTerminadaDto dto = ProduccionTerminadaDto.builder()
                    .id(UUID.randomUUID())
                    .fincaId(fincaId)
                    .productoId(productoId)
                    .cantidadTerminada(50)
                    .trabajadorEntregaId(trabajadorEntregaId)
                    .trabajadorRecibeId(trabajadorRecibeId)
                    .observaciones("Produccion de prueba")
                    .build();

            assertThrows(BusinessNotFoundException.class, () -> produccionTerminadaService.create(dto));
            verify(fincaProductoService, never()).entradaProduccion(any(), any(), any(), anyString(), any(UUID.class));
        }

        @Test
        @DisplayName("Debe crear producción vinculada y registrar una única entrada de almacén")
        void debeCrearProduccionVinculadaAlAlmacen() {
            UUID almacenId = UUID.randomUUID();
            UUID almacenFincaProductoId = UUID.randomUUID();
            UUID fincaProductoId = fincaProductoDto.getId();
            AlmacenFincaProductoDto almacenProducto = AlmacenFincaProductoDto.builder()
                    .id(almacenFincaProductoId)
                    .almacenId(almacenId)
                    .fincaProductoId(fincaProductoId)
                    .stock(10.0)
                    .build();
            ProduccionTerminadaDto dto = ProduccionTerminadaDto.builder()
                    .cantidadTerminada(1.5)
                    .trabajadorEntregaId(trabajadorEntregaId)
                    .trabajadorRecibeId(trabajadorRecibeId)
                    .observaciones("Yuca cosechada")
                    .build();

            when(almacenFincaProductoService.findById(almacenFincaProductoId)).thenReturn(almacenProducto);
            when(fincaProductoService.getById(fincaProductoId)).thenReturn(fincaProductoDto);
            when(trabajadorService.findById(trabajadorEntregaId)).thenReturn(trabajadorEntrega);
            when(trabajadorService.findById(trabajadorRecibeId)).thenReturn(trabajadorRecibe);
            when(numeracionService.generarSiguienteNumero(fincaId, TipoDocumento.PRODUCCION))
                    .thenReturn("PT-2026-00001");
            when(repositoryCommand.save(any(ProduccionTerminada.class))).thenAnswer(invocation -> invocation.getArgument(0));

            CreateProduccionTerminadaResult result = produccionTerminadaService
                    .createEnAlmacen(almacenId, almacenFincaProductoId, dto);

            assertNotNull(result.getId());
            assertEquals(10.0, result.getStockAnterior());
            assertEquals(11.5, result.getStockNuevo());
            verify(almacenFincaProductoService).registrarEntradaProduccionTerminada(
                    almacenFincaProductoId, 1.5, result.getId(), contains("Yuca cosechada"), isNull());
            verify(fincaProductoService, never()).entradaProduccion(
                    any(), any(), any(), anyString(), any(UUID.class));
        }
    }

    @Nested
    @DisplayName("update() Tests")
    class UpdateTests {

        @Test
        @DisplayName("Debe rechazar produccion no encontrada")
        void debeRechazarProduccionNoEncontrada() {
            // Arrange
            UUID produccionId = UUID.randomUUID();
            ProduccionTerminadaDto dto = ProduccionTerminadaDto.builder()
                    .id(produccionId)
                    .cantidadTerminada(50)
                    .build();

            when(repositoryQuery.findById(produccionId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> produccionTerminadaService.update(dto)
            );
        }

        @Test
        @DisplayName("Debe rechazar modificar produccion anulada")
        void debeRechazarModificarProduccionAnulada() {
            // Arrange
            UUID produccionId = UUID.randomUUID();
            ProduccionTerminada entity = new ProduccionTerminada();
            entity.setId(produccionId);
            entity.setActivo(false); // Anulada

            ProduccionTerminadaDto dto = ProduccionTerminadaDto.builder()
                    .id(produccionId)
                    .cantidadTerminada(50)
                    .build();

            when(repositoryQuery.findById(produccionId)).thenReturn(Optional.of(entity));

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> produccionTerminadaService.update(dto)
            );
        }

        @Test
        @DisplayName("RN-06: Debe rechazar ajuste que deja stock negativo")
        void debeRechazarAjusteQueDejaStockNegativo() {
            // Arrange
            UUID produccionId = UUID.randomUUID();
            ProduccionTerminada entity = new ProduccionTerminada();
            entity.setId(produccionId);
            entity.setFincaId(fincaId);
            entity.setProductoId(productoId);
            entity.setCantidadTerminada(100); // Cantidad original
            entity.setActivo(true);

            ProduccionTerminadaDto dto = ProduccionTerminadaDto.builder()
                    .id(produccionId)
                    .cantidadTerminada(20) // Reducir de 100 a 20, ajuste de -80
                    .trabajadorEntregaId(trabajadorEntregaId)
                    .trabajadorRecibeId(trabajadorRecibeId)
                    .build();

            when(repositoryQuery.findById(produccionId)).thenReturn(Optional.of(entity));
            when(trabajadorService.findById(trabajadorEntregaId)).thenReturn(trabajadorEntrega);
            when(trabajadorService.findById(trabajadorRecibeId)).thenReturn(trabajadorRecibe);
            when(fincaProductoService.obtenerStock(fincaId, productoId)).thenReturn(50.0); // Stock actual

            // Act & Assert - Ajuste de -80 con stock de 50 deja -30 (negativo)
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> produccionTerminadaService.update(dto)
            );
        }

        @Test
        @DisplayName("RN-06: Debe actualizar produccion y ajustar stock correctamente")
        void debeActualizarProduccionYAjustarStock() {
            // Arrange
            UUID produccionId = UUID.randomUUID();
            ProduccionTerminada entity = new ProduccionTerminada();
            entity.setId(produccionId);
            entity.setFincaId(fincaId);
            entity.setProductoId(productoId);
            entity.setCantidadTerminada(50); // Cantidad original
            UUID almacenFincaProductoId = UUID.randomUUID();
            entity.setAlmacenFincaProductoId(almacenFincaProductoId);
            entity.setActivo(true);

            ProduccionTerminadaDto dto = ProduccionTerminadaDto.builder()
                    .id(produccionId)
                    .productoId(productoId)
                    .cantidadTerminada(70) // Incrementar de 50 a 70, ajuste de +20
                    .trabajadorEntregaId(trabajadorEntregaId)
                    .trabajadorRecibeId(trabajadorRecibeId)
                    .observaciones("Actualizado")
                    .build();

            when(repositoryQuery.findById(produccionId)).thenReturn(Optional.of(entity));
            when(trabajadorService.findById(trabajadorEntregaId)).thenReturn(trabajadorEntrega);
            when(trabajadorService.findById(trabajadorRecibeId)).thenReturn(trabajadorRecibe);
            when(almacenFincaProductoService.findById(almacenFincaProductoId)).thenReturn(
                    AlmacenFincaProductoDto.builder().id(almacenFincaProductoId).stock(100.0).build());
            when(repositoryCommand.save(any(ProduccionTerminada.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            UpdateProduccionTerminadaResult result = produccionTerminadaService.update(dto);

            // Assert
            assertNotNull(result);
            assertEquals(100, result.getStockAnterior());
            assertEquals(120, result.getStockNuevo()); // 100 + 20
            assertEquals(20.0, result.getAjuste());

            verify(almacenFincaProductoService).actualizarEntradaProduccion(
                    eq(almacenFincaProductoId), eq(50.0), eq(70.0), eq(produccionId), anyString());
        }
    }

    @Nested
    @DisplayName("delete() Tests")
    class DeleteTests {

        @Test
        @DisplayName("Debe rechazar eliminar produccion no encontrada")
        void debeRechazarEliminarProduccionNoEncontrada() {
            // Arrange
            UUID produccionId = UUID.randomUUID();
            when(repositoryQuery.findById(produccionId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> produccionTerminadaService.delete(produccionId)
            );
        }

        @Test
        @DisplayName("Debe rechazar eliminar produccion ya anulada")
        void debeRechazarEliminarProduccionYaAnulada() {
            // Arrange
            UUID produccionId = UUID.randomUUID();
            ProduccionTerminada entity = new ProduccionTerminada();
            entity.setId(produccionId);
            entity.setActivo(false); // Ya anulada

            when(repositoryQuery.findById(produccionId)).thenReturn(Optional.of(entity));

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> produccionTerminadaService.delete(produccionId)
            );
        }

        @Test
        @DisplayName("RN-07: Debe rechazar reversion que deja stock negativo")
        void debeRechazarReversionQueDejaStockNegativo() {
            // Arrange
            UUID produccionId = UUID.randomUUID();
            ProduccionTerminada entity = new ProduccionTerminada();
            entity.setId(produccionId);
            entity.setFincaId(fincaId);
            entity.setProductoId(productoId);
            entity.setCantidadTerminada(100); // Cantidad a revertir
            entity.setActivo(true);

            when(repositoryQuery.findById(produccionId)).thenReturn(Optional.of(entity));
            when(fincaProductoService.obtenerStock(fincaId, productoId)).thenReturn(50.0); // Stock actual menor que cantidad

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> produccionTerminadaService.delete(produccionId)
            );
        }

        @Test
        @DisplayName("RN-07: Debe eliminar produccion y revertir stock correctamente")
        void debeEliminarProduccionYRevertirStock() {
            // Arrange
            UUID produccionId = UUID.randomUUID();
            ProduccionTerminada entity = new ProduccionTerminada();
            entity.setId(produccionId);
            entity.setFincaId(fincaId);
            entity.setProductoId(productoId);
            entity.setCantidadTerminada(50);
            UUID almacenFincaProductoId = UUID.randomUUID();
            entity.setAlmacenFincaProductoId(almacenFincaProductoId);
            entity.setActivo(true);

            when(repositoryQuery.findById(produccionId)).thenReturn(Optional.of(entity));
            when(almacenFincaProductoService.findById(almacenFincaProductoId)).thenReturn(
                    AlmacenFincaProductoDto.builder().id(almacenFincaProductoId).stock(100.0).build());
            when(repositoryCommand.save(any(ProduccionTerminada.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            DeleteProduccionTerminadaResult result = produccionTerminadaService.delete(produccionId);

            // Assert
            assertNotNull(result);
            assertEquals(100, result.getStockAnterior());
            assertEquals(50, result.getStockNuevo()); // 100 - 50
            assertEquals(50.0, result.getCantidadRevertida());

            verify(almacenFincaProductoService).revertirEntradaProduccion(
                    eq(almacenFincaProductoId), eq(50.0), eq(produccionId), anyString());
        }
    }

    @Nested
    @DisplayName("findById() Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Debe encontrar produccion por ID")
        void debeEncontrarProduccionPorId() {
            // Arrange
            UUID produccionId = UUID.randomUUID();
            ProduccionTerminada entity = new ProduccionTerminada();
            entity.setId(produccionId);
            entity.setFincaId(fincaId);
            entity.setProductoId(productoId);
            entity.setCantidadTerminada(50);
            entity.setTrabajadorEntregaId(trabajadorEntregaId);
            entity.setTrabajadorRecibeId(trabajadorRecibeId);
            entity.setFecha(LocalDateTime.now());
            entity.setActivo(true);

            when(repositoryQuery.findByIdWithDetails(produccionId)).thenReturn(Optional.of(entity));

            // Act
            ProduccionTerminadaDto result = produccionTerminadaService.findById(produccionId);

            // Assert
            assertNotNull(result);
            assertEquals(produccionId, result.getId());
            assertEquals(50.0, result.getCantidadTerminada());
        }

        @Test
        @DisplayName("Debe lanzar excepcion si no encuentra produccion")
        void debeLanzarExcepcionSiNoEncuentraProduccion() {
            // Arrange
            UUID produccionId = UUID.randomUUID();
            when(repositoryQuery.findByIdWithDetails(produccionId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> produccionTerminadaService.findById(produccionId)
            );
        }
    }
}
