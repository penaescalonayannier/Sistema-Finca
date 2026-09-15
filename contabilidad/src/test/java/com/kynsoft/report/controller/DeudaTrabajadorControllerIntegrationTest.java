package com.kynsoft.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsoft.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.domain.services.IDeudaTrabajadorDetalleService;
import com.kynsoft.report.domain.services.IDeudaTrabajadorService;
import com.kynsoft.report.domain.services.IPagoDeudaService;
import com.kynsoft.report.infrastructure.services.ReciboPdfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeudaTrabajadorController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("DeudaTrabajadorController Integration Tests")
class DeudaTrabajadorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IMediator mediator;

    @MockBean
    private IDeudaTrabajadorService deudaService;

    @MockBean
    private IDeudaTrabajadorDetalleService detalleService;

    @MockBean
    private IPagoDeudaService pagoDeudaService;

    @MockBean
    private ReciboPdfService reciboPdfService;

    private UUID trabajadorId;
    private UUID deudaId;

    @BeforeEach
    void setUp() {
        trabajadorId = UUID.randomUUID();
        deudaId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("POST /api/deuda-trabajador endpoint")
    class CreateEndpointTests {

        @Test
        @DisplayName("Debe aceptar request de creación")
        void debeAceptarRequestDeCreacion() throws Exception {
            // Arrange
            String requestJson = String.format("""
                    {
                        "trabajadorId": "%s",
                        "importe": 100.0
                    }
                    """, trabajadorId);

            // Act & Assert
            mockMvc.perform(post("/api/deuda-trabajador")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().is2xxSuccessful());
        }
    }

    @Nested
    @DisplayName("GET /api/deuda-trabajador/{trabajadorId} endpoint")
    class FindByTrabajadorIdEndpointTests {

        @Test
        @DisplayName("Debe aceptar request de obtener deuda por trabajador")
        void debeAceptarRequestDeObtenerDeuda() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/api/deuda-trabajador/{trabajadorId}", trabajadorId))
                    .andExpect(status().is2xxSuccessful());
        }
    }

    @Nested
    @DisplayName("PUT /api/deuda-trabajador/{id} endpoint")
    class UpdateEndpointTests {

        @Test
        @DisplayName("Debe aceptar request de actualización")
        void debeAceptarRequestDeActualizacion() throws Exception {
            // Arrange
            String requestJson = """
                    {
                        "importe": 150.0
                    }
                    """;

            // Act & Assert
            mockMvc.perform(put("/api/deuda-trabajador/{id}", deudaId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().is2xxSuccessful());
        }
    }

    @Nested
    @DisplayName("DELETE /api/deuda-trabajador/{id} endpoint")
    class DeleteEndpointTests {

        @Test
        @DisplayName("Debe aceptar request de eliminación")
        void debeAceptarRequestDeEliminacion() throws Exception {
            // Act & Assert
            mockMvc.perform(delete("/api/deuda-trabajador/{id}", deudaId))
                    .andExpect(status().is2xxSuccessful());
        }
    }
}
