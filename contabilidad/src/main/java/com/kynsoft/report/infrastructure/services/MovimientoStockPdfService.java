package com.kynsoft.report.infrastructure.services;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.kynsoft.report.domain.dto.reportes.ReporteMovimientosConsolidadoDto;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Documento PDF de consulta para el reporte de movimientos y salidas por destino. */
@Service
public class MovimientoStockPdfService {

    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(33, 95, 154);
    private static final DeviceRgb LIGHT_BLUE = new DeviceRgb(230, 240, 250);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generar(ReporteMovimientosConsolidadoDto reporte) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document(new PdfDocument(new PdfWriter(output)), PageSize.A4.rotate());
        document.setMargins(26, 24, 26, 24);

        PdfFont bold = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont normal = PdfFontFactory.createFont("Helvetica");
        String periodo = DATE_FORMAT.format(reporte.getFechaInicio()) + " al " + DATE_FORMAT.format(reporte.getFechaFin());

        document.add(new Paragraph("REPORTE CONSOLIDADO DE MOVIMIENTOS POR DESTINO")
                .setFont(bold).setFontSize(16).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Período: " + periodo)
                .setFont(normal).setFontSize(10).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Documento informativo. No modifica inventario ni contabilidad.")
                .setFont(normal).setFontSize(8).setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER).setMarginBottom(12));

        agregarResumen(document, reporte, bold, normal);
        agregarSalidasPorDestino(document, reporte, bold, normal);
        agregarMatrizPorProducto(document, reporte, bold, normal);

        document.close();
        return output.toByteArray();
    }

    private void agregarResumen(Document document, ReporteMovimientosConsolidadoDto reporte,
                                PdfFont bold, PdfFont normal) {
        double entradas = valor(reporte.getTotalEntradas());
        double salidas = valor(reporte.getTotalSalidas());
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1})).useAllAvailableWidth();
        table.addCell(celdaResumen("Total entradas", String.valueOf(entradas), bold, normal));
        table.addCell(celdaResumen("Total salidas", String.valueOf(salidas), bold, normal));
        table.addCell(celdaResumen("Balance", String.valueOf(entradas - salidas), bold, normal));
        document.add(table.setMarginBottom(14));
    }

    private void agregarSalidasPorDestino(Document document, ReporteMovimientosConsolidadoDto reporte,
                                          PdfFont bold, PdfFont normal) {
        document.add(new Paragraph("Resumen de salidas por destino").setFont(bold).setFontSize(12));
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1.4f, 1.6f, 1.1f})).useAllAvailableWidth();
        encabezado(table, "Destino", bold);
        encabezado(table, "Cantidad", bold);
        encabezado(table, "Valor total", bold);
        encabezado(table, "% salidas", bold);

        double importeTotal = reporte.getSalidasPorDestino() == null ? 0d : reporte.getSalidasPorDestino().stream()
                .mapToDouble(item -> item.getValorTotal() != null ? item.getValorTotal() : 0d).sum();
        for (ReporteMovimientosConsolidadoDto.SalidaPorDestino salida : lista(reporte.getSalidasPorDestino())) {
            double importe = salida.getValorTotal() != null ? salida.getValorTotal() : 0d;
            celda(table, texto(salida.getDestinoNombre()), normal, TextAlignment.LEFT);
            celda(table, String.valueOf(valor(salida.getCantidadTotal())), normal, TextAlignment.RIGHT);
            celda(table, formatoMoneda(importe), normal, TextAlignment.RIGHT);
            celda(table, String.format(Locale.US, "%.1f%%", importeTotal == 0d ? 0d : importe * 100d / importeTotal), normal, TextAlignment.RIGHT);
        }
        if (lista(reporte.getSalidasPorDestino()).isEmpty()) {
            Cell empty = new Cell(1, 4).add(new Paragraph("No existen salidas activas en el período seleccionado.").setFont(normal));
            table.addCell(empty);
        }
        document.add(table.setMarginBottom(14));
    }

    private void agregarMatrizPorProducto(Document document, ReporteMovimientosConsolidadoDto reporte,
                                          PdfFont bold, PdfFont normal) {
        document.add(new Paragraph("Movimientos por producto y destino").setFont(bold).setFontSize(12));
        List<String> destinos = lista(reporte.getSalidasPorDestino()).stream()
                .map(item -> item.getDestino() != null ? item.getDestino().name() : "OTROS")
                .sorted().toList();
        List<Float> widths = new ArrayList<>(List.of(2.3f, 0.65f, 0.9f, 0.9f));
        for (int ignored = 0; ignored < destinos.size(); ignored++) widths.add(0.9f);
        float[] columnWidths = new float[widths.size()];
        for (int index = 0; index < widths.size(); index++) columnWidths[index] = widths.get(index);
        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth().setFontSize(7);
        encabezado(table, "Producto", bold);
        encabezado(table, "UM", bold);
        encabezado(table, "Entradas", bold);
        encabezado(table, "Salidas", bold);
        for (String destino : destinos) encabezado(table, destino.replace('_', ' '), bold);

        Map<String, ProductoResumen> productos = productos(reporte, destinos);
        for (ProductoResumen producto : productos.values()) {
            celda(table, producto.codigo + (producto.nombre.isBlank() ? "" : " - " + producto.nombre), normal, TextAlignment.LEFT);
            celda(table, producto.unidad.isBlank() ? "-" : producto.unidad, normal, TextAlignment.CENTER);
            celda(table, String.valueOf(producto.entradas), normal, TextAlignment.RIGHT);
            celda(table, String.valueOf(producto.salidas), normal, TextAlignment.RIGHT);
            for (String destino : destinos) celda(table, String.valueOf(producto.porDestino.getOrDefault(destino, 0.0)), normal, TextAlignment.RIGHT);
        }
        if (productos.isEmpty()) {
            int columnas = 4 + destinos.size();
            table.addCell(new Cell(1, columnas).add(new Paragraph("No existen movimientos en el período seleccionado.").setFont(normal)));
        }
        document.add(table);
    }

    private Map<String, ProductoResumen> productos(ReporteMovimientosConsolidadoDto reporte, List<String> destinos) {
        Map<String, ProductoResumen> resultado = new LinkedHashMap<>();
        for (ReporteMovimientosConsolidadoDto.EntradaPorProducto entrada : lista(reporte.getEntradasPorProducto())) {
            ProductoResumen producto = resultado.computeIfAbsent(texto(entrada.getProductoCode()),
                    ignored -> new ProductoResumen(texto(entrada.getProductoCode()), texto(entrada.getProductoName()), texto(entrada.getUnidadMedida())));
            producto.entradas += valor(entrada.getCantidadTotal());
        }
        for (ReporteMovimientosConsolidadoDto.SalidaPorDestino salida : lista(reporte.getSalidasPorDestino())) {
            String destino = salida.getDestino() != null ? salida.getDestino().name() : "OTROS";
            for (ReporteMovimientosConsolidadoDto.SalidaProductoDetalle detalle : lista(salida.getProductos())) {
                ProductoResumen producto = resultado.computeIfAbsent(texto(detalle.getProductoCode()),
                        ignored -> new ProductoResumen(texto(detalle.getProductoCode()), texto(detalle.getProductoName()), ""));
                double cantidad = valor(detalle.getCantidad());
                producto.salidas += cantidad;
                producto.porDestino.merge(destino, cantidad, Double::sum);
            }
        }
        return resultado.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .collect(LinkedHashMap::new, (map, item) -> map.put(item.getKey(), item.getValue()), LinkedHashMap::putAll);
    }

    private Cell celdaResumen(String titulo, String cantidad, PdfFont bold, PdfFont normal) {
        return new Cell().setBackgroundColor(LIGHT_BLUE).setTextAlignment(TextAlignment.CENTER)
                .add(new Paragraph(titulo).setFont(bold).setFontSize(8))
                .add(new Paragraph(cantidad).setFont(bold).setFontSize(14));
    }

    private void encabezado(Table table, String contenido, PdfFont bold) {
        table.addHeaderCell(new Cell().setBackgroundColor(HEADER_COLOR).setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER).add(new Paragraph(contenido).setFont(bold).setFontSize(7)));
    }

    private void celda(Table table, String contenido, PdfFont font, TextAlignment alineacion) {
        table.addCell(new Cell().setTextAlignment(alineacion).add(new Paragraph(contenido).setFont(font)));
    }

    private double valor(Double number) {
        return number == null ? 0.0 : number;
    }

    private String texto(String value) {
        return value == null ? "" : value;
    }

    private String formatoMoneda(double value) {
        return String.format(Locale.US, "$ %,.2f", value);
    }

    private <T> List<T> lista(List<T> values) {
        return values == null ? List.of() : values;
    }

    private static class ProductoResumen {
        private final String codigo;
        private final String nombre;
        private final String unidad;
        private double entradas;
        private double salidas;
        private final Map<String, Double> porDestino = new LinkedHashMap<>();

        private ProductoResumen(String codigo, String nombre, String unidad) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.unidad = unidad;
        }
    }
}
