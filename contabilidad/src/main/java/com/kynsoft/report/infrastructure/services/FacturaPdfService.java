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
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.kynsoft.report.domain.dto.ConfiguracionEmpresaDto;
import com.kynsoft.report.domain.dto.DestinoSalida;
import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.dto.ItemSalidaDto;
import com.kynsoft.report.domain.dto.SalidaDto;
import com.kynsoft.report.domain.services.IAlmacenFincaProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.StringJoiner;

/**
 * Servicio para generación de documentos PDF según modelos oficiales cubanos.
 * Implementa los formatos SC-2-08 (Vale de Entrega o Devolución) y SC-2-12 (Factura)
 * según Resolución 11/2007 y Resolución 55/2021 del MFP.
 */
@Service
public class FacturaPdfService {

    // Paleta sobria para documentos de control: debe imprimirse bien también en escala de grises.
    private static final DeviceRgb HEADER_BG = new DeviceRgb(27, 92, 82);
    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(244, 247, 246);
    private static final DeviceRgb BORDER_COLOR = new DeviceRgb(174, 190, 185);
    private static final DeviceRgb TEXT_DARK = new DeviceRgb(51, 51, 51);

    private IAlmacenFincaProductoService almacenFincaProductoService;

    /**
     * Se inyecta por setter para mantener la generación PDF utilizable en pruebas unitarias
     * puras. La consulta solo complementa la impresión con el almacén y su saldo actual.
     */
    @Autowired
    public void setAlmacenFincaProductoService(IAlmacenFincaProductoService almacenFincaProductoService) {
        this.almacenFincaProductoService = almacenFincaProductoService;
    }

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
        document.add(crearContenidoVale(salida, empresa, fontBold, fontNormal));

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
                double cantidad = item.getCantidad() != null ? item.getCantidad() : 0;
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
                double cantidad = item.getCantidad() != null ? item.getCantidad() : 0;
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
            Div contenidoVale = crearContenidoVale(salida, empresa, fontBold, fontNormal);
            contenidoVale.setKeepTogether(true);
            document.add(contenidoVale);
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

