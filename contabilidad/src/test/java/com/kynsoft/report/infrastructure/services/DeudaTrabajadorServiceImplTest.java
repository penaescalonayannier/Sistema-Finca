package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDto;
import com.kynsoft.report.domain.dto.FormaPago;
import com.kynsoft.report.domain.dto.MovimientoDeudaResult;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.IDeudaTrabajadorDetalleService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import com.kynsoft.report.infrastructure.entity.DeudaTrabajador;
import com.kynsoft.report.infrastructure.repository.command.DeudaTrabajadorWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DeudaTrabajadorReadDataJPARepository;
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
@DisplayName("DeudaTrabajadorService Unit Tests")
class DeudaTrabajadorServiceImplTest {

    @Mock
    private DeudaTrabajadorWriteDataJPARepository repositoryCommand;

    @Mock
    private DeudaTrabajadorReadDataJPARepository repositoryQuery;

    @Mock
    private ITrabajadorService trabajadorService;

    @Mock
    private IDeudaTrabajadorDetalleService detalleService;

    @InjectMocks
    private DeudaTrabajadorServiceImpl deudaTrabajadorService;

    private UUID trabajadorId;
    private TrabajadorDto trabajadorDto;

    @BeforeEach
    void setUp() {
        trabajadorId = UUID.randomUUID();
        trabajadorDto = TrabajadorDto.builder()
                .id(trabajadorId)
                .nombre("Juan Perez")
                .ruc("1234567890")
                .activo(true)
                .build();
    }

    @Nested
    @DisplayName("registrarPago() Tests")
    class RegistrarPagoTests {

        @Test
        @DisplayName("RN-02: Debe rechazar pago mayor que deuda actual")
        void debeRechazarPagoMayorQueDeuda() {
            // Arrange
            DeudaTrabajador deudaEntity = new DeudaTrabajador();
            deudaEntity.setId(UUID.randomUUID());
            deudaEntity.setTrabajadorId(trabajadorId);
            deudaEntity.setImporte(50.0);

            when(trabajadorService.findById(trabajadorId)).thenReturn(trabajadorDto);
            when(repositoryQuery.findByTrabajadorId(trabajadorId)).thenReturn(Optional.of(deudaEntity));

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> deudaTrabajadorService.registrarPago(
                            trabajadorId,
                            100.0,
                            FormaPago.EFECTIVO,
                            null,
                            "Pago excesivo"
                    )
            );
        }

