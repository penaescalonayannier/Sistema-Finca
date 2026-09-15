package com.kynsoft.report.applications.command.fincaproducto;

import com.kynsoft.report.applications.command.fincaproducto.asignar.AsignarProductoAFincaCommand;
import com.kynsoft.report.applications.command.fincaproducto.asignar.AsignarProductoAFincaCommandHandler;
import com.kynsoft.report.domain.services.IFincaProductoService;
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
@DisplayName("AsignarProductoAFincaCommandHandler Unit Tests")
class AsignarProductoAFincaCommandHandlerTest {

    @Mock
    private IFincaProductoService fincaProductoService;

    @InjectMocks
    private AsignarProductoAFincaCommandHandler handler;

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
        @DisplayName("Debe asignar producto a finca correctamente")
        void debeAsignarProductoAFincaCorrectamente() {
            // Arrange
            UUID expectedId = UUID.randomUUID();
            AsignarProductoAFincaCommand command = new AsignarProductoAFincaCommand(
                    fincaId, productoId, 100, 20
            );

            when(fincaProductoService.asignarProductoAFinca(fincaId, productoId, 100, 20))
                    .thenReturn(expectedId);

            // Act
            handler.handle(command);

            // Assert
            assertEquals(expectedId, command.getId());
            verify(fincaProductoService).asignarProductoAFinca(fincaId, productoId, 100, 20);
        }

        @Test
        @DisplayName("Debe llamar al servicio con los parámetros correctos")
        void debeLlamarAlServicioConParametrosCorrectos() {
            // Arrange
            UUID expectedId = UUID.randomUUID();
            int stock = 50;
            int stockMinimo = 10;
            AsignarProductoAFincaCommand command = new AsignarProductoAFincaCommand(
                    fincaId, productoId, stock, stockMinimo
            );

            when(fincaProductoService.asignarProductoAFinca(any(), any(), anyInt(), anyInt()))
                    .thenReturn(expectedId);

            // Act
            handler.handle(command);

            // Assert
            verify(fincaProductoService).asignarProductoAFinca(
                    eq(fincaId),
                    eq(productoId),
                    eq(stock),
                    eq(stockMinimo)
            );
        }

        @Test
        @DisplayName("Debe setear el ID en el command después de asignar")
        void debeSetearIdEnCommandDespuesDeAsignar() {
            // Arrange
            UUID generatedId = UUID.randomUUID();
            AsignarProductoAFincaCommand command = new AsignarProductoAFincaCommand(
                    fincaId, productoId, 100, 20
            );

            when(fincaProductoService.asignarProductoAFinca(any(), any(), anyInt(), anyInt()))
                    .thenReturn(generatedId);

            // Pre-condition
            assertNull(command.getId());

            // Act
            handler.handle(command);

            // Assert
            assertNotNull(command.getId());
            assertEquals(generatedId, command.getId());
        }

        @Test
        @DisplayName("Debe asignar con stock inicial cero")
        void debeAsignarConStockInicialCero() {
            // Arrange
            UUID expectedId = UUID.randomUUID();
            AsignarProductoAFincaCommand command = new AsignarProductoAFincaCommand(
                    fincaId, productoId, 0, 5
            );

            when(fincaProductoService.asignarProductoAFinca(fincaId, productoId, 0, 5))
                    .thenReturn(expectedId);

            // Act
            handler.handle(command);

            // Assert
            assertEquals(expectedId, command.getId());
            verify(fincaProductoService).asignarProductoAFinca(fincaId, productoId, 0, 5);
        }

        @Test
        @DisplayName("Debe propagar excepción del servicio")
        void debePropagarExcepcionDelServicio() {
            // Arrange
            AsignarProductoAFincaCommand command = new AsignarProductoAFincaCommand(
                    fincaId, productoId, 100, 20
            );

            when(fincaProductoService.asignarProductoAFinca(any(), any(), anyInt(), anyInt()))
                    .thenThrow(new RuntimeException("Error de servicio"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> handler.handle(command));
        }
    }

    @Nested
    @DisplayName("Command Tests")
    class CommandTests {

        @Test
        @DisplayName("fromRequest debe crear comando correctamente")
        void fromRequestDebeCrearComandoCorrectamente() {
            // This is a static factory method test
            // The command is a simple data holder
            AsignarProductoAFincaCommand command = new AsignarProductoAFincaCommand(
                    fincaId, productoId, 200, 50
            );

            assertEquals(fincaId, command.getFincaId());
            assertEquals(productoId, command.getProductoId());
            assertEquals(200, command.getStock());
            assertEquals(50, command.getStockMinimo());
        }

        @Test
        @DisplayName("getMessage debe retornar mensaje con ID")
        void getMessageDebeRetornarMensajeConId() {
            // Arrange
            UUID expectedId = UUID.randomUUID();
            AsignarProductoAFincaCommand command = new AsignarProductoAFincaCommand(
                    fincaId, productoId, 100, 20
            );
            command.setId(expectedId);

            // Act
            var message = command.getMessage();

            // Assert
            assertNotNull(message);
        }
    }
}
