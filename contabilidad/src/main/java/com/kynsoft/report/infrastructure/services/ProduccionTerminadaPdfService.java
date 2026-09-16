package com.kynsoft.report.infrastructure.services;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.dto.ConfiguracionEmpresaDto;
import com.kynsoft.report.domain.dto.ProductoDto;
import com.kynsoft.report.domain.dto.ProduccionTerminadaDto;
import com.kynsoft.report.domain.services.IAlmacenFincaProductoService;
import com.kynsoft.report.domain.services.IConfiguracionEmpresaService;
import com.kynsoft.report.domain.services.IProductoService;
import com.kynsoft.report.domain.services.IProduccionTerminadaService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

/**
 * Genera el modelo SC-2-06, Entrega de Productos Terminados al Almacén.
 *
 * <p>La generación es únicamente una representación imprimible del documento
 * existente: no crea movimientos, no modifica existencias y no registra
 * operaciones contables.</p>
 */
@Service
public class ProduccionTerminadaPdfService {

    private static final DeviceRgb HEADER_BACKGROUND = new DeviceRgb(230, 230, 230);
    private static final DeviceRgb BORDER_COLOR = new DeviceRgb(100, 100, 100);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat QUANTITY_FORMAT = new DecimalFormat(
            "#,##0.####", DecimalFormatSymbols.getInstance(Locale.US));
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat(
            "#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));

    private final IProduccionTerminadaService produccionTerminadaService;
    private final IConfiguracionEmpresaService configuracionEmpresaService;
    private final IAlmacenFincaProductoService almacenFincaProductoService;
    private final IProductoService productoService;

    public ProduccionTerminadaPdfService(
            IProduccionTerminadaService produccionTerminadaService,
            IConfiguracionEmpresaService configuracionEmpresaService,
            IAlmacenFincaProductoService almacenFincaProductoService,
            IProductoService productoService) {
        this.produccionTerminadaService = produccionTerminadaService;
        this.configuracionEmpresaService = configuracionEmpresaService;
        this.almacenFincaProductoService = almacenFincaProductoService;
        this.productoService = productoService;
    }

    /**
     * Obtiene la producción y genera su modelo oficial de consulta.
     */
    public byte[] generar(UUID produccionId) throws Exception {
        ProduccionTerminadaDto produccion = produccionTerminadaService.findById(produccionId);
        ConfiguracionEmpresaDto empresa = configuracionEmpresaService.findActive()
                .orElseGet(ConfiguracionEmpresaDto::new);
        ProductoDto producto = productoService.findById(produccion.getProductoId());
        AlmacenFincaProductoDto almacenProducto = obtenerAlmacen(produccion.getAlmacenFincaProductoId());
        return generar(produccion, empresa, producto, almacenProducto);
    }

    /**
     * Variante separada para pruebas y para reutilización sin acceso a la base de datos.
     */
    public byte[] generar(ProduccionTerminadaDto produccion, ConfiguracionEmpresaDto empresa,
                          ProductoDto producto, AlmacenFincaProductoDto almacenProducto) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document(new PdfDocument(new PdfWriter(output)), PageSize.LETTER);
        document.setMargins(24, 25, 28, 25);

        PdfFont normal = PdfFontFactory.createFont("Helvetica");
        PdfFont bold = PdfFontFactory.createFont("Helvetica-Bold");

        document.add(cabecera(produccion, empresa, almacenProducto, normal, bold));
        document.add(tablaProductos(produccion, producto, almacenProducto, normal, bold));
        document.add(observaciones(produccion, normal, bold));
        document.add(firmas(produccion, normal, bold));
        document.add(new Paragraph("Modelo SC-2-06 — Entrega de Productos Terminados al Almacén. "
                + "Referencia: Resolución No. 11/2007 del MFP.")
                .setFont(normal).setFontSize(7).setFontColor(new DeviceRgb(80, 80, 80))
                .setTextAlignment(TextAlignment.CENTER).setMarginTop(10));

        document.close();
        return output.toByteArray();
    }

    private AlmacenFincaProductoDto obtenerAlmacen(UUID almacenFincaProductoId) {
        if (almacenFincaProductoId == null) {
            return null;
        }
        try {
            return almacenFincaProductoService.findById(almacenFincaProductoId);
        } catch (RuntimeException ignored) {
            // Producciones históricas pueden no estar ligadas a un almacén. El PDF debe
            // continuar disponible y expresar la ausencia de esa referencia.
            return null;
        }
    }

