package com.kynsoft.report.infrastructure.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.kynsoft.report.domain.dto.ConfiguracionEmpresaDto;
import com.kynsoft.report.domain.dto.DestinoSalida;
import com.kynsoft.report.domain.dto.ItemSalidaDto;
import com.kynsoft.report.domain.dto.SalidaDto;
import com.kynsoft.report.domain.dto.TipoSalida;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FacturaPdfServiceTest {

    @Test
    void debeGenerarValeSc208ConTrazabilidadYTotales() throws Exception {
        FacturaPdfService service = new FacturaPdfService();
        SalidaDto salida = SalidaDto.builder()
                .tipo(TipoSalida.VALE)
                .numero("VS-2026-001")
                .fecha(LocalDateTime.of(2026, 9, 16, 10, 30))
                .destino(DestinoSalida.COMEDOR)
                .fincaCode("F-01")
                .fincaName("Finca Norte")
                .observaciones("Orden OP-18")
                .items(List.of(ItemSalidaDto.builder()
                        .productoCode("YUCA")
                        .productoName("Yuca fresca")
                        .unidadMedida("kg")
                        .cantidad(1.5)
                        .precio(25d)
                        .build()))
                .build();
        ConfiguracionEmpresaDto empresa = ConfiguracionEmpresaDto.builder()
                .nombre("Empresa Agrícola")
                .codigo("EA-01")
                .build();

        byte[] pdf = service.generarFactura(salida, empresa);

        assertTrue(pdf.length > 100);
        try (PdfDocument documento = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdf)))) {
            String contenido = PdfTextExtractor.getTextFromPage(documento.getPage(1));
            assertTrue(contenido.contains("VALE DE ENTREGA O DEVOLUCIÓN"));
            assertTrue(contenido.contains("VS-2026-001"));
            assertTrue(contenido.contains("Saldo actual"));
            assertTrue(contenido.contains("1.5"));
            assertTrue(contenido.contains("No asociado"));
        }
    }
}