    private Div crearContenidoVale(SalidaDto salida, ConfiguracionEmpresaDto empresa,
                                   PdfFont fontBold, PdfFont fontNormal) {
        Div contenido = new Div();
        Map<UUID, AlmacenFincaProductoDto> almacenes = consultarAlmacenes(salida);

        boolean esVale = salida.getTipo() == null || salida.getTipo().name().equals("VALE");
        String tipoDoc = esVale ? "VALE DE ENTREGA O DEVOLUCIÓN" : "FACTURA";
        String modeloRef = esVale ? "Modelo SC-2-08" : "Modelo SC-2-12";

        // Referencia al modelo oficial (esquina superior derecha)
        Paragraph modeloParagraph = new Paragraph(modeloRef + "  |  Documento de control interno")
                .setFont(fontNormal)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontColor(TEXT_DARK)
                .setMarginBottom(2);
        contenido.add(modeloParagraph);

        // Título
        Paragraph titulo = new Paragraph(tipoDoc)
                .setFont(fontBold)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        contenido.add(titulo);

        // Número y fecha
        String fecha = salida.getFecha() != null
                ? salida.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "";
        Paragraph numeroFecha = new Paragraph("CONSECUTIVO: " + textoSeguro(salida.getNumero(), "SIN NÚMERO")
                + "     FECHA: " + fecha)
                .setFont(fontNormal)
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        contenido.add(numeroFecha);

        if (esVale) {
            contenido.add(new Paragraph("OPERACIÓN:  [ X ] ENTREGA     [   ] DEVOLUCIÓN")
                    .setFont(fontBold)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(-4)
                    .setMarginBottom(8));
        }

        // Tabla de cabecera (Suministrador y Receptor)
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(10);

        // Suministrador (datos obligatorios según Res. 55/2021)
        Cell suministradorCell = new Cell()
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setPadding(6);
        suministradorCell.add(new Paragraph("SUMINISTRADOR").setFont(fontBold).setFontSize(9).setMarginBottom(3));
        Table datosSuministrador = crearTablaDatosDosColumnas();
        datosSuministrador.addCell(crearCeldaDato("Entidad:", empresa.getNombre(), fontNormal));
        datosSuministrador.addCell(crearCeldaDato("Dirección:", empresa.getDireccionCompleta(), fontNormal));
        datosSuministrador.addCell(crearCeldaDato("Código:", empresa.getCodigo(), fontNormal));
        datosSuministrador.addCell(crearCeldaDato("Cuenta:", empresa.getCuentaBancaria(), fontNormal));
        datosSuministrador.addCell(crearCeldaDato("NIT:", empresa.getNit(), fontNormal));
        datosSuministrador.addCell(crearCeldaDato("Área / Finca:", descripcionFinca(salida), fontNormal));
        suministradorCell.add(datosSuministrador);
        headerTable.addCell(suministradorCell);

        // Receptor
        Cell receptorCell = new Cell()
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setPadding(6);
        receptorCell.add(new Paragraph("RECEPTOR").setFont(fontBold).setFontSize(9).setMarginBottom(3));
        Table datosReceptor = crearTablaDatosDosColumnas();
        String destino = salida.getDestino() != null ? formatDestino(salida.getDestino().name()) : "No especificado";
        datosReceptor.addCell(crearCeldaDato("Destino:", destino, fontNormal));
        datosReceptor.addCell(crearCeldaDato("Receptor:", destino, fontNormal));
        datosReceptor.addCell(crearCeldaDato("Código:", "________________", fontNormal));
        datosReceptor.addCell(crearCeldaDato("Área / Dirección:", "________________", fontNormal));
        receptorCell.add(datosReceptor);
        headerTable.addCell(receptorCell);

        contenido.add(headerTable);

        // Datos de trazabilidad previstos por el SC-2-08. Los registros históricos que no
        // están enlazados a almacén se identifican explícitamente, nunca se les inventa uno.
        Table trazabilidad = new Table(UnitValue.createPercentArray(new float[]{34, 33, 33}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginBottom(8);
        trazabilidad.addCell(crearCeldaTrazabilidad("ALMACÉN EMISOR", nombresAlmacenes(almacenes), "Inventario: "
                + inventariosAlmacenes(almacenes), fontBold, fontNormal));
        trazabilidad.addCell(crearCeldaTrazabilidad("ÁREA / CENTRO DE COSTO", descripcionFinca(salida), "Código: "
                + textoSeguro(salida.getFincaCode(), "--"), fontBold, fontNormal));
        trazabilidad.addCell(crearCeldaTrazabilidad("RECEPTOR / DESTINO", destino, "Almacén receptor: No informado", fontBold, fontNormal));
        contenido.add(trazabilidad);

        // Tabla de items
        Table itemsTable = new Table(UnitValue.createPercentArray(new float[]{5, 9, 27, 9, 10, 10, 12, 18}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(0);

        // Encabezados según modelo oficial
        String[] headers = {"No.", "Código", "Descripción", "U/M", "Cantidad", "Precio unit.", "Importe", "Saldo actual"};
        for (String header : headers) {
            itemsTable.addHeaderCell(new Cell()
                    .add(new Paragraph(header).setFont(fontBold).setFontSize(8))
                    .setBackgroundColor(HEADER_BG)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(new SolidBorder(BORDER_COLOR, 1))
                    .setPadding(4));
        }

        List<ItemSalidaDto> items = salida.getItems();
        double totalGeneral = 0.0;
        double totalCantidad = 0.0;

        if (items != null && !items.isEmpty()) {
            for (ItemSalidaDto item : items) {
                double precio = item.getPrecio() != null ? item.getPrecio() : 0.0;
                double cantidad = cantidad(item);
                totalGeneral += cantidad * precio;
                totalCantidad += cantidad;
            }
        }

        if (items != null && !items.isEmpty()) {
            int consecutivo = 1;
            for (ItemSalidaDto item : items) {
                double cantidad = cantidad(item);
                double precio = item.getPrecio() != null ? item.getPrecio() : 0.0;
                Color fondo = consecutivo % 2 == 0 ? LIGHT_GRAY : ColorConstants.WHITE;
                itemsTable.addCell(crearCeldaTabla(String.valueOf(consecutivo++), fontNormal, TextAlignment.CENTER, fondo));
                itemsTable.addCell(crearCeldaTabla(productoCode(salida, item), fontNormal, TextAlignment.CENTER, fondo));
                itemsTable.addCell(crearCeldaTabla(productoName(salida, item), fontNormal, TextAlignment.LEFT, fondo));
                itemsTable.addCell(crearCeldaTabla(unidadMedida(salida, item), fontNormal, TextAlignment.CENTER, fondo));
                itemsTable.addCell(crearCeldaTabla(formatearCantidad(cantidad), fontNormal, TextAlignment.CENTER, fondo));
                itemsTable.addCell(crearCeldaTabla(formatearImporte(precio), fontNormal, TextAlignment.RIGHT, fondo));
                itemsTable.addCell(crearCeldaTabla(formatearImporte(cantidad * precio), fontNormal, TextAlignment.RIGHT, fondo));
                AlmacenFincaProductoDto almacen = obtenerAlmacen(salida, item, almacenes);
                itemsTable.addCell(crearCeldaTabla(almacen == null ? "No asociado" : formatearCantidad(stock(almacen)),
                        fontNormal, TextAlignment.CENTER, fondo));
            }
        }

        // El modelo debe dejar visible tanto la cantidad entregada como el importe total.
        itemsTable.addCell(new Cell(1, 4)
                .add(new Paragraph("TOTAL").setFont(fontBold).setFontSize(8))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setBackgroundColor(LIGHT_GRAY).setPadding(3));
        itemsTable.addCell(new Cell()
                .add(new Paragraph(formatearCantidad(totalCantidad)).setFont(fontBold).setFontSize(8))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setBackgroundColor(LIGHT_GRAY).setPadding(3));
        itemsTable.addCell(new Cell()
                .add(new Paragraph("").setFont(fontBold).setFontSize(8))
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setBackgroundColor(LIGHT_GRAY).setPadding(3));
        itemsTable.addCell(new Cell()
                .add(new Paragraph(formatearImporte(totalGeneral)).setFont(fontBold).setFontSize(8))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setBackgroundColor(LIGHT_GRAY).setPadding(3));
        itemsTable.addCell(new Cell()
                .add(new Paragraph("").setFont(fontBold).setFontSize(8))
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setBackgroundColor(LIGHT_GRAY).setPadding(3));

        contenido.add(itemsTable);

        String observaciones = textoSeguro(salida.getObservaciones(), "Sin observaciones.");
        Table referencias = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginTop(5).setMarginBottom(3);
        referencias.addCell(crearCeldaDato("Lote / orden:", textoSeguro(salida.getObservaciones(), "No informado"), fontNormal)
                .setBorder(new SolidBorder(BORDER_COLOR, .5f)).setPadding(3));
        referencias.addCell(crearCeldaDato("Observaciones:", observaciones, fontNormal)
                .setBorder(new SolidBorder(BORDER_COLOR, .5f)).setPadding(3));
        contenido.add(referencias);

        // Firmas de control previstas por el modelo: despacho, recepción, inventario y contabilidad.
        Table firmasTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(0);

        firmasTable.addCell(crearCeldaFirma("Despachado", fontBold, fontNormal));
        firmasTable.addCell(crearCeldaFirma("Recibido", fontBold, fontNormal));
        firmasTable.addCell(crearCeldaFirma("Control de Inventario", fontBold, fontNormal));
        firmasTable.addCell(crearCeldaFirma("Contabilizado", fontBold, fontNormal));

        contenido.add(firmasTable);
        contenido.add(new Paragraph(modeloRef + " · La emisión o descarga de este documento no modifica existencias ni contabilidad.")
                .setFont(fontNormal).setFontSize(6.5f).setFontColor(TEXT_DARK)
                .setTextAlignment(TextAlignment.CENTER).setMarginTop(4).setMarginBottom(0));
        return contenido;
    }

    private void generarPaginaTrabajadores(Document document, SalidaDto salida, PdfFont fontBold, PdfFont fontNormal) {
        // Separador
        document.add(new Paragraph("\n").setMarginTop(10));

        // Título
        Paragraph titulo = new Paragraph("DETALLE DE COMPRAS DE TRABAJADORES")
                .setFont(fontBold)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(3);
        document.add(titulo);

        // Subtítulo con info de la salida
        String tipoDoc = (salida.getTipo() == null || salida.getTipo().name().equals("VALE")) ? "Vale" : "Factura";
        String destinoStr = salida.getDestino() != null ? formatDestino(salida.getDestino().name()) : "";
        Paragraph subtitulo = new Paragraph(tipoDoc + ": " + salida.getNumero() + " | Destino: " + destinoStr)
                .setFont(fontNormal)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(6);
        document.add(subtitulo);

        // Tabla de trabajadores
        Table table = new Table(UnitValue.createPercentArray(new float[]{6, 24, 27, 11, 11, 13, 8}))
                .setWidth(UnitValue.createPercentValue(100));

        // Encabezados
        String[] headers = {"No.", "Producto", "Trabajador", "Cantidad", "Precio", "Total", "Estado"};
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
        double totalCantidad = 0d;

        if (items != null) {
            int num = 1;
            boolean alternar = false;
            for (ItemSalidaDto item : items) {
                double precio = item.getPrecio() != null ? item.getPrecio() : 0.0;
                double cantidad = cantidad(item);
                double total = cantidad * precio;
                totalGeneral += total;
                totalCantidad += cantidad;

                Color rowBg = alternar ? LIGHT_GRAY : ColorConstants.WHITE;
                alternar = !alternar;

                table.addCell(crearCeldaTabla(String.valueOf(num++), fontNormal, TextAlignment.CENTER, rowBg));
                table.addCell(crearCeldaTabla(productoName(salida, item), fontNormal, TextAlignment.LEFT, rowBg));
                table.addCell(crearCeldaTabla(item.getTrabajadorNombre() != null ? item.getTrabajadorNombre() : "", fontNormal, TextAlignment.LEFT, rowBg));
                table.addCell(crearCeldaTabla(formatearCantidad(cantidad), fontNormal, TextAlignment.CENTER, rowBg));
                table.addCell(crearCeldaTabla(formatearImporte(precio), fontNormal, TextAlignment.RIGHT, rowBg));
                table.addCell(crearCeldaTabla(formatearImporte(total), fontNormal, TextAlignment.RIGHT, rowBg));
                table.addCell(crearCeldaTabla(Boolean.TRUE.equals(item.getPagado()) ? "Pagado" : "Deuda", fontNormal, TextAlignment.CENTER, rowBg));
            }
        }

        document.add(table);

        // Totales
        Table totalTable = new Table(UnitValue.createPercentArray(new float[]{57, 11, 11, 13, 8}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(0);

        totalTable.addCell(new Cell()
                .add(new Paragraph("TOTALES").setFont(fontBold).setFontSize(8).setFontColor(ColorConstants.WHITE))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setBackgroundColor(HEADER_BG)
                .setPadding(3));
        totalTable.addCell(new Cell()
                .add(new Paragraph(formatearCantidad(totalCantidad)).setFont(fontBold).setFontSize(8).setFontColor(ColorConstants.WHITE))
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
                .add(new Paragraph(formatearImporte(totalGeneral)).setFont(fontBold).setFontSize(8).setFontColor(ColorConstants.WHITE))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setBackgroundColor(HEADER_BG)
                .setPadding(3));
        totalTable.addCell(new Cell()
                .add(new Paragraph("").setFont(fontBold).setFontSize(8))
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setBackgroundColor(HEADER_BG)
                .setPadding(3));

        document.add(totalTable);
    }

    private Paragraph crearLineaInfo(String etiqueta, String valor, PdfFont font) {
        return new Paragraph()
                .setFont(font)
                .setFontSize(8)
                .setMarginBottom(1)
                .add(new Text(etiqueta + " ").setBold())
                .add(new Text(valor != null ? valor : ""));
    }

    private Table crearTablaDatosDosColumnas() {
        return new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100));
    }

    private Cell crearCeldaDato(String etiqueta, String valor, PdfFont font) {
        return new Cell()
                .setBorder(Border.NO_BORDER)
                .setPadding(1)
                .add(crearLineaInfo(etiqueta, valor, font));
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

    private double cantidad(ItemSalidaDto item) {
        return item.getCantidad() != null ? item.getCantidad() : 0d;
    }

    private String formatearCantidad(double cantidad) {
        if (Math.rint(cantidad) == cantidad) {
            return String.format(Locale.ROOT, "%.0f", cantidad);
        }
        return String.format(Locale.ROOT, "%.3f", cantidad)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
    }

    private String formatearImporte(double importe) {
        return String.format(Locale.ROOT, "$%.2f", importe);
    }

    private String textoSeguro(String valor, String sustituto) {
        return valor == null || valor.isBlank() ? sustituto : valor;
    }

    private String descripcionFinca(SalidaDto salida) {
        String codigo = valor(salida.getFincaCode()).trim();
        String nombre = valor(salida.getFincaName()).trim();
        String descripcion = (codigo + " " + nombre).trim();
        return descripcion.isEmpty() ? "No informado" : descripcion;
    }

    private Map<UUID, AlmacenFincaProductoDto> consultarAlmacenes(SalidaDto salida) {
        if (almacenFincaProductoService == null) {
            return Map.of();
        }
        Set<UUID> ids = new java.util.LinkedHashSet<>();
        if (salida.getAlmacenFincaProductoId() != null) {
            ids.add(salida.getAlmacenFincaProductoId());
        }
        if (salida.getItems() != null) {
            salida.getItems().stream()
                    .map(ItemSalidaDto::getAlmacenFincaProductoId)
                    .filter(java.util.Objects::nonNull)
                    .forEach(ids::add);
        }
        Map<UUID, AlmacenFincaProductoDto> resultado = new LinkedHashMap<>();
        for (UUID id : ids) {
            try {
                resultado.put(id, almacenFincaProductoService.findById(id));
            } catch (RuntimeException ignored) {
                // Un enlace eliminado o histórico no debe impedir imprimir el vale.
            }
        }
        return resultado;
    }

    private AlmacenFincaProductoDto obtenerAlmacen(SalidaDto salida, ItemSalidaDto item,
                                                    Map<UUID, AlmacenFincaProductoDto> almacenes) {
        UUID id = item.getAlmacenFincaProductoId() != null
                ? item.getAlmacenFincaProductoId()
                : salida.getAlmacenFincaProductoId();
        return id == null ? null : almacenes.get(id);
    }

    private String nombresAlmacenes(Map<UUID, AlmacenFincaProductoDto> almacenes) {
        if (almacenes.isEmpty()) {
            return "No asociado";
        }
        return almacenes.values().stream()
                .map(almacen -> textoSeguro(almacen.getAlmacenNombre(), "Sin nombre"))
                .distinct()
                .reduce((primero, siguiente) -> primero + ", " + siguiente)
                .orElse("No asociado");
    }

    private String inventariosAlmacenes(Map<UUID, AlmacenFincaProductoDto> almacenes) {
        if (almacenes.isEmpty()) {
            return "--";
        }
        return almacenes.values().stream()
                .map(almacen -> textoSeguro(almacen.getAlmacenInventario(), "--"))
                .distinct()
                .reduce((primero, siguiente) -> primero + ", " + siguiente)
                .orElse("--");
    }

    private double stock(AlmacenFincaProductoDto almacen) {
        return almacen.getStock() == null ? 0d : almacen.getStock();
    }

    private Cell crearCeldaTrazabilidad(String titulo, String valor, String detalle,
                                        PdfFont fontBold, PdfFont fontNormal) {
        Cell cell = new Cell().setBorder(new SolidBorder(BORDER_COLOR, .7f)).setPadding(4);
        cell.add(new Paragraph(titulo).setFont(fontBold).setFontSize(7).setFontColor(TEXT_DARK).setMarginBottom(2));
        cell.add(new Paragraph(textoSeguro(valor, "No informado")).setFont(fontNormal).setFontSize(8).setMarginBottom(1));
        cell.add(new Paragraph(detalle).setFont(fontNormal).setFontSize(7).setFontColor(TEXT_DARK).setMarginBottom(0));
        return cell;
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