    private Table cabecera(ProduccionTerminadaDto produccion, ConfiguracionEmpresaDto empresa,
                           AlmacenFincaProductoDto almacenProducto, PdfFont normal, PdfFont bold) {
        Table header = new Table(UnitValue.createPercentArray(new float[]{62, 38}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginBottom(10);

        Cell entity = borderedCell();
        entity.add(infoLine("Entidad:", safe(empresa.getNombre()), normal, bold));
        entity.add(infoLine("Código:", safe(empresa.getCodigo()), normal, bold));
        entity.add(infoLine("Dirección:", safe(empresa.getDireccionCompleta()), normal, bold));
        entity.add(infoLine("Área productora:", finca(produccion), normal, bold));
        entity.add(infoLine("Almacén receptor:", almacen(produccion, almacenProducto), normal, bold));
        header.addCell(entity);

        Cell model = borderedCell().setTextAlignment(TextAlignment.CENTER);
        model.add(new Paragraph("MODELO SC-2-06").setFont(bold).setFontSize(11).setMarginBottom(4));
        model.add(new Paragraph("ENTREGA DE PRODUCTOS\nTERMINADOS AL ALMACÉN")
                .setFont(bold).setFontSize(10).setMarginBottom(8));
        model.add(infoLine("Consecutivo:", consecutivo(produccion), normal, bold)
                .setTextAlignment(TextAlignment.LEFT));
        model.add(infoLine("Fecha:", produccion.getFecha() != null
                ? produccion.getFecha().format(DATE_FORMAT) : "", normal, bold)
                .setTextAlignment(TextAlignment.LEFT));
        header.addCell(model);
        return header;
    }

    private Table tablaProductos(ProduccionTerminadaDto produccion, ProductoDto producto,
                                 AlmacenFincaProductoDto almacenProducto, PdfFont normal, PdfFont bold) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{10, 28, 8, 12, 12, 11, 11, 8}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginBottom(9);
        String[] headers = {"Código", "Producto", "U/M", "Cant.\nentregada", "Cant.\nrecibida",
                "Costo\nunitario", "Importe", "Saldo"};
        for (String value : headers) {
            table.addHeaderCell(borderedCell().setBackgroundColor(HEADER_BACKGROUND).setPadding(4)
                    .add(new Paragraph(value).setFont(bold).setFontSize(7).setTextAlignment(TextAlignment.CENTER)));
        }

        double cantidad = value(produccion.getCantidadTerminada());
        Double costo = produccion.getCostoUnitario();
        Double importe = produccion.getImporte();
        String unidad = firstNotBlank(produccion.getUnidadMedidaSnapshot(),
                producto != null && producto.getUnidadMedida() != null ? producto.getUnidadMedida().name() : null);
        String codigo = firstNotBlank(produccion.getProductoCodigoSnapshot(),
                firstNotBlank(produccion.getProductoCode(), producto != null ? producto.getCode() : null));
        String nombre = firstNotBlank(produccion.getProductoNombreSnapshot(),
                firstNotBlank(produccion.getProductoName(), producto != null ? producto.getName() : null));
        // Solo se muestra un saldo congelado. Consultar el saldo actual del
        // almacén convertiría un comprobante histórico en una cifra falsa.
        String saldo = produccion.getSaldoPosterior() == null ? "No registrado" : quantity(produccion.getSaldoPosterior());
        String costoTexto = costo == null ? "No registrado" : money(costo);
        String importeTexto = importe == null ? "No registrado" : money(importe);
        String[] values = {codigo, nombre, unidad, quantity(cantidad), quantity(cantidad), costoTexto, importeTexto, saldo};
        for (int index = 0; index < values.length; index++) {
            TextAlignment alignment = index == 1 ? TextAlignment.LEFT : TextAlignment.CENTER;
            if (index == 5 || index == 6 || index == 7) alignment = TextAlignment.RIGHT;
            table.addCell(borderedCell().setPadding(4)
                    .add(new Paragraph(values[index]).setFont(normal).setFontSize(8).setTextAlignment(alignment)));
        }

        Cell total = new Cell(1, 6).setBorder(new SolidBorder(BORDER_COLOR, 0.6f)).setPadding(4);
        total.add(new Paragraph("TOTAL").setFont(bold).setFontSize(8).setTextAlignment(TextAlignment.RIGHT));
        table.addCell(total);
        table.addCell(borderedCell().setPadding(4)
                .add(new Paragraph(importeTexto).setFont(bold).setFontSize(8).setTextAlignment(TextAlignment.RIGHT)));
        table.addCell(borderedCell().setPadding(4)
                .add(new Paragraph(saldo).setFont(bold).setFontSize(8).setTextAlignment(TextAlignment.RIGHT)));
        return table;
    }

    private Table observaciones(ProduccionTerminadaDto produccion, PdfFont normal, PdfFont bold) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{34, 33, 33}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginBottom(12);
        String referencia = firstNotBlank(produccion.getLote(), "No registrado");
        table.addCell(borderedCell().setPadding(5)
                .add(infoLine("Orden/lote (referencia):", referencia, normal, bold)));
        table.addCell(borderedCell().setPadding(5)
                .add(infoLine("Centro de costo:", firstNotBlank(produccion.getCentroCosto(), "No registrado"), normal, bold)));
        table.addCell(borderedCell().setPadding(5)
                .add(infoLine("Observaciones:", safe(produccion.getObservaciones()), normal, bold)));
        return table;
    }

