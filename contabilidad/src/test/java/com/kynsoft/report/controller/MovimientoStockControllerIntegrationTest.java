package com.kynsoft.report.controller;

import com.kynsoft.report.domain.services.IMovimientoStockService;
import com.kynsoft.report.infrastructure.services.MovimientoStockPdfService;
import com.kynsoft.report.infrastructure.services.TarjetaEstibaPdfService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovimientoStockController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovimientoStockControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IMovimientoStockService movimientoStockService;

    @MockBean
    private MovimientoStockPdfService movimientoStockPdfService;

    @MockBean
    private TarjetaEstibaPdfService tarjetaEstibaPdfService;

    @Test
    void descargaTarjetaEstibaSc214PorAlmacenProductoYPeriodo() throws Exception {
        UUID almacenId = UUID.randomUUID();
        UUID fincaProductoId = UUID.randomUUID();
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fin = LocalDate.of(2026, 1, 31);
        when(tarjetaEstibaPdfService.generar(fincaProductoId, almacenId, inicio, fin))
                .thenReturn("%PDF-1.7".getBytes());

        mockMvc.perform(get("/api/movimiento-stock/tarjeta-estiba/pdf")
                        .param("almacenId", almacenId.toString())
                        .param("fincaProductoId", fincaProductoId.toString())
                        .param("fechaInicio", inicio.toString())
                        .param("fechaFin", fin.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("SC-2-14_tarjeta_estiba_")));
    }
}
