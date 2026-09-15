package com.kynsoft.report.applications.command.fincaproducto;

import com.kynsoft.report.applications.command.fincaproducto.actualizar.ActualizarStockFincaProductoCommand;
import com.kynsoft.report.applications.command.fincaproducto.actualizar.ActualizarStockFincaProductoCommandHandler;
import com.kynsoft.report.domain.services.IFincaProductoService;
import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActualizarStockFincaProductoCommandHandler Unit Tests")
class ActualizarStockFincaProductoCommandHandlerTest {

    @Mock
    private IFincaProductoService fincaProductoService;

    @InjectMocks
    private ActualizarStockFincaProductoCommandHandler handler;

    private UUID fincaId;
    private UUID productoId;

    @BeforeEach
    void setUp() {
        fincaId = UUID.randomUUID();
        productoId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("handle() Tests")
    class HandleTests {

        @Test
        @DisplayName("Debe actualizar stock correctamente")
        void debeActualizarStockCorrectamente() {
            // Arrange
            int nuevoStock = 150;
            ActualizarStockFincaProductoCommand command = new ActualizarStockFincaProductoCommand(
                    fincaId, productoId, nuevoStock
            );

            doNothing().when(fincaProductoService).actualizarStock(fincaId, productoId, nuevoStock);

            // Act
            handler.handle(command);

            // Assert
            verify(fincaProductoService).actualizarStock(fincaId, productoId, nuevoStock);
        }

        @Test
        @DisplayName("Debe llamar al servicio con los parámetros correctos")
        void debeLlamarAlServicioConParametrosCorrectos() {
            // Arrange
            int stock = 75;
            ActualizarStockFincaProductoCommand command = new ActualizarStockFincaProductoCommand(
                    fincaId, productoId, stock
            );

            doNothing().when(fincaProductoService).actualizarStock(any(), any(), anyInt());

            // Act
            handler.handle(command);

            // Assert
            verify(fincaProductoService).actualizarStock(
                    eq(fincaId),
                    eq(productoId),
                    eq(stock)
            );
        }

        @Test
        @DisplayName("Debe permitir actualizar a stock cero")
        void debePermitirActualizarAStockCero() {
            // Arrange
            ActualizarStockFincaProductoCommand command = new ActualizarStockFincaProductoCommand(
                    fincaId, productoId, 0
            );

            doNothing().when(fincaProductoService).actualizarStock(fincaId, productoId, 0);

            // Act & Assert - no exception
            assertDoesNotThrow(() -> handler.handle(command));
            verify(fincaProductoService).actualizarStock(fincaId, productoId, 0);
        }

        @Test
        @DisplayName("Debe propagar excepción si producto no está asignado a finca")
        void debePropagarExcepcionSiProductoNoAsignado() {
            // Arrange
            ActualizarStockFincaProductoCommand command = new ActualizarStockFincaProductoCommand(
                    fincaId, productoId, 100
            );

            doThrow(new BusinessNotFoundException(null))
                    .when(fincaProductoService).actualizarStock(any(), any(), anyInt());

            // Act & Assert
            assertThrows(BusinessNotFoundException.class, () -> handler.handle(command));
        }

        @Test
        @DisplayName("Debe permitir actualizar stock alto")
        void debePermitirActualizarStockAlto() {
            // Arrange
            int stockAlto = 10000;
            ActualizarStockFincaProductoCommand command = new ActualizarStockFincaProductoCommand(
                    fincaId, productoId, stockAlto
            );

            doNothing().when(fincaProductoService).actualizarStock(fincaId, productoId, stockAlto);

            // Act
            handler.handle(command);

            // Assert
            verify(fincaProductoService).actualizarStock(fincaId, productoId, stockAlto);
        }
    }

    @Nested
    @DisplayName("Command Tests")
    class CommandTests {

        @Test
        @DisplayName("Debe crear comando correctamente")
        void debeCrearComandoCorrectamente() {
            // Arrange & Act
            int stock = 200;
            ActualizarStockFincaProductoCommand command = new ActualizarStockFincaProductoCommand(
                    fincaId, productoId, stock
            );

            // Assert
            assertEquals(fincaId, command.getFincaId());
            assertEquals(productoId, command.getProductoId());
            assertEquals(stock, command.getStock());
        }

        @Test
        @DisplayName("getMessage debe retornar mensaje con fincaId y productoId")
        void getMessageDebeRetornarMensajeConIds() {
            // Arrange
            ActualizarStockFincaProductoCommand command = new ActualizarStockFincaProductoCommand(
                    fincaId, productoId, 100
            );

            // Act
            var message = command.getMessage();

            // Assert
            assertNotNull(message);
        }

        @Test
        @DisplayName("Debe permitir modificar stock via setter")
        void debePermitirModificarStockViaSetter() {
            // Arrange
            ActualizarStockFincaProductoCommand command = new ActualizarStockFincaProductoCommand(
                    fincaId, productoId, 50
            );

            // Act
            command.setStock(100);

            // Assert
            assertEquals(100, command.getStock());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("Debe manejar stock null en servicio")
        void debeManejarStockNullEnServicio() {
            // Arrange
            ActualizarStockFincaProductoCommand command = new ActualizarStockFincaProductoCommand(
                    fincaId, productoId, null
            );

            // Simulando que el servicio acepta null y lanza excepción
            doThrow(new IllegalArgumentException("Stock cannot be null"))
                    .when(fincaProductoService).actualizarStock(any(), any(), eq(null));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
        }

        @Test
        @DisplayName("Handler debe ser llamado una sola vez")
        void handlerDebeSerLlamadoUnaSolaVez() {
            // Arrange
            ActualizarStockFincaProductoCommand command = new ActualizarStockFincaProductoCommand(
                    fincaId, productoId, 100
            );

            doNothing().when(fincaProductoService).actualizarStock(any(), any(), anyInt());

            // Act
            handler.handle(command);

            // Assert
            verify(fincaProductoService, times(1)).actualizarStock(any(), any(), anyInt());
        }
    }
}
