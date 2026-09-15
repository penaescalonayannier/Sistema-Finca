package com.kynsoft.report.infrastructure.services;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.kynsoft.report.domain.dto.ConfiguracionEmpresaDto;
import com.kynsoft.report.domain.dto.DestinoSalida;
import com.kynsoft.report.domain.dto.ItemSalidaDto;
import com.kynsoft.report.domain.dto.SalidaDto;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Servicio para generación de documentos PDF según modelos oficiales cubanos.
 * Implementa los formatos SC-2-08 (Vale de Entrega o Devolución) y SC-2-12 (Factura)
 * según Resolución 11/2007 y Resolución 55/2021 del MFP.
 */
@Service
public class FacturaPdfService {

    private static final DeviceRgb HEADER_BG = new DeviceRgb(66, 139, 202);
    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(245, 245, 245);
    private static final DeviceRgb BORDER_COLOR = new DeviceRgb(200, 200, 200);
    private static final DeviceRgb TEXT_DARK = new DeviceRgb(51, 51, 51);

    /**
     * Genera factura/vale usando ConfiguracionEmpresaDto.
     * Formato según modelos oficiales SC-2-08 (Vale) o SC-2-12 (Factura).
     */
    public byte[] generarFactura(SalidaDto salida, ConfiguracionEmpresaDto empresa) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.LETTER);
        document.setMargins(30, 30, 30, 30);

        PdfFont fontBold = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont fontNormal = PdfFontFactory.createFont("Helvetica");

        // ========== FACTURA/VALE ==========
        generarPaginaFactura(document, salida, empresa, fontBold, fontNormal);

        // El listado de trabajadores solo corresponde a vales destinados a trabajadores.
        if (salida.getDestino() == DestinoSalida.TRABAJADORES) {
            generarPaginaTrabajadores(document, salida, fontBold, fontNormal);
        }

        document.close();
        return baos.toByteArray();
    }

    /**
     * Genera una representación consolidada de vales ya emitidos. Es un documento de consulta e
     * impresión: no crea ni modifica salidas, movimientos de stock, deudas ni asientos contables.
     */
    public byte[] generarValesConsolidados(List<SalidaDto> salidas, LocalDate fecha,
                                            DestinoSalida destino, ConfiguracionEmpresaDto empresa) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(new PdfDocument(new PdfWriter(baos)), PageSize.LETTER);
        document.setMargins(30, 30, 30, 30);

        PdfFont fontBold = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont fontNormal = PdfFontFactory.createFont("Helvetica");
        StringJoiner fincas = new StringJoiner(", ");
        StringJoiner numeros = new StringJoiner(", ");
        Map<String, ResumenProducto> productos = new LinkedHashMap<>();

        for (SalidaDto salida : salidas) {
            fincas.add((salida.getFincaCode() != null ? salida.getFincaCode() + " - " : "")
                    + (salida.getFincaName() != null ? salida.getFincaName() : ""));
            numeros.add(salida.getNumero());
            if (salida.getItems() == null) continue;
            for (ItemSalidaDto item : salida.getItems()) {
                String codigo = productoCode(salida, item);
                String nombre = productoName(salida, item);
                String unidad = unidadMedida(salida, item);
                String key = String.join("|", valor(codigo), valor(nombre), valor(unidad));
                ResumenProducto resumen = productos.computeIfAbsent(key,
                        ignored -> new ResumenProducto(codigo, nombre, unidad));
                int cantidad = item.getCantidad() != null ? item.getCantidad() : 0;
                double precio = item.getPrecio() != null ? item.getPrecio() : 0d;
                resumen.cantidad += cantidad;
                resumen.importe += cantidad * precio;
            }
        }

        document.add(new Paragraph("VALE CONSOLIDADO DE ENTREGA")
                .setFont(fontBold).setFontSize(16).setTextAlignment(TextAlignment.CENTER).setMarginBottom(4));
        document.add(new Paragraph("Documento informativo de impresión; no modifica inventario ni contabilidad.")
                .setFont(fontNormal).setFontSize(8).setTextAlignment(TextAlignment.CENTER)
                .setFontColor(TEXT_DARK).setMarginBottom(10));

        Table info = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginBottom(10);
        Cell empresaCell = new Cell().setBorder(new SolidBorder(BORDER_COLOR, 1)).setPadding(6);
        empresaCell.add(crearLineaInfo("Entidad:", empresa.getNombre(), fontNormal));
        empresaCell.add(crearLineaInfo("Finca:", fincas.toString(), fontNormal));
        info.addCell(empresaCell);
        Cell detalleCell = new Cell().setBorder(new SolidBorder(BORDER_COLOR, 1)).setPadding(6);
        detalleCell.add(crearLineaInfo("Fecha:", fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontNormal));
        detalleCell.add(crearLineaInfo("Destino:", formatDestino(destino.name()), fontNormal));
        detalleCell.add(crearLineaInfo("Vales incluidos:", numeros.toString(), fontNormal));
        info.addCell(detalleCell);
        document.add(info);

        document.add(new Paragraph("Resumen por producto").setFont(fontBold).setFontSize(11).setMarginBottom(4));
        Table resumenTable = new Table(UnitValue.createPercentArray(new float[]{10, 47, 13, 15, 15}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginBottom(14);
        String[] resumenHeaders = {"Código", "Producto", "U/M", "Cantidad", "Importe"};
        for (String header : resumenHeaders) {
            resumenTable.addHeaderCell(celdaEncabezado(header, fontBold));
        }
        double importeTotal = 0d;
        boolean alternar = false;
        for (ResumenProducto producto : productos.values()) {
            Color fondo = alternar ? LIGHT_GRAY : ColorConstants.WHITE;
            alternar = !alternar;
            resumenTable.addCell(crearCeldaTabla(producto.codigo, fontNormal, TextAlignment.CENTER, fondo));
            resumenTable.addCell(crearCeldaTabla(producto.nombre, fontNormal, TextAlignment.LEFT, fondo));
            resumenTable.addCell(crearCeldaTabla(producto.unidadMedida, fontNormal, TextAlignment.CENTER, fondo));
            resumenTable.addCell(crearCeldaTabla(String.valueOf(producto.cantidad), fontNormal, TextAlignment.CENTER, fondo));
            resumenTable.addCell(crearCeldaTabla(String.format("$%.2f", producto.importe), fontNormal, TextAlignment.RIGHT, fondo));
            importeTotal += producto.importe;
        }
        document.add(resumenTable);

        boolean esDestinoTrabajadores = destino == DestinoSalida.TRABAJADORES;
        document.add(new Paragraph(esDestinoTrabajadores ? "Detalle por trabajador" : "Detalle de vales")
                .setFont(fontBold).setFontSize(11).setMarginBottom(4));
        Table detalleTable = new Table(UnitValue.createPercentArray(esDestinoTrabajadores
                ? new float[]{6, 26, 25, 10, 10, 11, 12}
                : new float[]{7, 35, 12, 14, 15, 17}))
                .setWidth(UnitValue.createPercentValue(100));
        String[] detalleHeaders = esDestinoTrabajadores
                ? new String[]{"No.", "Trabajador", "Producto", "U/M", "Cantidad", "Vale", "Importe"}
                : new String[]{"No.", "Producto", "U/M", "Cantidad", "Vale", "Importe"};
        for (String header : detalleHeaders) {
            detalleTable.addHeaderCell(celdaEncabezado(header, fontBold));
        }

        int consecutivo = 1;
        alternar = false;
        for (SalidaDto salida : salidas) {
            if (salida.getItems() == null) continue;
            for (ItemSalidaDto item : salida.getItems()) {
                Color fondo = alternar ? LIGHT_GRAY : ColorConstants.WHITE;
                alternar = !alternar;
                int cantidad = item.getCantidad() != null ? item.getCantidad() : 0;
                double importe = cantidad * (item.getPrecio() != null ? item.getPrecio() : 0d);
                detalleTable.addCell(crearCeldaTabla(String.valueOf(consecutivo++), fontNormal, TextAlignment.CENTER, fondo));
                if (esDestinoTrabajadores) {
                    detalleTable.addCell(crearCeldaTabla(item.getTrabajadorNombre() != null
                            ? item.getTrabajadorNombre() : "Sin trabajador", fontNormal, TextAlignment.LEFT, fondo));
                }
                detalleTable.addCell(crearCeldaTabla(productoName(salida, item), fontNormal, TextAlignment.LEFT, fondo));
                detalleTable.addCell(crearCeldaTabla(unidadMedida(salida, item), fontNormal, TextAlignment.CENTER, fondo));
                detalleTable.addCell(crearCeldaTabla(String.valueOf(cantidad), fontNormal, TextAlignment.CENTER, fondo));
                detalleTable.addCell(crearCeldaTabla(salida.getNumero(), fontNormal, TextAlignment.CENTER, fondo));
                detalleTable.addCell(crearCeldaTabla(String.format("$%.2f", importe), fontNormal, TextAlignment.RIGHT, fondo));
            }
        }
        document.add(detalleTable);
        document.add(new Paragraph("Importe total: $" + String.format("%.2f", importeTotal))
                .setFont(fontBold).setFontSize(9).setTextAlignment(TextAlignment.RIGHT).setMarginTop(6));
        document.close();
        return baos.toByteArray();
    }

    /**
     * Une en un único PDF los vales seleccionados, iniciando una sección independiente por destino.
     * Es una operación de impresión: no crea ni modifica información del sistema.
     */
    public byte[] generarValesConsolidadosPorDestino(List<SalidaDto> salidas, LocalDate fecha,
                                                      ConfiguracionEmpresaDto empresa) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument destinoPdf = new PdfDocument(new PdfWriter(baos));
        PdfMerger merger = new PdfMerger(destinoPdf);
        try {
            Map<DestinoSalida, List<SalidaDto>> porDestino = new LinkedHashMap<>();
            for (DestinoSalida destino : DestinoSalida.values()) {
                List<SalidaDto> grupo = salidas.stream()
                        .filter(salida -> destino.equals(salida.getDestino()))
                        .toList();
                if (!grupo.isEmpty()) {
                    porDestino.put(destino, grupo);
                }
            }

            for (Map.Entry<DestinoSalida, List<SalidaDto>> grupo : porDestino.entrySet()) {
                byte[] pdfGrupo = generarValesConsolidados(grupo.getValue(), fecha, grupo.getKey(), empresa);
                try (PdfDocument origen = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdfGrupo)))) {
                    merger.merge(origen, 1, origen.getNumberOfPages());
                }
            }
        } finally {
            merger.close();
        }
        return baos.toByteArray();
    }

    /**
     * Une los vales seleccionados respetando el formato individual de cada uno.
     * No fuerza saltos de página: iText aprovecha el espacio disponible y solo crea
     * una hoja nueva cuando el siguiente vale ya no cabe.
     */
    public byte[] generarValesIndividuales(List<SalidaDto> salidas,
                                            ConfiguracionEmpresaDto empresa) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(new PdfDocument(new PdfWriter(baos)), PageSize.LETTER);
        document.setMargins(24, 30, 24, 30);
        PdfFont fontBold = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont fontNormal = PdfFontFactory.createFont("Helvetica");

        for (int indice = 0; indice < salidas.size(); indice++) {
            if (indice > 0) {
                document.add(new Paragraph("")
                        .setBorderBottom(new SolidBorder(BORDER_COLOR, 0.7f))
                        .setMarginTop(8)
                        .setMarginBottom(8));
            }
            SalidaDto salida = salidas.get(indice);
            generarPaginaFactura(document, salida, empresa, fontBold, fontNormal);
            if (salida.getDestino() == DestinoSalida.TRABAJADORES) {
                generarPaginaTrabajadores(document, salida, fontBold, fontNormal);
            }
        }
        document.close();
        return baos.toByteArray();
    }

    /**
     * Método legacy para compatibilidad hacia atrás.
     */
    public byte[] generarFactura(SalidaDto salida, String suministradorNombre, String suministradorCodigo,
                                  String suministradorDir, String suministradorMunicipio) throws Exception {
        ConfiguracionEmpresaDto empresa = ConfiguracionEmpresaDto.builder()
                .nombre(suministradorNombre)
                .codigo(suministradorCodigo)
                .direccion(suministradorDir)
                .municipio(suministradorMunicipio)
                .nit("")
                .build();
        return generarFactura(salida, empresa);
    }

    private void generarPaginaFactura(Document document, SalidaDto salida, ConfiguracionEmpresaDto empresa,
                                       PdfFont fontBold, PdfFont fontNormal) {

        boolean esVale = salida.getTipo() == null || salida.getTipo().name().equals("VALE");
        String tipoDoc = esVale ? "VALE DE ENTREGA O DEVOLUCIÓN" : "FACTURA";
        String modeloRef = esVale ? "Modelo SC-2-08" : "Modelo SC-2-12";

        // Referencia al modelo oficial (esquina superior derecha)
        Paragraph modeloParagraph = new Paragraph(modeloRef)
                .setFont(fontNormal)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontColor(TEXT_DARK)
                .setMarginBottom(2);
        document.add(modeloParagraph);

        // Título
        Paragraph titulo = new Paragraph(tipoDoc)
                .setFont(fontBold)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(titulo);

        // Número y fecha
        String fecha = salida.getFecha() != null
                ? salida.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "";
        Paragraph numeroFecha = new Paragraph("No.: " + salida.getNumero() + "     Fecha: " + fecha)
                .setFont(fontNormal)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(numeroFecha);

        // Tabla de cabecera (Suministrador y Receptor)
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(10);

        // Suministrador (datos obligatorios según Res. 55/2021)
        Cell suministradorCell = new Cell()
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setPadding(6);
        suministradorCell.add(new Paragraph("SUMINISTRADOR").setFont(fontBold).setFontSize(9).setMarginBottom(3));
        suministradorCell.add(crearLineaInfo("Nombre:", empresa.getNombre(), fontNormal));
        suministradorCell.add(crearLineaInfo("Código:", empresa.getCodigo(), fontNormal));
        suministradorCell.add(crearLineaInfo("NIT:", empresa.getNit(), fontNormal));
        suministradorCell.add(crearLineaInfo("Dirección:", empresa.getDireccionCompleta(), fontNormal));
        suministradorCell.add(crearLineaInfo("Cuenta:", empresa.getCuentaBancaria(), fontNormal));
        suministradorCell.add(crearLineaInfo("Finca:", salida.getFincaCode() + " " + salida.getFincaName(), fontNormal));
        headerTable.addCell(suministradorCell);

        // Receptor
        Cell receptorCell = new Cell()
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setPadding(6);
        receptorCell.add(new Paragraph("RECEPTOR").setFont(fontBold).setFontSize(9).setMarginBottom(3));
        receptorCell.add(crearLineaInfo("Destino:", salida.getDestino() != null ? formatDestino(salida.getDestino().name()) : "", fontNormal));
        receptorCell.add(crearLineaInfo("Nombre:", "", fontNormal));
        receptorCell.add(crearLineaInfo("Código:", "", fontNormal));
        receptorCell.add(crearLineaInfo("Dirección:", "", fontNormal));
        headerTable.addCell(receptorCell);

        document.add(headerTable);

        // Tabla de items
        Table itemsTable = new Table(UnitValue.createPercentArray(new float[]{6, 10, 32, 12, 13, 13, 14}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(0);

        // Encabezados según modelo oficial
        String[] headers = {"No.", "Código", "Descripción", "U/M", "Cantidad", "Precio", "Importe"};
        for (String header : headers) {
            itemsTable.addHeaderCell(new Cell()
                    .add(new Paragraph(header).setFont(fontBold).setFontSize(8))
                    .setBackgroundColor(HEADER_BG)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(new SolidBorder(BORDER_COLOR, 1))
                    .setPadding(4));
        }

        // Fila del producto
        List<ItemSalidaDto> items = salida.getItems();
        double totalGeneral = 0.0;
        int totalCantidad = 0;

        if (items != null && !items.isEmpty()) {
            for (ItemSalidaDto item : items) {
                double precio = item.getPrecio() != null ? item.getPrecio() : 0.0;
                totalGeneral += item.getCantidad() * precio;
                totalCantidad += item.getCantidad();
            }
        }

        if (items != null && !items.isEmpty()) {
            int consecutivo = 1;
            for (ItemSalidaDto item : items) {
                int cantidad = item.getCantidad() != null ? item.getCantidad() : 0;
                double precio = item.getPrecio() != null ? item.getPrecio() : 0.0;
                itemsTable.addCell(crearCeldaTabla(String.valueOf(consecutivo++), fontNormal, TextAlignment.CENTER, ColorConstants.WHITE));
                itemsTable.addCell(crearCeldaTabla(productoCode(salida, item), fontNormal, TextAlignment.CENTER, ColorConstants.WHITE));
                itemsTable.addCell(crearCeldaTabla(productoName(salida, item), fontNormal, TextAlignment.LEFT, ColorConstants.WHITE));
                itemsTable.addCell(crearCeldaTabla(unidadMedida(salida, item), fontNormal, TextAlignment.CENTER, ColorConstants.WHITE));
                itemsTable.addCell(crearCeldaTabla(String.valueOf(cantidad), fontNormal, TextAlignment.CENTER, ColorConstants.WHITE));
                itemsTable.addCell(crearCeldaTabla(String.format("$%.2f", precio), fontNormal, TextAlignment.RIGHT, ColorConstants.WHITE));
                itemsTable.addCell(crearCeldaTabla(String.format("$%.2f", cantidad * precio), fontNormal, TextAlignment.RIGHT, ColorConstants.WHITE));
            }
        }

        // Fila de TOTAL
        itemsTable.addCell(new Cell(1, 6)
                .add(new Paragraph("TOTAL").setFont(fontBold).setFontSize(8))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setPadding(3));
        itemsTable.addCell(new Cell()
                .add(new Paragraph(String.format("$%.2f", totalGeneral)).setFont(fontBold).setFontSize(8))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setPadding(3));

        document.add(itemsTable);

        // Firmas (según modelo oficial: Entregado, Transportador, Recibido, Contabilizado)
        Table firmasTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(0);

        firmasTable.addCell(crearCeldaFirma("Entregado", fontBold, fontNormal));
        firmasTable.addCell(crearCeldaFirma("Transportador", fontBold, fontNormal));
        firmasTable.addCell(crearCeldaFirma("Recibido", fontBold, fontNormal));
        firmasTable.addCell(crearCeldaFirma("Contabilizado", fontBold, fontNormal));

        document.add(firmasTable);
    }

    private void generarPaginaTrabajadores(Document document, SalidaDto salida, PdfFont fontBold, PdfFont fontNormal) {
        // Separador
        document.add(new Paragraph("\n").setMarginTop(10));

        // Título
        Paragraph titulo = new Paragraph("LISTADO DE TRABAJADORES")
                .setFont(fontBold)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(3);
        document.add(titulo);

        // Subtítulo con info de la salida
        String tipoDoc = (salida.getTipo() == null || salida.getTipo().name().equals("VALE")) ? "Vale" : "Factura";
        String destinoStr = salida.getDestino() != null ? formatDestino(salida.getDestino().name()) : "";
        Paragraph subtitulo = new Paragraph(tipoDoc + ": " + salida.getNumero() + " | Producto: " + salida.getProductoName() + " | Destino: " + destinoStr)
                .setFont(fontNormal)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(6);
        document.add(subtitulo);

        // Tabla de trabajadores
        Table table = new Table(UnitValue.createPercentArray(new float[]{8, 47, 15, 15, 15}))
                .setWidth(UnitValue.createPercentValue(100));

        // Encabezados
        String[] headers = {"No.", "Trabajador", "Cantidad", "Precio", "Total"};
        for (String header : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(header).setFont(fontBold).setFontSize(8).setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(HEADER_BG)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(new SolidBorder(BORDER_COLOR, 1))
                    .setPadding(3));
        }

        // Filas de trabajadores
        List<ItemSalidaDto> items = salida.getItems();
        double totalGeneral = 0.0;
        int totalCantidad = 0;

        if (items != null) {
            int num = 1;
            boolean alternar = false;
            for (ItemSalidaDto item : items) {
                double precio = item.getPrecio() != null ? item.getPrecio() : 0.0;
                double total = item.getCantidad() * precio;
                totalGeneral += total;
                totalCantidad += item.getCantidad();

                Color rowBg = alternar ? LIGHT_GRAY : ColorConstants.WHITE;
                alternar = !alternar;

                table.addCell(crearCeldaTabla(String.valueOf(num++), fontNormal, TextAlignment.CENTER, rowBg));
                table.addCell(crearCeldaTabla(item.getTrabajadorNombre() != null ? item.getTrabajadorNombre() : "", fontNormal, TextAlignment.LEFT, rowBg));
                table.addCell(crearCeldaTabla(String.valueOf(item.getCantidad()), fontNormal, TextAlignment.CENTER, rowBg));
                table.addCell(crearCeldaTabla(String.format("$%.2f", precio), fontNormal, TextAlignment.RIGHT, rowBg));
                table.addCell(crearCeldaTabla(String.format("$%.2f", total), fontNormal, TextAlignment.RIGHT, rowBg));
            }
        }

        document.add(table);

        // Totales
        Table totalTable = new Table(UnitValue.createPercentArray(new float[]{55, 15, 15, 15}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(0);

        totalTable.addCell(new Cell()
                .add(new Paragraph("TOTALES").setFont(fontBold).setFontSize(8).setFontColor(ColorConstants.WHITE))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setBackgroundColor(HEADER_BG)
                .setPadding(3));
        totalTable.addCell(new Cell()
                .add(new Paragraph(String.valueOf(totalCantidad)).setFont(fontBold).setFontSize(8).setFontColor(ColorConstants.WHITE))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setBackgroundColor(HEADER_BG)
                .setPadding(3));
        totalTable.addCell(new Cell()
                .add(new Paragraph("").setFont(fontBold).setFontSize(8))
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setBackgroundColor(HEADER_BG)
                .setPadding(3));
        totalTable.addCell(new Cell()
                .add(new Paragraph(String.format("$%.2f", totalGeneral)).setFont(fontBold).setFontSize(8).setFontColor(ColorConstants.WHITE))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setBackgroundColor(HEADER_BG)
                .setPadding(3));

        document.add(totalTable);
    }

    private Paragraph crearLineaInfo(String etiqueta, String valor, PdfFont font) {
        return new Paragraph(etiqueta + " " + (valor != null ? valor : ""))
                .setFont(font)
                .setFontSize(8)
                .setMarginBottom(1);
    }

    private Cell crearCeldaTabla(String texto, PdfFont font, TextAlignment alignment, Color bgColor) {
        return new Cell()
                .add(new Paragraph(texto != null ? texto : "").setFont(font).setFontSize(8))
                .setTextAlignment(alignment)
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setBackgroundColor(bgColor)
                .setPadding(3);
    }

    private Cell celdaEncabezado(String texto, PdfFont font) {
        return new Cell().add(new Paragraph(texto).setFont(font).setFontSize(8))
                .setBackgroundColor(HEADER_BG).setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(BORDER_COLOR, 1)).setPadding(4);
    }

    private String productoCode(SalidaDto salida, ItemSalidaDto item) {
        return item.getProductoCode() != null ? item.getProductoCode() : salida.getProductoCode();
    }

    private String productoName(SalidaDto salida, ItemSalidaDto item) {
        return item.getProductoName() != null ? item.getProductoName() : salida.getProductoName();
    }

    private String unidadMedida(SalidaDto salida, ItemSalidaDto item) {
        return item.getUnidadMedida() != null ? item.getUnidadMedida()
                : (salida.getUnidadMedida() != null ? salida.getUnidadMedida() : "UND");
    }

    private String valor(String texto) {
        return texto != null ? texto : "";
    }

    private static class ResumenProducto {
        private final String codigo;
        private final String nombre;
        private final String unidadMedida;
        private int cantidad;
        private double importe;

        private ResumenProducto(String codigo, String nombre, String unidadMedida) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.unidadMedida = unidadMedida;
        }
    }

    private Cell crearCeldaFirma(String titulo, PdfFont fontBold, PdfFont fontNormal) {
        Cell cell = new Cell()
                .setBorder(new SolidBorder(BORDER_COLOR, 0.5f))
                .setPadding(6)
                .setBackgroundColor(LIGHT_GRAY);

        // Título centrado
        cell.add(new Paragraph(titulo)
                .setFont(fontBold)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(8));

        // Campos de firma
        cell.add(new Paragraph("Nombre: ____________________")
                .setFont(fontNormal)
                .setFontSize(7)
                .setMarginBottom(4));
        cell.add(new Paragraph("Cargo: _____________________")
                .setFont(fontNormal)
                .setFontSize(7)
                .setMarginBottom(4));
        cell.add(new Paragraph("Firma: _____________________")
                .setFont(fontNormal)
                .setFontSize(7)
                .setMarginBottom(4));
        cell.add(new Paragraph("CI: ________________________")
                .setFont(fontNormal)
                .setFontSize(7));

        return cell;
    }

    private String formatDestino(String destino) {
        return switch (destino) {
            case "TRABAJADORES" -> "Trabajadores";
            case "COMEDOR" -> "Comedor";
            case "VENTA_ESTADO" -> "Venta Estado";
            case "POBLACION" -> "Población";
            case "INSUMO" -> "Insumo";
            case "OTROS" -> "Otros";
            default -> destino;
        };
    }
}
