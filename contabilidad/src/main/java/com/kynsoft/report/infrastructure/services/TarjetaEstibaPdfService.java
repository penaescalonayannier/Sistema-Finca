package com.kynsoft.report.infrastructure.services;

import com.itextpdf.kernel.colors.ColorConstants;
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
import com.kynsoft.report.domain.dto.KardexDto;
import com.kynsoft.report.domain.services.IAlmacenFincaProductoService;
import com.kynsoft.report.domain.services.IConfiguracionEmpresaService;
import com.kynsoft.report.domain.services.IMovimientoStockService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Modelo SC-2-14: Tarjeta de Estiba.
 *
 * <p>La tarjeta se genera a partir del kardex ya registrado. Es una consulta
 * documental: no crea movimientos, no actualiza existencias y no contabiliza.</p>
 */
@Service
public class TarjetaEstibaPdfService {

    private static final DeviceRgb HEADER_BACKGROUND = new DeviceRgb(220, 230, 240);
    private static final DeviceRgb BORDER_COLOR = new DeviceRgb(90, 90, 90);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DecimalFormat QUANTITY_FORMAT = new DecimalFormat(
            "#,##0.####", DecimalFormatSymbols.getInstance(Locale.US));

    private final IMovimientoStockService movimientoStockService;
    private final IAlmacenFincaProductoService almacenFincaProductoService;
    private final IConfiguracionEmpresaService configuracionEmpresaService;

    public TarjetaEstibaPdfService(IMovimientoStockService movimientoStockService,
                                   IAlmacenFincaProductoService almacenFincaProductoService,
                                   IConfiguracionEmpresaService configuracionEmpresaService) {
        this.movimientoStockService = movimientoStockService;
        this.almacenFincaProductoService = almacenFincaProductoService;
        this.configuracionEmpresaService = configuracionEmpresaService;
    }

    public byte[] generar(UUID fincaProductoId, UUID almacenId, LocalDate fechaInicio, LocalDate fechaFin)
            throws Exception {
        validarPeriodo(fechaInicio, fechaFin);
        AlmacenFincaProductoDto almacenProducto = almacenFincaProductoService
                .findByAlmacenIdAndFincaProductoId(almacenId, fincaProductoId);
        KardexDto kardex = movimientoStockService.getKardex(fincaProductoId, almacenId,
                fechaInicio.atStartOfDay(), fechaFin.atTime(LocalTime.MAX));
        ConfiguracionEmpresaDto empresa = configuracionEmpresaService.findActive()
                .orElseGet(ConfiguracionEmpresaDto::new);
        return generar(kardex, almacenProducto, empresa, fechaInicio, fechaFin);
    }

    /** Genera la tarjeta física consolidada del producto en todos los almacenes de su finca. */
    public byte[] generarFinca(UUID fincaProductoId, LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        validarPeriodo(fechaInicio, fechaFin);
        KardexDto kardex = movimientoStockService.getKardex(fincaProductoId, null,
                fechaInicio.atStartOfDay(), fechaFin.atTime(LocalTime.MAX));
        ConfiguracionEmpresaDto empresa = configuracionEmpresaService.findActive()
                .orElseGet(ConfiguracionEmpresaDto::new);
        return generar(kardex, null, empresa, fechaInicio, fechaFin, true);
    }

    /** Variante sin acceso a datos, destinada a pruebas y reutilización. */
    public byte[] generar(KardexDto kardex, AlmacenFincaProductoDto almacenProducto,
                          ConfiguracionEmpresaDto empresa, LocalDate fechaInicio, LocalDate fechaFin)
            throws Exception {
        return generar(kardex, almacenProducto, empresa, fechaInicio, fechaFin, false);
    }

