package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.dto.ConfiguracionEmpresaDto;
import com.kynsoft.report.domain.dto.KardexDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.dto.UnidadMedida;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TarjetaEstibaPdfServiceTest {

    @Test
    void generaPdfSc214ConMovimientoYSaldoDePeriodo() throws Exception {
        UUID fincaProductoId = UUID.randomUUID();
        KardexDto kardex = KardexDto.builder()
                .producto(KardexDto.ProductoInfoDto.builder()
                        .fincaProductoId(fincaProductoId).productoCode("YUCA").productoName("Yuca").build())
                .stockInicial(12.5d).totalEntradas(5d).totalSalidas(3d).stockFinal(14.5d)
                .movimientos(List.of(KardexDto.MovimientoKardexDto.builder()
                        .fecha(LocalDateTime.of(2026, 1, 5, 8, 30))
                        .tipoMovimiento(TipoMovimientoStock.ENTRADA_PRODUCCION)
                        .entrada(5d).salida(0d).saldo(17.5d)
                        .referenciaTabla("produccion_terminada").referenciaId(UUID.randomUUID())
                        .descripcion("Producción terminada").build()))
                .build();
        AlmacenFincaProductoDto almacenProducto = AlmacenFincaProductoDto.builder()
                .almacenNombre("Almacén Central").almacenInventario("ALM-01")
                .productoCode("YUCA").productoName("Yuca").unidadMedida(UnidadMedida.KG).build();
        ConfiguracionEmpresaDto empresa = ConfiguracionEmpresaDto.builder().nombre("Finca Demo").codigo("F-01").build();

        TarjetaEstibaPdfService service = new TarjetaEstibaPdfService(null, null, null);
        byte[] pdf = service.generar(kardex, almacenProducto, empresa,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        assertTrue(pdf.length > 100);
        assertTrue(new String(pdf, 0, 4).startsWith("%PDF"));
    }
}