    private Table firmas(ProduccionTerminadaDto produccion, PdfFont normal, PdfFont bold) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}))
                .setWidth(UnitValue.createPercentValue(100));
        signatureCell(table, "ENTREGADO POR", produccion.getTrabajadorEntregaNombre(), normal, bold);
        signatureCell(table, "RECIBIDO POR", produccion.getTrabajadorRecibeNombre(), normal, bold);
        signatureCell(table, "CONTABILIDAD", "", normal, bold);
        signatureCell(table, "CONTROL DE INVENTARIO", "", normal, bold);
        return table;
    }

    private void signatureCell(Table table, String role, String name, PdfFont normal, PdfFont bold) {
        Cell cell = borderedCell().setMinHeight(74).setPadding(5);
        cell.add(new Paragraph(role).setFont(bold).setFontSize(7).setTextAlignment(TextAlignment.CENTER));
        cell.add(new Paragraph("\n\n____________________________").setFont(normal).setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER));
        cell.add(new Paragraph(safe(name)).setFont(normal).setFontSize(7).setTextAlignment(TextAlignment.CENTER));
        cell.add(new Paragraph("Firma y fecha").setFont(normal).setFontSize(6).setTextAlignment(TextAlignment.CENTER));
        table.addCell(cell);
    }

    private Cell borderedCell() {
        return new Cell().setBorder(new SolidBorder(BORDER_COLOR, 0.6f));
    }

    private Paragraph infoLine(String label, String content, PdfFont normal, PdfFont bold) {
        return new Paragraph().setMargin(0).setFontSize(8)
                .add(new Text(label + " ").setFont(bold))
                .add(new Text(safe(content)).setFont(normal));
    }

    private String finca(ProduccionTerminadaDto produccion) {
        String code = safe(produccion.getFincaCode());
        String name = safe(produccion.getFincaName());
        return code.isBlank() ? name : (name.isBlank() ? code : code + " - " + name);
    }

    private String almacen(ProduccionTerminadaDto produccion, AlmacenFincaProductoDto almacenProducto) {
        String nombreSnapshot = safe(produccion.getAlmacenNombreSnapshot());
        String inventarioSnapshot = safe(produccion.getAlmacenInventarioSnapshot());
        if (!nombreSnapshot.isBlank() || !inventarioSnapshot.isBlank()) {
            return nombreSnapshot.isBlank() ? inventarioSnapshot
                    : (inventarioSnapshot.isBlank() ? nombreSnapshot : nombreSnapshot + " (" + inventarioSnapshot + ")");
        }
        if (almacenProducto == null) return "Sin almacén asociado";
        String nombre = safe(almacenProducto.getAlmacenNombre());
        String inventario = safe(almacenProducto.getAlmacenInventario());
        return nombre.isBlank() ? inventario : (inventario.isBlank() ? nombre : nombre + " (" + inventario + ")");
    }

    private String consecutivo(ProduccionTerminadaDto produccion) {
        return firstNotBlank(produccion.getNumeroDocumento(), "SIN-NÚMERO HISTÓRICO");
    }

    private String quantity(double value) {
        return QUANTITY_FORMAT.format(value);
    }

    private String money(double value) {
        return MONEY_FORMAT.format(value);
    }

    private double value(Double number) {
        return number == null ? 0d : number;
    }

    private String firstNotBlank(String first, String second) {
        return first != null && !first.isBlank() ? first : safe(second);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
