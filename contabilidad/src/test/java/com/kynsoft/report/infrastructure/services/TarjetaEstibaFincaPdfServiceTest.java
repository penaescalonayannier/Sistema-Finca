package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.ConfiguracionEmpresaDto;
import com.kynsoft.report.domain.dto.KardexDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TarjetaEstibaFincaPdfServiceTest {

    @Test
    void generaTarjetaDeFincaConAlmacenOriginador() throws Exception {
        KardexDto kardex = KardexDto.builder()
                .stockInicial(10d).totalEntradas(1.5d).totalSalidas(2d).stockFinal(9.5d)
                .movimientos(List.of(KardexDto.MovimientoKardexDto.builder()
                        .fecha(LocalDateTime.of(2026, 2, 2, 8, 0))
                        .tipoMovimiento(TipoMovimientoStock.ENTRADA_PRODUCCION)
                        .entrada(1.5d).salida(0d).saldo(11.5d)
                        .almacenId(UUID.randomUUID()).almacenNombre("Almacén Norte")
                        .referenciaTabla("produccion_terminada").referenciaId(UUID.randomUUID())
                        .descripcion("Producción").build()))
                .build();
        TarjetaEstibaFincaPdfService service = new TarjetaEstibaFincaPdfService(null, null, null, null);

        byte[] pdf = service.generar(kardex,
                new TarjetaEstibaFincaPdfService.DatosFincaProducto("F-01", "Finca Norte", "YUCA", "Yuca", "KG"),
                ConfiguracionEmpresaDto.builder().nombre("Empresa").build(),
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28));

        assertTrue(pdf.length > 100);
        assertTrue(new String(pdf, 0, 4).startsWith("%PDF"));
    }
}