    private byte[] generar(KardexDto kardex, AlmacenFincaProductoDto almacenProducto,
                           ConfiguracionEmpresaDto empresa, LocalDate fechaInicio, LocalDate fechaFin,
                           boolean todaLaFinca) throws Exception {
        validarPeriodo(fechaInicio, fechaFin);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document(new PdfDocument(new PdfWriter(output)), PageSize.A4.rotate());
        document.setMargins(22, 22, 25, 22);

        PdfFont normal = PdfFontFactory.createFont("Helvetica");
        PdfFont bold = PdfFontFactory.createFont("Helvetica-Bold");
        document.add(cabecera(kardex, almacenProducto, empresa, fechaInicio, fechaFin, normal, bold, todaLaFinca));
        document.add(tablaMovimientos(kardex, normal, bold, todaLaFinca));
        document.add(resumen(kardex, normal, bold));
        document.add(firmas(normal, bold));
        document.add(new Paragraph("Modelo SC-2-14 — Tarjeta de Estiba. Resolución No. 11/2007 del MFP. "
                + "Documento de consulta; no modifica inventario ni contabilidad.")
                .setFont(normal).setFontSize(7).setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER).setMarginTop(10));
        document.close();
        return output.toByteArray();
    }

    private Table cabecera(KardexDto kardex, AlmacenFincaProductoDto almacenProducto,
                           ConfiguracionEmpresaDto empresa, LocalDate inicio, LocalDate fin,
                           PdfFont normal, PdfFont bold, boolean todaLaFinca) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{44, 30, 26}))
                .useAllAvailableWidth().setMarginBottom(10);
        Cell entity = celdaBorde();
        entity.add(linea("Entidad:", safe(empresa.getNombre()), normal, bold));
        entity.add(linea("Código:", safe(empresa.getCodigo()), normal, bold));
        entity.add(linea("Dirección:", safe(empresa.getDireccionCompleta()), normal, bold));
        entity.add(linea(todaLaFinca ? "Finca:" : "Almacén:",
                todaLaFinca ? finca(kardex) : almacen(almacenProducto, kardex), normal, bold));
        if (todaLaFinca) entity.add(linea("Almacenes:", "Todos los almacenes de la finca", normal, bold));
        table.addCell(entity);

        Cell producto = celdaBorde();
        producto.add(linea("Código producto:", codigo(kardex, almacenProducto), normal, bold));
        producto.add(linea("Producto:", nombre(kardex, almacenProducto), normal, bold));
        producto.add(linea("Unidad de medida:", unidad(almacenProducto, kardex), normal, bold));
        producto.add(linea("Ubicación:", "No registrado", normal, bold));
        producto.add(linea("Cuenta / subcuenta / análisis:", "No registrado", normal, bold));
        producto.add(linea("Período:", DATE_FORMAT.format(inicio) + " al " + DATE_FORMAT.format(fin), normal, bold));
        table.addCell(producto);

        Cell titulo = celdaBorde().setTextAlignment(TextAlignment.CENTER);
        titulo.add(new Paragraph("MODELO SC-2-14").setFont(bold).setFontSize(11).setMarginBottom(4));
        titulo.add(new Paragraph("TARJETA DE ESTIBA").setFont(bold).setFontSize(13).setMarginBottom(10));
        titulo.add(new Paragraph("Control físico de existencias").setFont(normal).setFontSize(8));
        table.addCell(titulo);
        return table;
    }

    private Table tablaMovimientos(KardexDto kardex, PdfFont normal, PdfFont bold, boolean todaLaFinca) {
        float[] columnas = todaLaFinca
                ? new float[]{10, 14, 15, 12, 21, 7, 7, 7, 7}
                : new float[]{11, 15, 13, 25, 9, 9, 9, 9};
        Table table = new Table(UnitValue.createPercentArray(columnas))
                .useAllAvailableWidth();
        String[] headers = todaLaFinca
                ? new String[]{"Fecha", "Tipo de movimiento", "Almacén que registró", "Documento / No.", "Concepto", "Entrada", "Salida", "Existencia", "Firma"}
                : new String[]{"Fecha", "Tipo de movimiento", "Documento / No.", "Concepto", "Entrada", "Salida", "Existencia", "Firma"};
        for (String header : headers) {
            table.addHeaderCell(celdaBorde().setBackgroundColor(HEADER_BACKGROUND).setPadding(4)
                    .setTextAlignment(TextAlignment.CENTER)
                    .add(new Paragraph(header).setFont(bold).setFontSize(7)));
        }
        List<KardexDto.MovimientoKardexDto> movimientos = kardex.getMovimientos() == null
                ? List.of() : kardex.getMovimientos();
        for (KardexDto.MovimientoKardexDto movimiento : movimientos) {
            celda(table, fecha(movimiento.getFecha()), normal, TextAlignment.CENTER);
            celda(table, tipo(movimiento), normal, TextAlignment.LEFT);
            if (todaLaFinca) celda(table, almacenMovimiento(movimiento), normal, TextAlignment.LEFT);
            celda(table, referencia(movimiento), normal, TextAlignment.LEFT);
            celda(table, concepto(movimiento), normal, TextAlignment.LEFT);
            celda(table, cantidad(movimiento.getEntrada()), normal, TextAlignment.RIGHT);
            celda(table, cantidad(movimiento.getSalida()), normal, TextAlignment.RIGHT);
            celda(table, cantidad(movimiento.getSaldo()), normal, TextAlignment.RIGHT);
            celda(table, "", normal, TextAlignment.CENTER);
        }
        if (movimientos.isEmpty()) {
            table.addCell(new Cell(1, todaLaFinca ? 9 : 8).setBorder(new SolidBorder(BORDER_COLOR, .5f))
                    .add(new Paragraph("No se registran movimientos del producto en el período seleccionado.")
                            .setFont(normal).setFontSize(8).setTextAlignment(TextAlignment.CENTER)));
        }
        return table;
    }

    private Table resumen(KardexDto kardex, PdfFont normal, PdfFont bold) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}))
                .useAllAvailableWidth().setMarginTop(10).setMarginBottom(14);
        agregarResumen(table, "Saldo inicial", kardex.getStockInicial(), normal, bold);
        agregarResumen(table, "Total entradas", kardex.getTotalEntradas(), normal, bold);
        agregarResumen(table, "Total salidas", kardex.getTotalSalidas(), normal, bold);
        agregarResumen(table, "Saldo final", kardex.getStockFinal(), normal, bold);
        return table;
    }

    private void agregarResumen(Table table, String etiqueta, Double valor, PdfFont normal, PdfFont bold) {
        table.addCell(celdaBorde().setBackgroundColor(HEADER_BACKGROUND).setTextAlignment(TextAlignment.CENTER)
                .add(new Paragraph(etiqueta).setFont(bold).setFontSize(8))
                .add(new Paragraph(cantidad(valor)).setFont(normal).setFontSize(11)));
    }

    private Table firmas(PdfFont normal, PdfFont bold) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                .useAllAvailableWidth().setMarginTop(8);
        firma(table, "Responsable del almacén", normal, bold);
        firma(table, "Control de inventario", normal, bold);
        firma(table, "Contabilidad", normal, bold);
        return table;
    }

    private void firma(Table table, String cargo, PdfFont normal, PdfFont bold) {
        table.addCell(new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER).setPaddingTop(22)
                .add(new Paragraph("_______________________________").setFont(normal).setFontSize(9))
                .add(new Paragraph(cargo).setFont(bold).setFontSize(8))
                .add(new Paragraph("Nombre, firma y fecha").setFont(normal).setFontSize(7)));
    }

    private Cell celdaBorde() {
        return new Cell().setBorder(new SolidBorder(BORDER_COLOR, .5f)).setPadding(4);
    }

    private void celda(Table table, String texto, PdfFont font, TextAlignment alignment) {
        table.addCell(celdaBorde().setTextAlignment(alignment)
                .add(new Paragraph(texto).setFont(font).setFontSize(7)));
    }

    private Paragraph linea(String etiqueta, String valor, PdfFont normal, PdfFont bold) {
        return new Paragraph().setFontSize(8).setMargin(0)
                .add(new Text(etiqueta + " ").setFont(bold))
                .add(new Text(valor).setFont(normal));
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null || fin.isBefore(inicio)) {
            throw new IllegalArgumentException("El período seleccionado para la tarjeta de estiba es inválido.");
        }
    }

    private String fecha(LocalDateTime fecha) { return fecha == null ? "" : DATE_TIME_FORMAT.format(fecha); }
    private String cantidad(Double valor) { return valor == null || Math.abs(valor) < .0000001d ? "" : QUANTITY_FORMAT.format(valor); }
    private String safe(String valor) { return valor == null ? "" : valor; }
    private String tipo(KardexDto.MovimientoKardexDto m) { return m.getTipoMovimiento() == null ? "" : m.getTipoMovimiento().name().replace('_', ' '); }
    private String concepto(KardexDto.MovimientoKardexDto m) {
        return !safe(m.getDescripcion()).isBlank() ? m.getDescripcion() : safe(m.getObservaciones());
    }
    private String referencia(KardexDto.MovimientoKardexDto m) {
        String tabla = safe(m.getReferenciaTabla()).replace('_', ' ');
        String id = m.getReferenciaId() == null ? "" : m.getReferenciaId().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        return tabla.isBlank() ? id : tabla + (id.isBlank() ? "" : " #" + id);
    }
    private String almacen(AlmacenFincaProductoDto afp, KardexDto kardex) {
        if (afp != null) return safe(afp.getAlmacenNombre()) + (safe(afp.getAlmacenInventario()).isBlank() ? "" : " (" + afp.getAlmacenInventario() + ")");
        return kardex.getAlmacen() == null ? "" : safe(kardex.getAlmacen().getAlmacenNombre());
    }
    private String codigo(KardexDto kardex, AlmacenFincaProductoDto afp) {
        return afp != null ? safe(afp.getProductoCode()) : kardex.getProducto() == null ? "" : safe(kardex.getProducto().getProductoCode());
    }
    private String nombre(KardexDto kardex, AlmacenFincaProductoDto afp) {
        return afp != null ? safe(afp.getProductoName()) : kardex.getProducto() == null ? "" : safe(kardex.getProducto().getProductoName());
    }
    private String unidad(AlmacenFincaProductoDto afp, KardexDto kardex) {
        if (afp != null && afp.getUnidadMedida() != null) return afp.getUnidadMedida().name();
        return kardex.getProducto() == null ? "" : safe(kardex.getProducto().getUnidadMedida());
    }
    private String finca(KardexDto kardex) {
        if (kardex.getProducto() == null) return "";
        String codigo = safe(kardex.getProducto().getFincaCode());
        String nombre = safe(kardex.getProducto().getFincaName());
        return (codigo + (codigo.isBlank() || nombre.isBlank() ? "" : " - ") + nombre).trim();
    }
    private String almacenMovimiento(KardexDto.MovimientoKardexDto movimiento) {
        if (movimiento.getAlmacenId() == null) return "Sin almacén asociado";
        String nombre = safe(movimiento.getAlmacenNombre());
        String inventario = safe(movimiento.getAlmacenInventario());
        return nombre.isBlank() ? "Almacén no disponible" : nombre + (inventario.isBlank() ? "" : " (" + inventario + ")");
    }
}