        @Test
        @DisplayName("RN-03: Debe rechazar transferencia sin referencia bancaria")
        void debeRechazarTransferenciaSinReferencia() {
            // Arrange
            DeudaTrabajador deudaEntity = new DeudaTrabajador();
            deudaEntity.setId(UUID.randomUUID());
            deudaEntity.setTrabajadorId(trabajadorId);
            deudaEntity.setImporte(100.0);

            when(trabajadorService.findById(trabajadorId)).thenReturn(trabajadorDto);
            when(repositoryQuery.findByTrabajadorId(trabajadorId)).thenReturn(Optional.of(deudaEntity));

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> deudaTrabajadorService.registrarPago(
                            trabajadorId,
                            50.0,
                            FormaPago.TRANSFERENCIA,
                            null,
                            "Pago por transferencia"
                    )
            );
        }

        @Test
        @DisplayName("RN-03: Debe rechazar transferencia con referencia vacia")
        void debeRechazarTransferenciaConReferenciaVacia() {
            // Arrange
            DeudaTrabajador deudaEntity = new DeudaTrabajador();
            deudaEntity.setId(UUID.randomUUID());
            deudaEntity.setTrabajadorId(trabajadorId);
            deudaEntity.setImporte(100.0);

            when(trabajadorService.findById(trabajadorId)).thenReturn(trabajadorDto);
            when(repositoryQuery.findByTrabajadorId(trabajadorId)).thenReturn(Optional.of(deudaEntity));

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> deudaTrabajadorService.registrarPago(
                            trabajadorId,
                            50.0,
                            FormaPago.TRANSFERENCIA,
                            "",
                            "Pago por transferencia"
                    )
            );
        }

        @Test
        @DisplayName("Debe registrar pago en efectivo correctamente")
        void debeRegistrarPagoEfectivoCorrectamente() {
            // Arrange
            DeudaTrabajador deudaEntity = new DeudaTrabajador();
            deudaEntity.setId(UUID.randomUUID());
            deudaEntity.setTrabajadorId(trabajadorId);
            deudaEntity.setImporte(100.0);

            when(trabajadorService.findById(trabajadorId)).thenReturn(trabajadorDto);
            when(repositoryQuery.findByTrabajadorId(trabajadorId)).thenReturn(Optional.of(deudaEntity));
            when(repositoryCommand.save(any(DeudaTrabajador.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(detalleService.create(any())).thenReturn(UUID.randomUUID());

            // Act
            MovimientoDeudaResult result = deudaTrabajadorService.registrarPago(
                    trabajadorId,
                    30.0,
                    FormaPago.EFECTIVO,
                    null,
                    "Pago parcial"
            );

            // Assert
            assertNotNull(result);
            assertEquals(100.0, result.getSaldoAnterior());
            assertEquals(70.0, result.getSaldoNuevo());
        }

        @Test
        @DisplayName("Debe registrar pago por transferencia con referencia")
        void debeRegistrarPagoTransferenciaConReferencia() {
            // Arrange
            DeudaTrabajador deudaEntity = new DeudaTrabajador();
            deudaEntity.setId(UUID.randomUUID());
            deudaEntity.setTrabajadorId(trabajadorId);
            deudaEntity.setImporte(100.0);

            when(trabajadorService.findById(trabajadorId)).thenReturn(trabajadorDto);
            when(repositoryQuery.findByTrabajadorId(trabajadorId)).thenReturn(Optional.of(deudaEntity));
            when(repositoryCommand.save(any(DeudaTrabajador.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(detalleService.create(any())).thenReturn(UUID.randomUUID());

            // Act
            MovimientoDeudaResult result = deudaTrabajadorService.registrarPago(
                    trabajadorId,
                    50.0,
                    FormaPago.TRANSFERENCIA,
                    "REF-123456",
                    "Pago por transferencia"
            );

            // Assert
            assertNotNull(result);
            assertEquals(100.0, result.getSaldoAnterior());
            assertEquals(50.0, result.getSaldoNuevo());
        }
    }

    @Nested
    @DisplayName("registrarAjuste() Tests")
    class RegistrarAjusteTests {

        @Test
        @DisplayName("RN-07: Debe registrar ajuste positivo correctamente")
        void debeRegistrarAjustePositivoCorrectamente() {
            // Arrange
            DeudaTrabajador deudaEntity = new DeudaTrabajador();
            deudaEntity.setId(UUID.randomUUID());
            deudaEntity.setTrabajadorId(trabajadorId);
            deudaEntity.setImporte(100.0);

            when(trabajadorService.findById(trabajadorId)).thenReturn(trabajadorDto);
            when(repositoryQuery.findByTrabajadorId(trabajadorId)).thenReturn(Optional.of(deudaEntity));
            when(repositoryCommand.save(any(DeudaTrabajador.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(detalleService.create(any())).thenReturn(UUID.randomUUID());

            // Act
            MovimientoDeudaResult result = deudaTrabajadorService.registrarAjuste(
                    trabajadorId,
                    25.0,
                    "Ajuste por error de facturacion"
            );

            // Assert
            assertNotNull(result);
            assertEquals(100.0, result.getSaldoAnterior());
            assertEquals(125.0, result.getSaldoNuevo());
        }

        @Test
        @DisplayName("RN-07: Debe registrar ajuste negativo correctamente")
        void debeRegistrarAjusteNegativoCorrectamente() {
            // Arrange
            DeudaTrabajador deudaEntity = new DeudaTrabajador();
            deudaEntity.setId(UUID.randomUUID());
            deudaEntity.setTrabajadorId(trabajadorId);
            deudaEntity.setImporte(100.0);

            when(trabajadorService.findById(trabajadorId)).thenReturn(trabajadorDto);
            when(repositoryQuery.findByTrabajadorId(trabajadorId)).thenReturn(Optional.of(deudaEntity));
            when(repositoryCommand.save(any(DeudaTrabajador.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(detalleService.create(any())).thenReturn(UUID.randomUUID());

            // Act
            MovimientoDeudaResult result = deudaTrabajadorService.registrarAjuste(
                    trabajadorId,
                    -20.0,
                    "Ajuste por devolucion"
            );

            // Assert
            assertNotNull(result);
            assertEquals(100.0, result.getSaldoAnterior());
            assertEquals(80.0, result.getSaldoNuevo());
        }

        @Test
        @DisplayName("RN-02: Debe rechazar ajuste que deja deuda negativa")
        void debeRechazarAjusteQueDejaDeudaNegativa() {
            // Arrange
            DeudaTrabajador deudaEntity = new DeudaTrabajador();
            deudaEntity.setId(UUID.randomUUID());
            deudaEntity.setTrabajadorId(trabajadorId);
            deudaEntity.setImporte(50.0);

            when(trabajadorService.findById(trabajadorId)).thenReturn(trabajadorDto);
            when(repositoryQuery.findByTrabajadorId(trabajadorId)).thenReturn(Optional.of(deudaEntity));

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> deudaTrabajadorService.registrarAjuste(
                            trabajadorId,
                            -100.0,
                            "Ajuste excesivo"
                    )
            );
        }
    }

    @Nested
    @DisplayName("registrarCargaInicial() Tests")
    class RegistrarCargaInicialTests {

        @Test
        @DisplayName("RN-08: Debe crear carga inicial para trabajador sin deuda")
        void debeCrearCargaInicialParaTrabajadorSinDeuda() {
            // Arrange
            when(repositoryQuery.findByTrabajadorId(trabajadorId)).thenReturn(Optional.empty());
            when(trabajadorService.findById(trabajadorId)).thenReturn(trabajadorDto);
            when(repositoryCommand.save(any(DeudaTrabajador.class))).thenAnswer(invocation -> {
                DeudaTrabajador saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                return saved;
            });
            when(detalleService.create(any())).thenReturn(UUID.randomUUID());

            // Act
            MovimientoDeudaResult result = deudaTrabajadorService.registrarCargaInicial(
                    trabajadorId,
                    500.0,
                    "Saldo inicial por migracion"
            );

            // Assert
            assertNotNull(result);
            assertEquals(0.0, result.getSaldoAnterior());
            assertEquals(500.0, result.getSaldoNuevo());
        }

        @Test
        @DisplayName("RN-08: Debe rechazar carga inicial con monto negativo")
        void debeRechazarCargaInicialConMontoNegativo() {
            // Arrange
            when(trabajadorService.findById(trabajadorId)).thenReturn(trabajadorDto);

            // Act & Assert
            assertThrows(
                    BusinessNotFoundException.class,
                    () -> deudaTrabajadorService.registrarCargaInicial(
                            trabajadorId,
                            -100.0,
                            "Carga negativa"
                    )
            );
        }

        @Test
        @DisplayName("RN-08: Debe agregar a carga existente")
        void debeAgregarACargaExistente() {
            // Arrange
            DeudaTrabajador deudaExistente = new DeudaTrabajador();
            deudaExistente.setId(UUID.randomUUID());
            deudaExistente.setTrabajadorId(trabajadorId);
            deudaExistente.setImporte(100.0);

            when(repositoryQuery.findByTrabajadorId(trabajadorId)).thenReturn(Optional.of(deudaExistente));
            when(trabajadorService.findById(trabajadorId)).thenReturn(trabajadorDto);
            when(repositoryCommand.save(any(DeudaTrabajador.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(detalleService.create(any())).thenReturn(UUID.randomUUID());

            // Act
            MovimientoDeudaResult result = deudaTrabajadorService.registrarCargaInicial(
                    trabajadorId,
                    500.0,
                    "Segunda carga inicial"
            );

            // Assert
            assertNotNull(result);
            assertEquals(100.0, result.getSaldoAnterior());
            assertEquals(600.0, result.getSaldoNuevo());
        }
    }

    @Nested
    @DisplayName("findByTrabajadorId() Tests")
    class FindByTrabajadorIdTests {

        @Test
        @DisplayName("Debe retornar deuda del trabajador")
        void debeRetornarDeudaDelTrabajador() {
            // Arrange
            DeudaTrabajador deudaEntity = new DeudaTrabajador();
            deudaEntity.setId(UUID.randomUUID());
            deudaEntity.setTrabajadorId(trabajadorId);
            deudaEntity.setImporte(150.0);

            when(repositoryQuery.findByTrabajadorId(trabajadorId)).thenReturn(Optional.of(deudaEntity));

            // Act
            DeudaTrabajadorDto result = deudaTrabajadorService.findByTrabajadorId(trabajadorId);

            // Assert
            assertNotNull(result);
            assertEquals(150.0, result.getImporte());
        }

        @Test
        @DisplayName("Debe retornar null si trabajador no tiene deuda")
        void debeRetornarNullSiNoTieneDeuda() {
            // Arrange
            when(repositoryQuery.findByTrabajadorId(trabajadorId)).thenReturn(Optional.empty());

            // Act
            DeudaTrabajadorDto result = deudaTrabajadorService.findByTrabajadorId(trabajadorId);

            // Assert
            assertNull(result);
        }
    }
}
